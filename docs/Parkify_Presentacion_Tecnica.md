# 🅿️ Parkify — Presentación Técnica
### Sistema de Control de Parqueadero para Carros y Motos

---

## 1. Tecnologías Emergentes y Disruptivas

### 🔥 Firebase (Google)
Firebase es una plataforma de desarrollo de aplicaciones en la nube de Google. Es considerada **disruptiva** porque eliminó la necesidad de construir y mantener servidores propios.

| Servicio | Uso en Parkify | Por qué es emergente |
|---------|---------------|---------------------|
| **Firebase Authentication** | Gestión segura de usuarios y sesiones | Autenticación lista en minutos sin servidores |
| **Cloud Firestore** | Base de datos NoSQL en tiempo real | Sincronización automática entre dispositivos |

**Impacto:** Parkify no necesita servidor propio. Firebase maneja la autenticación, la base de datos y la sincronización en tiempo real, reduciendo el tiempo de desarrollo en un 60%.

---

### 🤖 Jetpack Compose (Google)
Jetpack Compose es el **toolkit moderno y declarativo** de Google para construir interfaces de usuario en Android. Lanzado en 2021, es la evolución disruptiva del desarrollo de UI en Android.

**¿Por qué es disruptivo?**
- Antes se usaba XML para diseñar interfaces (imperativo)
- Compose permite describir **cómo debe verse** la UI en Kotlin (declarativo)
- La UI se actualiza automáticamente cuando cambian los datos

```kotlin
// Con Compose: la UI reacciona automáticamente al estado
@Composable
fun PantallaBienvenida(nombre: String) {
    Text("Bienvenido, $nombre") // Se actualiza solo cuando cambia 'nombre'
}
```

---

### ⚡ Kotlin Coroutines + Flow
Tecnología moderna para manejar operaciones asíncronas (red, base de datos) de forma simple y eficiente.

**¿Por qué es emergente?**
- Reemplaza los callbacks complicados y el código espagueti
- Permite escuchar cambios en tiempo real con `Flow`
- En Parkify: cuando un vehículo entra, el contador se actualiza en pantalla automáticamente sin recargar

---

### 💉 Hilt (Inyección de Dependencias)
Hilt es la solución oficial de Google para inyección de dependencias en Android, construida sobre Dagger.

**¿Por qué es emergente?**
- Elimina el acoplamiento entre componentes
- Hace el código más testeable y mantenible
- En Parkify: el ViewModel no crea sus dependencias, las recibe automáticamente

---

### 🏗️ Clean Architecture
Patrón arquitectónico que separa el código en capas independientes, promovido por Robert C. Martin.

**¿Por qué es relevante?**
- Permite cambiar Firebase por otra base de datos sin tocar la UI
- Cada capa tiene una responsabilidad única
- El código de negocio no depende de Android

---

## 2. Diagrama de Clases

