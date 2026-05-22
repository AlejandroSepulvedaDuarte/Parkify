package com.example.parkify.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkify.domain.model.TipoVehiculo
import com.example.parkify.utils.toCurrency

@Composable
fun ResumenAdminTab(uiState: AdminUiState) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Panel de Control", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Disponibilidad
        Text("Disponibilidad en tiempo real", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResumenCard(modifier = Modifier.weight(1f), label = "🚗 Carros libres",
                value = uiState.carrosDisponibles.toString(), color = MaterialTheme.colorScheme.primaryContainer)
            ResumenCard(modifier = Modifier.weight(1f), label = "🏍️ Motos libres",
                value = uiState.motosDisponibles.toString(), color = MaterialTheme.colorScheme.secondaryContainer)
        }

        val totalCarros = uiState.vehiculosActivos.count { it.tipo == TipoVehiculo.CARRO }
        val totalMotos = uiState.vehiculosActivos.count { it.tipo == TipoVehiculo.MOTO }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResumenCard(modifier = Modifier.weight(1f), label = "Carros ocupados",
                value = totalCarros.toString(), color = MaterialTheme.colorScheme.errorContainer)
            ResumenCard(modifier = Modifier.weight(1f), label = "Motos ocupadas",
                value = totalMotos.toString(), color = MaterialTheme.colorScheme.tertiaryContainer)
        }

        HorizontalDivider()

        // Ingresos
        Text("Ingresos", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResumenCard(modifier = Modifier.weight(1f), label = "Hoy",
                value = uiState.totalIngresosDia.toCurrency(), color = MaterialTheme.colorScheme.primaryContainer)
            ResumenCard(modifier = Modifier.weight(1f), label = "Este mes",
                value = uiState.totalIngresosMes.toCurrency(), color = MaterialTheme.colorScheme.secondaryContainer)
        }

        HorizontalDivider()

        // Últimos movimientos
        // Últimos movimientos
        Text("Últimos movimientos del mes (${uiState.historial.size})", style = MaterialTheme.typography.titleSmall)

        // 1. Quitamos el .take(5) para mostrar TODO el historial
        // 2. Usamos .sortedByDescending para que los cobros más nuevos salgan primero arriba
        uiState.historial.sortedByDescending { it.horaSalida }.forEach { v ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            v.placa,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = v.tipo.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = v.totalCobro.toCurrency(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }
}

@Composable
fun ResumenCard(modifier: Modifier, label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
