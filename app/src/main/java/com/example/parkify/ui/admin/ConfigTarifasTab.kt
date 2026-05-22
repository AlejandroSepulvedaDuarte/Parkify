package com.example.parkify.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.parkify.domain.model.Tarifa
import com.example.parkify.utils.toCurrency

@Composable
fun ConfigTarifasTab(
    uiState: AdminUiState,
    onGuardar: (Tarifa) -> Unit
) {
    var editando by remember { mutableStateOf<Tarifa?>(null) }
    var nuevoPrecio by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Configuración de Tarifas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Solo el administrador puede modificar los precios.", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        if (uiState.tarifas.isEmpty()) {
            Text("No hay tarifas configuradas. Agrega tarifas iniciales desde la consola Firebase.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.tarifas) { tarifa ->
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${tarifa.tipo} — ${tarifa.modalidad}", fontWeight = FontWeight.Bold)
                            Text(tarifa.precio.toCurrency(), color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { editando = tarifa; nuevoPrecio = tarifa.precio.toString() }) {
                            Icon(Icons.Default.Edit, "Editar tarifa")
                        }
                    }
                }
            }
        }
    }

    // Dialog de edición
    editando?.let { t ->
        AlertDialog(
            onDismissRequest = { editando = null },
            title = { Text("Editar tarifa: ${t.tipo} / ${t.modalidad}") },
            text = {
                OutlinedTextField(
                    value = nuevoPrecio,
                    onValueChange = { nuevoPrecio = it },
                    label = { Text("Precio (COP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val p = nuevoPrecio.toDoubleOrNull() ?: return@Button
                    onGuardar(t.copy(precio = p))
                    editando = null
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editando = null }) { Text("Cancelar") } }
        )
    }
}