```
┌─────────────────────────────────────────────────────────────────┐
│                        DOMAIN — MODELS                          │
├─────────────────┐  ┌─────────────────┐  ┌────────────────────┐ │
│    Usuario      │  │    Vehiculo     │  │      Tarifa        │ │
├─────────────────┤  ├─────────────────┤  ├────────────────────┤ │
│ uid: String     │  │ id: String      │  │ id: String         │ │
│ nombre: String  │  │ placa: String   │  │ tipo: String       │ │
│ email: String   │  │ tipo: TipoVeh.  │  │ modalidad: String  │ │
│ rol: Rol        │  │ propietario:Str │  │ precio: Double     │ │
│ telefono: String│  │ horaEntrada:Date│  └────────────────────┘ │
│ activo: Boolean │  │ horaSalida:Date?│                          │
│ intentosFall:Int│  │ totalCobro:Doub │  ┌────────────────────┐ │
│ bloqueado: Bool │  │ estado:EstadoV. │  │   Parqueadero      │ │
└─────────────────┘  │ modalidad:Modal.│  ├────────────────────┤ │
                     │ operEntradaId:S │  │ nombre: String     │ │
       ┌─────────┐   │ operSalidaId:S  │  │ espaciosCarros:Int │ │
       │   Rol   │   └─────────────────┘  │ espaciosMotos:Int  │ │
       ├─────────┤                        │ telefonoAdmin:Str  │ │
       │ADMINIST.│   ┌──────────────┐     │ emailAdmin: String │ │
       │OPERARIO │   │ TipoVehiculo │     └────────────────────┘ │
       └─────────┘   ├──────────────┤                            │
                     │ CARRO        │   ┌──────────────────────┐ │
       ┌───────────┐ │ MOTO         │   │   ModalidadCobro     │ │
       │EstadoVeh. │ └──────────────┘   ├──────────────────────┤ │
       ├───────────┤                    │ HORA                 │ │
       │EN_PARQUEO │                    │ DIA                  │ │
       │SALIDA     │                    │ SEMANA               │ │
       └───────────┘                    │ MES                  │ │
                                        └──────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    DOMAIN — REPOSITORIES                        │
├─────────────────────────┐  ┌──────────────────────────────────┐ │
│    IAuthRepository      │  │      IParqueoRepository          │ │
├─────────────────────────┤  ├──────────────────────────────────┤ │
│ + login(): Resource     │  │ + registrarEntrada(): Resource   │ │
│ + logout()              │  │ + registrarSalida(): Resource    │ │
│ + getUsuarioActual()    │  │ + buscarPorPlaca(): Resource     │ │
│ + crearUsuario()        │  │ + getTarifas(): Resource         │ │
│ + registrarIntento()    │  │ + actualizarTarifa(): Resource   │ │
│ + desbloquearUsuario()  │  │ + getParqueaderoInfo(): Resource │ │
│ + observeAuthState()    │  │ + observarVehiculos(): Flow      │ │
└─────────────────────────┘  │ + observarDisponibilidad(): Flow │ │
                             └──────────────────────────────────┘ │
┌─────────────────────────┐                                       │
│    IAdminRepository     │  ┌──────────────────────────────────┐ │
├─────────────────────────┤  │    CalcularCobroUseCase          │ │
│ + getUsuarios()         │  ├──────────────────────────────────┤ │
│ + observarUsuarios()    │  │ + invoke(entrada, salida,        │ │
│ + actualizarUsuario()   │  │         tipo, modalidad,         │ │
│ + eliminarUsuario()     │  │         tarifas): Double         │ │
│ + getReporteIngresos()  │  └──────────────────────────────────┘ │
└─────────────────────────┘                                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    DATA — IMPLEMENTACIONES                      │
├──────────────────────────┐  ┌─────────────────────────────────┐ │
│  AuthRepositoryImpl      │  │    ParqueoRepositoryImpl        │ │
│  implementa IAuthRepo.   │  │    implementa IParqueoRepo.     │ │
├──────────────────────────┤  └─────────────────────────────────┘ │
│ - firebase: FirebaseSvc  │                                      │
└──────────────────────────┘  ┌─────────────────────────────────┐ │
                              │    AdminRepositoryImpl          │ │
┌──────────────────────────┐  │    implementa IAdminRepo.       │ │
│    FirebaseService       │  └─────────────────────────────────┘ │
├──────────────────────────┤                                      │
│ - auth: FirebaseAuth     │  ┌─────────────────────────────────┐ │
│ - db: FirebaseFirestore  │  │         UsuarioMapper           │ │
│ + login()                │  ├─────────────────────────────────┤ │
│ + logout()               │  │ + toDomain(dto): Usuario        │ │
│ + getUsuario()           │  │ + toDTO(usuario): UsuarioDTO    │ │
│ + saveUsuario()          │  └─────────────────────────────────┘ │
│ + saveVehiculo()         │                                      │
│ + updateVehiculo()       │  ┌─────────────────────────────────┐ │
│ + getTarifas()           │  │         VehiculoMapper          │ │
│ + observarVehiculos()    │  ├─────────────────────────────────┤ │
└──────────────────────────┘  │ + toDomain(dto): Vehiculo       │ │
                              │ + toDTO(vehiculo): VehiculoDTO  │ │
                              └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Diagrama de Paquetes

```
com.example.parkify
│
├── 📦 domain
│   ├── 📦 model
│   │   ├── Usuario.kt
│   │   ├── Vehiculo.kt
│   │   ├── Tarifa.kt
│   │   └── Parqueadero.kt
│   ├── 📦 repository
│   │   ├── IAuthRepository.kt
│   │   ├── IParqueoRepository.kt
│   │   └── IAdminRepository.kt
│   └── 📦 usecase
│       └── CalcularCobroUseCase.kt
│
├── 📦 data
│   ├── 📦 remote
│   │   ├── 📦 firebase
│   │   │   ├── FirebaseService.kt
│   │   │   └── 📦 models
│   │   │       ├── UsuarioDTO.kt
│   │   │       ├── VehiculoDTO.kt
│   │   │       └── TarifaDTO.kt
│   │   └── 📦 mappers
│   │       ├── UsuarioMapper.kt
│   │       └── VehiculoMapper.kt
│   ├── 📦 local
│   │   └── PreferencesManager.kt
│   └── 📦 repository
│       ├── AuthRepositoryImpl.kt
│       ├── ParqueoRepositoryImpl.kt
│       └── AdminRepositoryImpl.kt
│
├── 📦 ui
│   ├── 📦 auth
│   │   ├── LoginScreen.kt
│   │   └── AuthViewModel.kt
│   ├── 📦 admin
│   │   ├── DashboardAdminScreen.kt
│   │   ├── AdminViewModel.kt
│   │   ├── ResumenAdminTab.kt
│   │   ├── ConfigTarifasTab.kt
│   │   ├── UsuariosTab.kt
│   │   └── ReportesTab.kt
│   ├── 📦 operario
│   │   ├── ControlParqueoScreen.kt
│   │   └── OperarioViewModel.kt
│   ├── 📦 common
│   │   └── LoadingDialog.kt
│   ├── 📦 theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── ParkifyNavHost.kt
│
├── 📦 di
│   └── AppModule.kt
│
├── 📦 utils
│   ├── Resource.kt
│   ├── Constants.kt
│   ├── Extensions.kt
│   └── NetworkMonitor.kt
│
├── MainActivity.kt
└── ParkifyApp.kt
```

**Relaciones entre paquetes:**
```
ui ──────────────→ domain (usa interfaces y modelos)
data ────────────→ domain (implementa interfaces)
di ──────────────→ data + domain (conecta implementaciones)
ui NO depende de data (nunca importa directamente)
domain NO depende de ui ni data
```

---

## 4. Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                    DISPOSITIVO ANDROID                       │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                  CAPA DE PRESENTACIÓN               │   │
│  │                                                     │   │
│  │  ┌────────────┐  ┌──────────────┐  ┌────────────┐  │   │
│  │  │LoginScreen │  │DashboardAdmin│  │ControlParq │  │   │
│  │  └─────┬──────┘  └──────┬───────┘  └─────┬──────┘  │   │
│  │        │                │                 │         │   │
│  │  ┌─────▼──────┐  ┌──────▼───────┐  ┌─────▼──────┐  │   │
│  │  │AuthViewModel│ │AdminViewModel│  │OperarioVM  │  │   │
│  │  └─────┬──────┘  └──────┬───────┘  └─────┬──────┘  │   │
│  └────────┼────────────────┼─────────────────┼─────────┘   │
│           │                │                 │             │
│  ┌────────▼────────────────▼─────────────────▼─────────┐   │
│  │                  CAPA DE DOMINIO                    │   │
│  │                                                     │   │
│  │  ┌────────────────┐  ┌──────────────────────────┐  │   │
│  │  │ IAuthRepository│  │   IParqueoRepository     │  │   │
│  │  └────────────────┘  └──────────────────────────┘  │   │
│  │                                                     │   │
│  │  ┌──────────────────────────────────────────────┐  │   │
│  │  │         CalcularCobroUseCase                 │  │   │
│  │  └──────────────────────────────────────────────┘  │   │
│  └────────┬────────────────────────────────────────────┘   │
│           │                                                 │
│  ┌────────▼────────────────────────────────────────────┐   │
│  │                   CAPA DE DATOS                     │   │
│  │                                                     │   │
│  │  ┌──────────────────┐  ┌──────────────────────────┐│   │
│  │  │AuthRepositoryImpl│  │ ParqueoRepositoryImpl    ││   │
│  │  └────────┬─────────┘  └────────────┬─────────────┘│   │
│  │           │                         │              │   │
│  │  ┌────────▼─────────────────────────▼──────────┐  │   │
│  │  │              FirebaseService                │  │   │
│  │  └────────┬────────────────────────────────────┘  │   │
│  │           │                                       │   │
│  │  ┌────────▼──────────────┐                       │   │
│  │  │   PreferencesManager  │ ← DataStore local     │   │
│  │  └───────────────────────┘                       │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
          │                          │
          ▼                          ▼
┌──────────────────┐      ┌─────────────────────┐
│  FIREBASE AUTH   │      │   CLOUD FIRESTORE   │
│                  │      │                     │
│ • Autenticación  │      │ • usuarios          │
│ • Sesiones       │      │ • vehiculos         │
│ • Seguridad      │      │ • tarifas           │
└──────────────────┘      │ • config            │
                          └─────────────────────┘
```

