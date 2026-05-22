package com.example.parkify.ui.operario

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
import androidx.compose.ui.unit.sp
import com.example.parkify.domain.model.Vehiculo
import com.example.parkify.domain.model.TipoVehiculo
import com.example.parkify.utils.toDisplayString

@Composable
fun VehiculosActivosTab(vehiculos: List<Vehiculo>) {
    if (vehiculos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🅿️", fontSize = 48.sp)
                Text("No hay vehículos en el parqueadero", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vehiculos) { v ->
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (v.tipo == TipoVehiculo.CARRO) "🚗" else "🏍️", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(v.placa, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(v.propietario.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        v.horaEntrada?.let { Text("Entrada: ${it.toDisplayString()}", style = MaterialTheme.typography.bodySmall) }
                    }
                    AssistChip(onClick = {}, label = { Text(v.modalidadCobro.name.lowercase()) })
                }
            }
        }
    }
}
