package com.example.parkify.ui.operario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkify.domain.model.*
import com.example.parkify.domain.repository.IAuthRepository
import com.example.parkify.domain.repository.IParqueoRepository
import com.example.parkify.domain.usecase.CalcularCobroUseCase
import com.example.parkify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class OperarioUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val vehiculosActivos: List<Vehiculo> = emptyList(),
    val carrosDisponibles: Int = 0,
    val motosDisponibles: Int = 0,
    val vehiculoBuscado: Vehiculo? = null,
    val cobroCalculado: Double = 0.0,
    val tarifas: List<Tarifa> = emptyList()
)

@HiltViewModel
class OperarioViewModel @Inject constructor(
    private val parqueoRepo: IParqueoRepository,
    private val authRepo: IAuthRepository,
    private val calcularCobro: CalcularCobroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperarioUiState())
    val uiState: StateFlow<OperarioUiState> = _uiState.asStateFlow()

    init {
        observarVehiculos()
        observarDisponibilidad()
        cargarTarifas()
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
            parqueoRepo.observarDisponibilidad().collect { (carros, motos) ->
                _uiState.update { it.copy(carrosDisponibles = carros, motosDisponibles = motos) }
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

    fun registrarEntrada(
        placa: String,
        tipo: TipoVehiculo,
        propietario: String,
        telefono: String,
        modalidad: ModalidadCobro,
        operarioId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val vehiculo = Vehiculo(
                placa = placa.uppercase().trim(),
                tipo = tipo,
                propietario = propietario,
                telefono = telefono,
                modalidadCobro = modalidad,
                operarioEntradaId = operarioId
            )
            when (val r = parqueoRepo.registrarEntrada(vehiculo)) {
                is Resource.Success -> _uiState.update { it.copy(loading = false, mensaje = "Entrada registrada: ${placa.uppercase()}") }
                is Resource.Error -> _uiState.update { it.copy(loading = false, error = r.message) }
                else -> {}
            }
        }
    }

    fun buscarPorPlaca(placa: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, vehiculoBuscado = null, cobroCalculado = 0.0) }
            when (val r = parqueoRepo.buscarPorPlaca(placa)) {
                is Resource.Success -> {
                    val v = r.data
                    if (v != null) {
                        val cobro = v.horaEntrada?.let {
                            calcularCobro(it, Date(), v.tipo, v.modalidadCobro, _uiState.value.tarifas)
                        } ?: 0.0
                        _uiState.update { it.copy(loading = false, vehiculoBuscado = v, cobroCalculado = cobro) }
                    } else {
                        _uiState.update { it.copy(loading = false, error = "Vehículo no encontrado en el parqueadero") }
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(loading = false, error = r.message) }
                else -> {}
            }
        }
    }

    fun registrarSalida(vehiculoId: String, operarioId: String, cobro: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val r = parqueoRepo.registrarSalida(vehiculoId, operarioId, cobro)) {
                is Resource.Success -> _uiState.update {
                    it.copy(
                        loading = false,
                        mensaje = "Salida registrada. Total: $cobro",
                        vehiculoBuscado = null
                    )
                }
                is Resource.Error -> _uiState.update { it.copy(loading = false, error = r.message) }
                else -> {}
            }
        }
    }

    fun clearMensaje() = _uiState.update { it.copy(mensaje = null) }
    fun clearError() = _uiState.update { it.copy(error = null) }
}
