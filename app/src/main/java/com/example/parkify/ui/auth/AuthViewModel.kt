package com.example.parkify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkify.domain.model.Rol
import com.example.parkify.domain.model.Usuario
import com.example.parkify.domain.repository.IAuthRepository
import com.example.parkify.domain.repository.IParqueoRepository
import com.example.parkify.utils.Constants
import com.example.parkify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val usuarioActual: Usuario? = null,
    val bloqueado: Boolean = false,
    val intentosRestantes: Int = Constants.MAX_INTENTOS_LOGIN,
    val telefonoAdmin: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: IAuthRepository,
    private val parqueoRepo: IParqueoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn = authRepo.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            val info = parqueoRepo.getParqueaderoInfo()
            if (info is Resource.Success) {
                _uiState.update { it.copy(telefonoAdmin = info.data.telefonoAdmin) }
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validarPassword(password)) {
            _uiState.update { it.copy(error = "La contraseña debe tener entre ${Constants.MIN_PASSWORD_LENGTH} y ${Constants.MAX_PASSWORD_LENGTH} caracteres") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            when (val result = authRepo.login(email, password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(loading = false, usuarioActual = result.data) }
                }
                is Resource.Error -> {
                    if (result.message == "BLOQUEADO") {
                        _uiState.update { it.copy(loading = false, bloqueado = true, error = "Cuenta bloqueada. Comuníquese con el administrador.") }
                    } else {
                        authRepo.registrarIntentoFallido(email)
                        val restantes = _uiState.value.intentosRestantes - 1
                        val msg = if (restantes <= 0)
                            "Cuenta bloqueada. Contacte al administrador."
                        else
                            "Credenciales incorrectas. Intentos restantes: $restantes"
                        _uiState.update { it.copy(
                            loading = false,
                            error = msg,
                            intentosRestantes = restantes,
                            bloqueado = restantes <= 0
                        )}
                    }
                }
                else -> _uiState.update { it.copy(loading = false) }
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            _uiState.update { AuthUiState() }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun validarPassword(p: String) =
        p.length in Constants.MIN_PASSWORD_LENGTH..Constants.MAX_PASSWORD_LENGTH

    fun cargarUsuarioActual() {
        viewModelScope.launch {
            when (val r = authRepo.getUsuarioActual()) {
                is Resource.Success -> _uiState.update { it.copy(usuarioActual = r.data) }
                else -> {}
            }
        }
    }
}