---

## 5. Mapa de Navegación

```
                    ┌─────────────────┐
                    │   APP INICIO    │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   LOGIN SCREEN  │
                    │                 │
                    │  [Correo]       │
                    │  [Contraseña]   │
                    │  [Ingresar]     │
                    │  [¿Olvidaste?]  │
                    └────────┬────────┘
                             │
               ┌─────────────┴─────────────┐
               │                           │
     ROL: ADMINISTRADOR            ROL: OPERARIO
               │                           │
    ┌──────────▼──────────┐     ┌──────────▼──────────┐
    │   DASHBOARD ADMIN   │     │  CONTROL PARQUEO    │
    │                     │     │                     │
    │  ┌───────────────┐  │     │  ┌───────────────┐  │
    │  │  Tab: INICIO  │  │     │  │ Tab: ENTRADA  │  │
    │  │               │  │     │  │               │  │
    │  │ • Carros lib. │  │     │  │ • Placa       │  │
    │  │ • Motos lib.  │  │     │  │ • Propietario │  │
    │  │ • Ocupados    │  │     │  │ • Tipo vehic. │  │
    │  │ • Ingresos    │  │     │  │ • Modalidad   │  │
    │  └───────────────┘  │     │  └───────────────┘  │
    │                     │     │                     │
    │  ┌───────────────┐  │     │  ┌───────────────┐  │
    │  │ Tab: PARQUEO  │  │     │  │  Tab: SALIDA  │  │
    │  │               │  │     │  │               │  │
    │  │ (misma vista  │  │     │  │ • Buscar placa│  │
    │  │  operario)    │  │     │  │ • Ver datos   │  │
    │  └───────────────┘  │     │  │ • Total cobro │  │
    │                     │     │  │ • Confirmar   │  │
    │  ┌───────────────┐  │     │  └───────────────┘  │
    │  │ Tab: TARIFAS  │  │     │                     │
    │  │               │  │     │  ┌───────────────┐  │
    │  │ • Ver tarifas │  │     │  │ Tab: ACTIVOS  │  │
    │  │ • Editar c/u  │  │     │  │               │  │
    │  │ • Guardar     │  │     │  │ • Lista vehi. │  │
    │  └───────────────┘  │     │  │ • Placa, tipo │  │
    │                     │     │  │ • Hora entrada│  │
    │  ┌───────────────┐  │     │  └───────────────┘  │
    │  │ Tab: USUARIOS │  │     │                     │
    │  │               │  │     │  [Cerrar sesión]    │
    │  │ • Lista users │  │     └─────────────────────┘
    │  │ • Crear user  │  │
    │  │ • Desbloquear │  │
    │  │ • Eliminar    │  │
    │  └───────────────┘  │
    │                     │
    │  ┌───────────────┐  │
    │  │ Tab: REPORTES │  │
    │  │               │  │
    │  │ • Ingr. hoy   │  │
    │  │ • Ingr. mes   │  │
    │  │ • # carros    │  │
    │  │ • # motos     │  │
    │  │ • Historial   │  │
    │  └───────────────┘  │
    │                     │
    │  [Cerrar sesión]    │
    └─────────────────────┘

FLUJOS ESPECIALES:
──────────────────
[¿Olvidaste contraseña?] → Dialog con teléfono del admin
[5 intentos fallidos]    → Cuenta bloqueada → Contactar admin
[Sin internet]           → NetworkMonitor detecta → Alerta
```

