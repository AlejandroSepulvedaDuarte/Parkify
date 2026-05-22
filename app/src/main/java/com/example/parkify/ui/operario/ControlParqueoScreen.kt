package com.example.parkify.ui.operario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parkify.domain.model.Vehiculo
import com.example.parkify.utils.toDisplayString
import com.example.parkify.utils.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlParqueoScreen(
    usuarioId: String,
    viewModel: OperarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionado by remember { mutableStateOf(0) }

    // Snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.clearMensaje() }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar("Error: $it"); viewModel.clearError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(bottom = 0.dp))  {

            // ── STATS ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Carros disponibles",
                    valor = uiState.carrosDisponibles.toString(),
                    icono = "🚗"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Motos disponibles",
                    valor = uiState.motosDisponibles.toString(),
                    icono = "🏍️"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    titulo = "En parqueadero",
                    valor = uiState.vehiculosActivos.size.toString(),
                    icono = "🅿️"
                )
            }

            // ── TABS ─────────────────────────────────────────────────────
            TabRow(selectedTabIndex = tabSeleccionado) {
                Tab(selected = tabSeleccionado == 0, onClick = { tabSeleccionado = 0 },
                    text = { Text("Entrada") }, icon = { Icon(Icons.Default.Add, null) })
                Tab(selected = tabSeleccionado == 1, onClick = { tabSeleccionado = 1 },
                    text = { Text("Salida") }, icon = { Icon(Icons.Default.ExitToApp, null) })
                Tab(selected = tabSeleccionado == 2, onClick = { tabSeleccionado = 2 },
                    text = { Text("Activos") }, icon = { Icon(Icons.Default.List, null) })
            }

            when (tabSeleccionado) {
                0 -> RegistrarEntradaTab(usuarioId = usuarioId, viewModel = viewModel, uiState = uiState)
                1 -> RegistrarSalidaTab(usuarioId = usuarioId, viewModel = viewModel, uiState = uiState)
                2 -> VehiculosActivosTab(vehiculos = uiState.vehiculosActivos)
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, titulo: String, valor: String, icono: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, fontSize = 24.sp)
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(titulo, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
