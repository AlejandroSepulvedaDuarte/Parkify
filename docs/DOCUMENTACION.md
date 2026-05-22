# 📚 Documentación Técnica — Parkify

## Tabla de contenidos

1. [Estándares de código](#estándares-de-código)
2. [Arquitectura](#arquitectura)
3. [Convenciones de nomenclatura](#convenciones-de-nomenclatura)
4. [Flujo de datos](#flujo-de-datos)
5. [Manejo de errores](#manejo-de-errores)
6. [Firebase — Estructura de datos](#firebase--estructura-de-datos)
7. [Guía de contribución](#guía-de-contribución)
8. [Glosario](#glosario)

---

## Estándares de código

### Kotlin

- Se usa **Kotlin** como único lenguaje del proyecto.
- Seguir las [convenciones oficiales de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).
- Máximo **120 caracteres** por línea.
- Usar `val` siempre que sea posible; usar `var` solo cuando sea estrictamente necesario.
- Preferir **funciones de extensión** sobre funciones utilitarias sueltas.

### Jetpack Compose

- Cada pantalla principal tiene su propio archivo `*Screen.kt`.
- Los componentes reutilizables van en `ui/common/`.
- Todos los Composables deben estar anotados con `@Composable`.
- Composables con más de 3 parámetros deben usar un data class de estado (`UiState`).
- Evitar lógica de negocio dentro de los Composables; delegar al ViewModel.

### Ejemplo de Composable bien estructurado

```kotlin
// ✅ Correcto
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (rol: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    // Solo UI aquí, sin lógica de negocio
}

// ❌ Incorrecto — lógica de negocio en el Composable
@Composable
fun LoginScreen() {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password) // NO
}
```

---

## Arquitectura

El proyecto implementa **Clean Architecture** dividida en tres capas:

### 1. Domain (Dominio)
- Es la capa más interna y no depende de ninguna otra.
- Contiene los **modelos** de negocio, las **interfaces** de repositorios y los **casos de uso**.
- Es puro Kotlin, sin dependencias de Android ni Firebase.

### 2. Data (Datos)
- Implementa las interfaces definidas en Domain.
- Contiene los **DTOs** de Firebase, los **mappers** y las **implementaciones** de repositorios.
- Es la única capa que conoce Firebase.

### 3. UI (Presentación)
- Contiene las pantallas Compose y los ViewModels.
- Los ViewModels usan las interfaces del dominio, nunca las implementaciones directas.
- Se comunica con Data a través de las interfaces.

### Diagrama de dependencias

```
UI → Domain ← Data
```

La UI y Data dependen de Domain. Domain no depende de nadie.

---

## Convenciones de nomenclatura

### Archivos y clases

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| Modelos de dominio | `NombreEnSingular.kt` | `Usuario.kt` |
| DTOs | `NombreDTO.kt` | `UsuarioDTO.kt` |
| Interfaces repositorio | `INombreRepository.kt` | `IAuthRepository.kt` |
| Implementaciones | `NombreRepositoryImpl.kt` | `AuthRepositoryImpl.kt` |
| ViewModels | `NombreViewModel.kt` | `AuthViewModel.kt` |
| Pantallas | `NombreScreen.kt` | `LoginScreen.kt` |
| Casos de uso | `VerbNombreUseCase.kt` | `CalcularCobroUseCase.kt` |
| Mappers | `NombreMapper.kt` | `VehiculoMapper.kt` |

### Variables y funciones

```kotlin
// Variables: camelCase
val usuarioActual: Usuario
var intentosFallidos: Int

// Funciones: camelCase comenzando con verbo
fun registrarEntrada()
fun calcularCobro()
fun observarVehiculos()

// Constantes: SCREAMING_SNAKE_CASE
const val MAX_INTENTOS_LOGIN = 5
const val MIN_PASSWORD_LENGTH = 6

// Enums: PascalCase para el tipo, SCREAMING_SNAKE_CASE para valores
enum class TipoVehiculo { CARRO, MOTO }
enum class Rol { ADMINISTRADOR, OPERARIO }
```

### Colecciones de Firestore

- Nombres en **minúsculas** y **plural**: `usuarios`, `vehiculos`, `tarifas`
- Campos en **camelCase**: `horaEntrada`, `totalCobro`, `intentosFallidos`
- IDs de documentos: generados automáticamente por Firestore o UID de Firebase Auth

---

## Flujo de datos

### Login

```
LoginScreen
    → AuthViewModel.login()
        → IAuthRepository.login()
            → AuthRepositoryImpl.login()
                → FirebaseService.login()
                    → Firebase Auth
                → FirebaseService.getUsuario()
                    → Firestore (colección usuarios)
            → UsuarioMapper.toDomain()
        → Resource.Success(Usuario)
    → UiState actualizado
→ Navegación según rol
```

### Registro de entrada

```
EntradaTab
    → OperarioViewModel.registrarEntrada()
        → IParqueoRepository.registrarEntrada()
            → ParqueoRepositoryImpl.registrarEntrada()
                → VehiculoMapper.toDTO()
                → FirebaseService.saveVehiculo()
                    → Firestore (colección vehiculos)
        → Resource.Success(Vehiculo)
    → Snackbar de confirmación
```

### Registro de salida con cobro

```
SalidaTab
    → OperarioViewModel.buscarPorPlaca()
        → Firestore busca vehiculo por placa y estado EN_PARQUEADERO
        → CalcularCobroUseCase(horaEntrada, horaActual, tipo, modalidad, tarifas)
        → Muestra cobro calculado en pantalla
    → OperarioViewModel.registrarSalida(cobro)
        → IParqueoRepository.registrarSalida(vehiculoId, operarioId, cobro)
            → Firestore actualiza: estado=SALIDA, horaSalida, totalCobro
        → Resource.Success
    → Snackbar de confirmación
```

---

## Manejo de errores

Se usa la clase sellada `Resource<T>` para manejar estados:

```kotlin
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
```

### Reglas de uso

- Toda función de repositorio debe retornar `Resource<T>`.
- Los repositorios capturan excepciones con `try/catch` y retornan `Resource.Error`.
- Los ViewModels consumen el `Resource` y actualizan el `UiState`.
- La UI observa el `UiState` y muestra errores via `Snackbar` o `Card` de error.

```kotlin
// ✅ Correcto en repositorio
override suspend fun getUsuario(uid: String): Resource<Usuario> {
    return try {
        val dto = firebase.getUsuario(uid)
        if (dto == null) Resource.Error("Usuario no encontrado")
        else Resource.Success(UsuarioMapper.toDomain(dto))
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error desconocido")
    }
}

// ✅ Correcto en ViewModel
when (val result = authRepo.login(email, password)) {
    is Resource.Success -> { /* actualizar estado */ }
    is Resource.Error -> { /* mostrar error */ }
    is Resource.Loading -> { /* mostrar loading */ }
}
```

---

## Firebase — Estructura de datos

### Colección `usuarios`

```
usuarios/{uid}
├── uid: String           # UID de Firebase Auth
├── nombre: String        # Nombre completo
├── email: String         # Correo electrónico
├── rol: String           # "ADMINISTRADOR" o "OPERARIO"
├── telefono: String      # Teléfono de contacto
├── activo: Boolean       # Si la cuenta está activa
├── intentosFallidos: Int # Contador de intentos fallidos
└── bloqueado: Boolean    # Si la cuenta está bloqueada
```

### Colección `vehiculos`

```
vehiculos/{id}
├── id: String            # ID automático de Firestore
├── placa: String         # Placa en mayúsculas (ej: ABC123)
├── tipo: String          # "CARRO" o "MOTO"
├── propietario: String   # Nombre del propietario
├── telefono: String      # Teléfono del propietario
├── horaEntrada: Long     # Timestamp en milisegundos
├── horaSalida: Long?     # Timestamp en milisegundos (null si aún está)
├── totalCobro: Double    # Total cobrado en pesos colombianos
├── estado: String        # "EN_PARQUEADERO" o "SALIDA"
├── modalidadCobro: String # "HORA", "DIA", "SEMANA" o "MES"
├── operarioEntradaId: String  # UID del operario que registró entrada
└── operarioSalidaId: String   # UID del operario que registró salida
```

### Colección `tarifas`

```
tarifas/{id}
├── id: String      # ID del documento
├── tipo: String    # "CARRO" o "MOTO"
├── modalidad: String # "HORA", "DIA", "SEMANA" o "MES"
└── precio: Double  # Precio en pesos colombianos
```

### Colección `config`

```
config/parqueadero
├── nombre: String             # Nombre del parqueadero
├── totalEspaciosCarros: Int   # Capacidad total de carros
├── totalEspaciosMotos: Int    # Capacidad total de motos
├── telefonoAdmin: String      # Teléfono del administrador
└── emailAdmin: String         # Correo del administrador
```

---

## Guía de contribución

### Flujo de trabajo con Git

```bash
# 1. Crear rama desde main
git checkout -b feature/nombre-funcionalidad

# 2. Hacer commits pequeños y descriptivos
git commit -m "feat: agrega registro de entrada de vehículos"
git commit -m "fix: corrige cálculo de cobro por semana"
git commit -m "docs: actualiza README con instrucciones de Firebase"

# 3. Push y Pull Request
git push origin feature/nombre-funcionalidad
```

### Prefijos de commits

| Prefijo | Uso |
|---------|-----|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Cambios en documentación |
| `style:` | Cambios de formato o UI |
| `refactor:` | Refactorización sin cambiar funcionalidad |
| `test:` | Agregar o modificar tests |
| `chore:` | Tareas de mantenimiento |

### Checklist antes de hacer PR

- [ ] El código compila sin errores
- [ ] No hay warnings ignorados sin justificación
- [ ] Los nombres siguen las convenciones del proyecto
- [ ] Se actualizó la documentación si aplica
- [ ] Se probó en emulador y/o dispositivo físico

---

## Glosario

| Término | Definición |
|---------|-----------|
| **Operario** | Usuario con acceso limitado, puede registrar entradas y salidas |
| **Administrador** | Usuario con acceso completo a toda la plataforma |
| **DTO** | Data Transfer Object, modelo usado para comunicarse con Firebase |
| **Mapper** | Clase que convierte entre DTO y modelo de dominio |
| **UseCase** | Clase que encapsula una regla de negocio específica |
| **UiState** | Data class que representa el estado completo de una pantalla |
| **Resource** | Clase sellada para manejar Success, Error y Loading |
| **Flow** | Stream de datos reactivo de Kotlin Coroutines |
| **Hilt** | Framework de inyección de dependencias para Android |
| **Firestore** | Base de datos NoSQL en tiempo real de Firebase |
| **Placa** | Identificador único del vehículo, formato colombiano (ej: ABC123) |
| **Modalidad** | Forma de cobro: por hora, día, semana o mes |