---

## 6. Librerías de la Capa de Presentación

### Librerías principales

| Librería | Versión | Propósito |
|---------|---------|-----------|
| `androidx.compose.ui` | BOM 2024.09.03 | Motor de renderizado de Compose |
| `androidx.compose.material3` | BOM 2024.09.03 | Componentes Material Design 3 |
| `androidx.compose.material:material-icons-extended` | BOM | Iconos de Material Design |
| `androidx.activity:activity-compose` | 1.9.2 | Integración de Compose con Activity |
| `androidx.hilt:hilt-navigation-compose` | 1.2.0 | ViewModels con Hilt en Compose |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.6 | Ciclo de vida y ViewModels |

### Componentes de Material3 usados en Parkify

| Componente | Pantalla donde se usa |
|-----------|----------------------|
| `Scaffold` | Todas las pantallas |
| `TopAppBar` | Dashboard Admin, Operario |
| `NavigationBar` + `NavigationBarItem` | Dashboard Admin |
| `TabRow` + `Tab` | Control de Parqueo |
| `OutlinedTextField` | Login, Entrada, Salida, Usuarios |
| `Button` | Login, Entrada, Salida |
| `TextButton` | Login (¿Olvidaste?) |
| `FilterChip` | Tipo de vehículo, Modalidad |
| `Card` + `CardDefaults` | Todas las pantallas |
| `AlertDialog` | ¿Olvidaste?, Confirmar eliminar |
| `SnackbarHost` + `Snackbar` | Mensajes de éxito/error |
| `CircularProgressIndicator` | Estados de carga |
| `LinearProgressIndicator` | Búsqueda de placa |
| `HorizontalDivider` | Detalles de vehículo |
| `AssistChip` | Modalidad en lista de activos |

### ¿Por qué Material Design 3?

- Es el **sistema de diseño oficial de Google** para Android
- Soporte nativo en Jetpack Compose
- Temas dinámicos con `ColorScheme`
- Componentes accesibles por defecto
- Consistencia visual con el ecosistema Android

### Patrón de estado en la UI

Cada pantalla sigue el patrón **UiState + ViewModel**:

```kotlin
// 1. Estado de la UI como data class
data class OperarioUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val vehiculosActivos: List<Vehiculo> = emptyList(),
    val carrosDisponibles: Int = 0
)

// 2. ViewModel expone el estado como StateFlow
val uiState: StateFlow<OperarioUiState> = _uiState.asStateFlow()

// 3. Composable observa el estado
val uiState by viewModel.uiState.collectAsState()
```

Este patrón garantiza que la UI siempre refleje el estado actual de los datos, siguiendo el principio de **fuente única de verdad**.
