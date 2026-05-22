package com.example.parkify.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkify.domain.model.*
import com.example.parkify.domain.repository.IAdminRepository
import com.example.parkify.domain.repository.IAuthRepository
import com.example.parkify.domain.repository.IParqueoRepository
import com.example.parkify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AdminUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val usuarios: List<Usuario> = emptyList(),
    val vehiculosActivos: List<Vehiculo> = emptyList(),
    val historial: List<Vehiculo> = emptyList(),
    val tarifas: List<Tarifa> = emptyList(),
    val parqueadero: Parqueadero = Parqueadero(),
    val totalIngresosDia: Double = 0.0,
    val totalIngresosMes: Double = 0.0,
    val carrosDisponibles: Int = 0,
    val motosDisponibles: Int = 0
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepo: IAdminRepository,
    private val parqueoRepo: IParqueoRepository,
    private val authRepo: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observarUsuarios()
        observarVehiculos()
        observarDisponibilidad()
        cargarTarifas()
        cargarParqueadero()
        cargarReportes()
    }

    private fun observarUsuarios() {
        viewModelScope.launch {
            adminRepo.observarUsuarios().collect { lista ->
                _uiState.update { it.copy(usuarios = lista) }
            }
        }
    }

    private fun observarVehiculos() {
        viewModelScope.launch {
            parqueoRepo.observarVehiculosActivos().collect { lista ->
                _uiState.update { it.copy(vehiculosActivos = lista) }
            }
        }
    }

    private fun observarDisponibilidad() {
        viewModelScope.launch {
            parqueoRepo.observarDisponibilidad().collect { (c, m) ->
                _uiState.update { it.copy(carrosDisponibles = c, motosDisponibles = m) }
            }
        }
    }

    private fun cargarTarifas() {
        viewModelScope.launch {
            when (val r = parqueoRepo.getTarifas()) {
                is Resource.Success -> _uiState.update { it.copy(tarifas = r.data) }
                else -> {}
            }
        }
    }

    private fun cargarParqueadero() {
        viewModelScope.launch {
            when (val r = parqueoRepo.getParqueaderoInfo()) {
                is Resource.Success -> _uiState.update { it.copy(parqueadero = r.data) }
                else -> {}
            }
        }
    }

    private fun cargarReportes() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val hasta = cal.timeInMillis

            // Inicio del día de hoy (00:00:00)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val inicioDia = cal.timeInMillis

            // Inicio del mes actual (Día 1)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val inicioMes = cal.timeInMillis

            // NOTA: Asegúrate de que tus repositorios busquen por "horaSalida" internamente
            val dia = adminRepo.getReporteIngresos(inicioDia, hasta)
            val mes = adminRepo.getReporteIngresos(inicioMes, hasta)
            val hist = parqueoRepo.getHistorial(inicioMes, hasta)

            if (dia is Resource.Success) _uiState.update { it.copy(totalIngresosDia = dia.data) }
            if (mes is Resource.Success) _uiState.update { it.copy(totalIngresosMes = mes.data) }
            if (hist is Resource.Success) _uiState.update { it.copy(historial = hist.data) }
        }
    }

    fun crearUsuario(usuario: Usuario, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            when (val r = authRepo.crearUsuario(usuario, password)) {
                is Resource.Success -> _uiState.update { it.copy(loading = false, mensaje = "Usuario ${usuario.nombre} creado correctamente") }
                is Resource.Error -> _uiState.update { it.copy(loading = false, error = r.message) }
                else -> {}
            }
        }
    }

    fun actualizarTarifa(tarifa: Tarifa) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val r = parqueoRepo.actualizarTarifa(tarifa)) {
                is Resource.Success -> { cargarTarifas(); _uiState.update { it.copy(loading = false, mensaje = "Tarifa actualizada") } }
                is Resource.Error -> _uiState.update { it.copy(loading = false, error = r.message) }
                else -> {}
            }
        }
    }

    fun actualizarParqueadero(p: Parqueadero) {
        viewModelScope.launch {
            when (val r = parqueoRepo.actualizarParqueadero(p)) {
                is Resource.Success -> { cargarParqueadero(); _uiState.update { it.copy(mensaje = "Configuración guardada") } }
                is Resource.Error -> _uiState.update { it.copy(error = r.message) }
                else -> {}
            }
        }
    }

    fun desbloquearUsuario(uid: String) {
        viewModelScope.launch {
            authRepo.desbloquearUsuario(uid)
            _uiState.update { it.copy(mensaje = "Usuario desbloqueado") }
        }
    }

    fun eliminarUsuario(uid: String) {
        viewModelScope.launch {
            adminRepo.eliminarUsuario(uid)
            _uiState.update { it.copy(mensaje = "Usuario eliminado") }
        }
    }

    fun clearMensaje() = _uiState.update { it.copy(mensaje = null) }
    fun clearError() = _uiState.update { it.copy(error = null) }
}
