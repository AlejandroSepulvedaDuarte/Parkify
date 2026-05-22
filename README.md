# 🅿️ Parkify

Sistema de control de ingreso y salida de vehículos para parqueaderos de carros y motos.

---

## 📱 Capturas de pantalla

| Login | Dashboard Admin | Registro Entrada | Reportes |
|-------|----------------|-----------------|---------|
| _(agregar screenshot)_ | _(agregar screenshot)_ | _(agregar screenshot)_ | _(agregar screenshot)_ |

---

## 🚀 Características

- 🔐 Autenticación con correo y contraseña (Firebase Auth)
- 👥 Dos roles: **Administrador** y **Operario**
- 🚗 Registro de entrada y salida de carros y motos
- ⏱️ Modalidades de cobro: por hora, día, semana o mes
- 💰 Cálculo automático del cobro al registrar salida
- 📊 Dashboard con estadísticas en tiempo real
- 📋 Reportes de ingresos diarios y mensuales
- 🔒 Bloqueo automático tras 5 intentos fallidos de login
- 📞 Muestra teléfono del administrador al olvidar contraseña
- 🌐 Monitor de conexión a internet

---

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** con tres capas:

```
app/
└── src/main/java/com/example/parkify/
    ├── domain/          # Lógica de negocio (puro Kotlin)
    │   ├── model/       # Entidades del dominio
    │   ├── repository/  # Interfaces de repositorios
    │   └── usecase/     # Casos de uso
    ├── data/            # Implementaciones concretas
    │   ├── remote/      # Firebase (Auth + Firestore)
    │   └── repository/  # Implementaciones de interfaces
    ├── ui/              # Capa de presentación (Jetpack Compose)
    │   ├── auth/        # Login
    │   ├── admin/       # Dashboard administrador
    │   ├── operario/    # Control de parqueo
    │   └── common/      # Componentes reutilizables
    ├── di/              # Inyección de dependencias (Hilt)
    └── utils/           # Utilidades y extensiones
```

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|-----------|-----|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Firebase Auth | Autenticación de usuarios |
| Cloud Firestore | Base de datos en tiempo real |
| Hilt | Inyección de dependencias |
| Coroutines + Flow | Programación asíncrona |
| DataStore | Preferencias locales |
| Material Design 3 | Sistema de diseño |

---

## ⚙️ Requisitos previos

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 26 o superior
- Cuenta en [Firebase Console](https://console.firebase.google.com)

---

## 🔧 Configuración inicial

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/parkify.git
cd parkify
```

### 2. Configurar Firebase

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com)
2. Agrega una app Android con package `com.example.parkify`
3. Descarga `google-services.json` y colócalo en `app/`
4. Activa **Authentication → Correo/Contraseña**
5. Activa **Cloud Firestore** en modo prueba

### 3. Crear datos iniciales en Firestore

#### Colección `config` → documento `parqueadero`

```json
{
  "nombre": "Parkify",
  "totalEspaciosCarros": 50,
  "totalEspaciosMotos": 30,
  "telefonoAdmin": "3001234567",
  "emailAdmin": "admin@parkify.com"
}
```

#### Colección `tarifas` (8 documentos, uno por combinación)

| tipo | modalidad | precio |
|------|-----------|--------|
| CARRO | HORA | 3000 |
| CARRO | DIA | 25000 |
| CARRO | SEMANA | 150000 |
| CARRO | MES | 500000 |
| MOTO | HORA | 2000 |
| MOTO | DIA | 15000 |
| MOTO | SEMANA | 80000 |
| MOTO | MES | 250000 |

#### Colección `usuarios` → documento con UID del admin

```json
{
  "uid": "UID_DEL_ADMIN",
  "nombre": "Administrador",
  "email": "admin@parkify.com",
  "rol": "ADMINISTRADOR",
  "telefono": "3001234567",
  "activo": true,
  "intentosFallidos": 0,
  "bloqueado": false
}
```

### 4. Crear el admin en Firebase Auth

1. Ve a Firebase Console → Authentication → Usuarios
2. Click en **Agregar usuario**
3. Ingresa el correo y contraseña (mínimo 6, máximo 8 caracteres)
4. Copia el UID generado y úsalo en el documento de Firestore

---

## 👤 Roles y permisos

| Funcionalidad | Administrador | Operario |
|--------------|:-------------:|:--------:|
| Ver disponibilidad | ✅ | ✅ |
| Registrar entrada | ✅ | ✅ |
| Registrar salida | ✅ | ✅ |
| Ver vehículos activos | ✅ | ✅ |
| Ver reportes | ✅ | ❌ |
| Configurar tarifas | ✅ | ❌ |
| Gestionar usuarios | ✅ | ❌ |
| Configurar parqueadero | ✅ | ❌ |

---

## 🔐 Seguridad

- Contraseñas entre **6 y 8 caracteres**
- Máximo **5 intentos** de login antes de bloqueo
- Cuentas bloqueadas solo pueden ser desbloqueadas por el administrador
- Contraseñas gestionadas por Firebase Auth (nunca en texto plano)

---

## 📁 Estructura de Firestore

```
firestore/
├── config/
│   └── parqueadero          # Configuración general del parqueadero
├── usuarios/
│   └── {uid}                # Un documento por usuario
├── vehiculos/
│   └── {id}                 # Un documento por registro de vehículo
└── tarifas/
    └── {id}                 # Un documento por tarifa
```

---

## 🤝 Contribuir

1. Haz fork del proyecto
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Haz commit: `git commit -m 'Agrega nueva funcionalidad'`
4. Haz push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 👨‍💻 Autor

Alejandro Sepúlveda Duarte & Lucy Estefany Izquiuerdo Jaramillo
