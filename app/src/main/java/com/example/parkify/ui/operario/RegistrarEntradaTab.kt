package com.example.parkify.ui.operario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkify.domain.model.ModalidadCobro
import com.example.parkify.domain.model.TipoVehiculo
import com.example.parkify.utils.isValidPlaca

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarEntradaTab(
    usuarioId: String,
    viewModel: OperarioViewModel,
    uiState: OperarioUiState
) {
    var placa by remember { mutableStateOf("") }
    var propietario by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoVehiculo.CARRO) }
    var modalidadSeleccionada by remember { mutableStateOf(ModalidadCobro.HORA) }
    var placaError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar Entrada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it.uppercase(); placaError = false },
            label = { Text("Placa del vehículo") },
            placeholder = { Text("Ej: ABC123") },
            isError = placaError,
            supportingText = if (placaError) {{ Text("Placa inválida") }} else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = propietario,
            onValueChange = { propietario = it },
            label = { Text("Nombre del propietario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Tipo de vehículo
        Text("Tipo de vehículo", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipoVehiculo.values().forEach { tipo ->
                FilterChip(
                    selected = tipoSeleccionado == tipo,
                    onClick = { tipoSeleccionado = tipo },
                    label = { Text(if (tipo == TipoVehiculo.CARRO) "🚗 Carro" else "🏍️ Moto") }
                )
            }
        }

        // Modalidad de cobro
        Text("Modalidad de cobro", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModalidadCobro.values().forEach { m ->
                FilterChip(
                    selected = modalidadSeleccionada == m,
                    onClick = { modalidadSeleccionada = m },
                    label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        Button(
            onClick = {
                if (!placa.isValidPlaca()) { placaError = true; return@Button }
                viewModel.registrarEntrada(placa, tipoSeleccionado, propietario, telefono, modalidadSeleccionada, usuarioId)
                placa = ""; propietario = ""; telefono = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.loading && placa.isNotBlank()
        ) {
            if (uiState.loading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Registrar Entrada")
        }
    }
}
