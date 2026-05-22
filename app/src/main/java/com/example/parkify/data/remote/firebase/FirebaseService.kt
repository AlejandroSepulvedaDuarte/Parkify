package com.example.parkify.data.remote.firebase

import com.example.parkify.data.remote.firebase.models.UsuarioDTO
import com.example.parkify.data.remote.firebase.models.VehiculoDTO
import com.example.parkify.data.remote.firebase.models.TarifaDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    // ========== AUTENTICACIÓN ==========
    suspend fun login(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun createUserAuth(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    fun currentUserId() = auth.currentUser?.uid

    fun logout() = auth.signOut()

    fun authStateFlow(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // ========== USUARIOS ==========
    suspend fun getUsuario(uid: String): UsuarioDTO? =
        db.collection("usuarios").document(uid).get().await().toObject<UsuarioDTO>()

    suspend fun actualizarIntentosFallidos(uid: String, intentos: Int, bloqueado: Boolean) {
        db.collection("usuarios").document(uid)
            .update("intentosFallidos", intentos, "bloqueado", bloqueado).await()
    }

    suspend fun getUsuarios(): List<UsuarioDTO> =
        db.collection("usuarios").get().await()
            .documents.mapNotNull { it.toObject<UsuarioDTO>() }

    fun observarUsuarios(): Flow<List<UsuarioDTO>> = callbackFlow {
        val snapshotListener = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val usuarios = snapshot?.documents?.mapNotNull { it.toObject<UsuarioDTO>() } ?: emptyList()
                trySend(usuarios)
            }
        awaitClose { snapshotListener.remove() }
    }

    suspend fun saveUsuario(usuario: UsuarioDTO) {
        db.collection("usuarios")
            .document(usuario.uid)
            .set(usuario)
            .await()
    }

    // ========== VEHÍCULOS ==========

    // Observar vehículos activos (estado = "EN_PARQUEADERO" o similar)
    fun observarVehiculosActivos(): Flow<List<VehiculoDTO>> = callbackFlow {
        val snapshotListener = db.collection("vehiculos")
            .whereEqualTo("estado", "EN_PARQUEADERO")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val vehiculos = snapshot?.documents?.mapNotNull { it.toObject<VehiculoDTO>() } ?: emptyList()
                trySend(vehiculos)
            }
        awaitClose { snapshotListener.remove() }
    }

    suspend fun saveVehiculo(vehiculo: VehiculoDTO) {
        db.collection("vehiculos")
            .document(vehiculo.id)
            .set(vehiculo)
            .await()
    }

    suspend fun updateVehiculo(vehiculoId: String, campos: Map<String, Any>) {
        db.collection("vehiculos")
            .document(vehiculoId)
            .update(campos)
            .await()
    }

    suspend fun getVehiculoPorPlaca(placa: String): VehiculoDTO? {
        val snapshot = db.collection("vehiculos")
            .whereEqualTo("placa", placa)
            .limit(1)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject<VehiculoDTO>()
    }

    // ========== VEHÍCULOS ==========

    // 1. Modifica esto para que escuche en tiempo real usando 'horaSalida' en vez de 'fecha'
    fun observarHistorialVehiculos(desde: Long, hasta: Long): Flow<List<VehiculoDTO>> = callbackFlow {
        val snapshotListener = db.collection("vehiculos")
            .whereGreaterThanOrEqualTo("horaSalida", desde)
            .whereLessThanOrEqualTo("horaSalida", hasta)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val vehiculos = snapshot?.documents?.mapNotNull { it.toObject<VehiculoDTO>() } ?: emptyList()
                trySend(vehiculos)
            }
        awaitClose { snapshotListener.remove() }
    }

    // Dejamos esta por si la necesitas en promesas, pero corregida con 'horaSalida'
    suspend fun getHistorialVehiculos(desde: Long, hasta: Long): List<VehiculoDTO> {
        return db.collection("vehiculos")
            .whereGreaterThanOrEqualTo("horaSalida", desde)
            .whereLessThanOrEqualTo("horaSalida", hasta)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<VehiculoDTO>() }
    }

    // ========== TARIFAS ==========

    suspend fun getTarifas(): List<TarifaDTO> {
        return db.collection("tarifas")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<TarifaDTO>() }
    }

    suspend fun saveTarifa(tarifa: TarifaDTO) {
        db.collection("tarifas")
            .document(tarifa.id)
            .set(tarifa)
            .await()
    }

    // ========== PARQUEADERO / CONFIGURACIÓN ==========

    suspend fun getParqueaderoInfo(): Map<String, Any>? {
        val snapshot = db.collection("configuracion")
            .document("parqueadero")
            .get()
            .await()
        return snapshot.data
    }

    suspend fun updateParqueaderoInfo(data: Map<String, Any>) {
        db.collection("configuracion")
            .document("parqueadero")
            .set(data)
            .await()
    }
}