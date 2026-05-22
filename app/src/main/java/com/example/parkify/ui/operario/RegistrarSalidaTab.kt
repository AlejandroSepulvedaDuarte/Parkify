package com.example.parkify.ui.operario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkify.utils.toCurrency
import com.example.parkify.utils.toDisplayString

@Composable
fun RegistrarSalidaTab(
    usuarioId: String,
    viewModel: OperarioViewModel,
    uiState: OperarioUiState
) {
    var placa by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar Salida", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = placa,
                onValueChange = { placa = it.uppercase() },
                label = { Text("Buscar por placa") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = { viewModel.buscarPorPlaca(placa.trim()) },
                enabled = placa.isNotBlank() && !uiState.loading,
                modifier = Modifier.height(56.dp)
            ) { Text("Buscar") }
        }

        if (uiState.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        uiState.vehiculoBuscado?.let { v ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Vehículo encontrado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    InfoRow("Placa", v.placa)
                    InfoRow("Tipo", v.tipo.name)
                    InfoRow("Propietario", v.propietario.ifBlank { "—" })
                    InfoRow("Entrada", v.horaEntrada?.toDisplayString() ?: "—")
                    InfoRow("Modalidad", v.modalidadCobro.name)
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a cobrar", fontWeight = FontWeight.Bold)
                        Text(uiState.cobroCalculado.toCurrency(), fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    }

                    Button(
                        onClick = { viewModel.registrarSalida(v.id, usuarioId, uiState.cobroCalculado) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.loading
                    ) { Text("Confirmar Salida y Cobrar") }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
