package com.example.parkify.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parkify.domain.model.Rol
import com.example.parkify.ui.admin.DashboardAdminScreen
import com.example.parkify.ui.auth.AuthViewModel
import com.example.parkify.ui.auth.LoginScreen
import com.example.parkify.ui.operario.ControlParqueoScreen

@Composable
fun ParkifyNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val uiState by authViewModel.uiState.collectAsState()

    when {
        uiState.usuarioActual == null -> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {}
            )
        }
        uiState.usuarioActual?.rol == Rol.ADMINISTRADOR -> {
            val user = uiState.usuarioActual!!
            DashboardAdminScreen(
                usuarioId = user.uid,
                usuarioNombre = user.nombre,
                onLogout = { authViewModel.logout() }
            )
        }
        else -> {
            val user = uiState.usuarioActual!!
            OperarioNavWrapper(
                usuarioId = user.uid,
                onLogout = { authViewModel.logout() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperarioNavWrapper(usuarioId: String, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parkify") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Salir"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            ControlParqueoScreen(usuarioId = usuarioId)
        }
    }
}