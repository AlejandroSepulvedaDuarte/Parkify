package com.example.parkify.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkify.domain.model.TipoVehiculo
import com.example.parkify.utils.toCurrency
import com.example.parkify.utils.toDisplayString

@Composable
fun ReportesTab(uiState: AdminUiState) {
    val totalCarros = uiState.historial.count { it.tipo == TipoVehiculo.CARRO }
    val totalMotos = uiState.historial.count { it.tipo == TipoVehiculo.MOTO }
    val promedioTiempo = if (uiState.historial.isNotEmpty()) {
        uiState.historial.mapNotNull { v ->
            if (v.horaEntrada != null && v.horaSalida != null)
                (v.horaSalida.time - v.horaEntrada.time) / (1000 * 60) else null
        }.average().toLong()
    } else 0L

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reportes del mes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Métricas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ResumenCard(Modifier.weight(1f), "Total ingresos", uiState.totalIngresosMes.toCurrency(),
                MaterialTheme.colorScheme.primaryContainer)
            ResumenCard(Modifier.weight(1f), "Vehículos atendidos", uiState.historial.size.toString(),
                MaterialTheme.colorScheme.secondaryContainer)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ResumenCard(Modifier.weight(1f), "🚗 Carros", totalCarros.toString(),
                MaterialTheme.colorScheme.surfaceVariant)
            ResumenCard(Modifier.weight(1f), "🏍️ Motos", totalMotos.toString(),
                MaterialTheme.colorScheme.surfaceVariant)
            ResumenCard(Modifier.weight(1f), "Tiempo prom.", "${promedioTiempo}min",
                MaterialTheme.colorScheme.surfaceVariant)
        }

        Spacer(Modifier.height(16.dp))
        Text("Historial de salidas", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))

        if (uiState.historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin registros este mes", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(uiState.historial.sortedByDescending { it.horaSalida }) { v ->
                    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(v.placa, fontWeight = FontWeight.Bold)
                                Text("${v.tipo.name} · ${v.modalidadCobro.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                v.horaSalida?.let {
                                    Text(it.toDisplayString(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Text(v.totalCobro.toCurrency(), fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
