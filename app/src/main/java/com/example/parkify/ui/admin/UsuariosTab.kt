package com.example.parkify.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.parkify.domain.model.Rol
import com.example.parkify.domain.model.Usuario
import com.example.parkify.utils.Constants

@Composable
fun UsuariosTab(
    uiState: AdminUiState,
    onCrear: (Usuario, String) -> Unit,
    onDesbloquear: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    var mostrarCrear by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Usuarios del sistema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { mostrarCrear = true }) {
                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Crear")
            }
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.usuarios) { u ->
                UsuarioCard(
                    usuario = u,
                    onDesbloquear = { onDesbloquear(u.uid) },
                    onEliminar = { onEliminar(u.uid) }
                )
            }
        }
    }

    if (mostrarCrear) {
        CrearUsuarioDialog(
            loading = uiState.loading,
            onCrear = { u, p -> onCrear(u, p); mostrarCrear = false },
            onDismiss = { mostrarCrear = false }
        )
    }
}

@Composable
fun UsuarioCard(usuario: Usuario, onDesbloquear: () -> Unit, onEliminar: () -> Unit) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(50),
                color = if (usuario.rol == Rol.ADMINISTRADOR) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(usuario.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold)
                Text(usuario.email, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(onClick = {}, label = { Text(usuario.rol.name.lowercase()) })
                    if (usuario.bloqueado) {
                        AssistChip(
                            onClick = onDesbloquear,
                            label = { Text("bloqueado — desbloquear") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, Modifier.size(16.dp)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        )
                    }
                }
            }
            IconButton(onClick = { confirmarEliminar = true }) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar usuario?") },
            text = { Text("Se desactivará la cuenta de ${usuario.nombre}.") },
            confirmButton = {
                Button(onClick = { onEliminar(); confirmarEliminar = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar")
                }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun CrearUsuarioDialog(
    loading: Boolean,
    onCrear: (Usuario, String) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(Rol.OPERARIO) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nuevo usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = email, onValueChange = { email = it },
                    label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
                OutlinedTextField(value = telefono, onValueChange = { telefono = it },
                    label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(
                    value = password,
                    onValueChange = { if (it.length <= Constants.MAX_PASSWORD_LENGTH) password = it },
                    label = { Text("Contraseña (${Constants.MIN_PASSWORD_LENGTH}-${Constants.MAX_PASSWORD_LENGTH} caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    }
                )
                Text("Rol del usuario", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Rol.values().forEach { rol ->
                        FilterChip(selected = rolSeleccionado == rol, onClick = { rolSeleccionado = rol },
                            label = { Text(rol.name.lowercase().replaceFirstChar { it.uppercase() }) })
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        nombre.isBlank() -> error = "El nombre es obligatorio"
                        email.isBlank() -> error = "El correo es obligatorio"
                        password.length < Constants.MIN_PASSWORD_LENGTH -> error = "La contraseña debe tener mínimo ${Constants.MIN_PASSWORD_LENGTH} caracteres"
                        password.length > Constants.MAX_PASSWORD_LENGTH -> error = "La contraseña debe tener máximo ${Constants.MAX_PASSWORD_LENGTH} caracteres"
                        else -> {
                            onCrear(Usuario(nombre = nombre, email = email, telefono = telefono, rol = rolSeleccionado), password)
                        }
                    }
                },
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Crear usuario")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
