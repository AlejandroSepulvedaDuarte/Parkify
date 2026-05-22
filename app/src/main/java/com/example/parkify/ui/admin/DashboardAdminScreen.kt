package com.example.parkify.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parkify.ui.operario.ControlParqueoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    usuarioId: String,
    usuarioNombre: String,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionado by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.clearMensaje() }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar("Error: $it"); viewModel.clearError() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Parkify Admin", fontWeight = FontWeight.Bold)
                        Text(
                            "Bienvenido, $usuarioNombre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Salir") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    icon = { Icon(Icons.Default.LocalParking, null) },
                    label = { Text("Parqueo") }
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 2,
                    onClick = { tabSeleccionado = 2 },
                    icon = { Icon(Icons.Default.AttachMoney, null) },
                    label = { Text("Tarifas") }
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 3,
                    onClick = { tabSeleccionado = 3 },
                    icon = { Icon(Icons.Default.People, null) },
                    label = { Text("Usuarios") }
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 4,
                    onClick = { tabSeleccionado = 4 },
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("Reportes") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (tabSeleccionado) {
                0 -> ResumenAdminTab(uiState = uiState)
                1 -> ControlParqueoScreen(usuarioId = usuarioId)
                2 -> ConfigTarifasTab(uiState = uiState, onGuardar = { viewModel.actualizarTarifa(it) })
                3 -> UsuariosTab(
                    uiState = uiState,
                    onCrear = { u, p -> viewModel.crearUsuario(u, p) },
                    onDesbloquear = { viewModel.desbloquearUsuario(it) },
                    onEliminar = { viewModel.eliminarUsuario(it) }
                )
                4 -> ReportesTab(uiState = uiState)
            }
        }
    }
}