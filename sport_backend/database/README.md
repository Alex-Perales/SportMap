# 📦 SportMap Database Setup

## 📊 Estructura de Base de Datos v2.0

Este proyecto ahora incluye una **estructura de BD profesional** inspirada en arquitecturas Fintech P2P, adaptada al contexto de **SportMap** (plataforma de actividades deportivas).

---

## 🎯 Qué Incluye

### **Tablas Base (Originales)**
- ✅ `users` — Usuarios del sistema
- ✅ `places` — Lugares deportivos
- ✅ `activities` — Actividades registradas
- ✅ `reservations` — Reservas de lugares
- ✅ `products` — Productos de la tienda
- ✅ `cart_items` — Carrito de compras
- ✅ `medals` — Sistema de gamificación

### **Tablas Nuevas (v2.0)**
- 🆕 `user_kyc` — Verificación de identidad (KYC)
- 🆕 `bank_accounts` — Cuentas bancarias vinculadas
- 🆕 `ratings` — Calificaciones P2P entre usuarios
- 🆕 `reviews` — Reseñas de lugares
- 🆕 `transactions` — Registro de transacciones y pagos
- 🆕 `disputes` — Gestión de conflictos
- 🆕 `complaints` — Quejas y reportes
- 🆕 `notifications` — Sistema de notificaciones

---

## 📂 Estructura del Proyecto

```
SportMap/
├── app/src/main/java/com/tunalex/sportmap/
│   └── data/local/
│       ├── SportMapDatabase.kt         ← BD principal (v2.0)
│       ├── entity/                      ← Entidades Room
│       │   ├── UserEntity.kt
│       │   ├── PlaceEntity.kt
│       │   ├── ActivityEntity.kt
│       │   ├── ReservationEntity.kt
│       │   ├── ProductEntity.kt
│       │   ├── CartItemEntity.kt
│       │   ├── MedalEntity.kt
│       │   ├── UserKycEntity.kt        ← NUEVO
│       │   ├── BankAccountEntity.kt    ← NUEVO
│       │   ├── RatingEntity.kt         ← NUEVO
│       │   ├── ReviewEntity.kt         ← NUEVO
│       │   ├── TransactionEntity.kt    ← NUEVO
│       │   ├── DisputeEntity.kt        ← NUEVO
│       │   ├── ComplaintEntity.kt      ← NUEVO
│       │   └── NotificationEntity.kt   ← NUEVO
│       └── dao/                         ← Data Access Objects
│           ├── UserDao.kt
│           ├── PlaceDao.kt
│           ├── ActivityDao.kt
│           ├── ReservationDao.kt
│           ├── ProductDao.kt
│           ├── CartDao.kt
│           ├── MedalDao.kt
│           ├── UserKycDao.kt           ← NUEVO
│           ├── BankAccountDao.kt       ← NUEVO
│           ├── RatingDao.kt            ← NUEVO
│           ├── ReviewDao.kt            ← NUEVO
│           ├── TransactionDao.kt       ← NUEVO
│           ├── DisputeDao.kt           ← NUEVO
│           ├── ComplaintDao.kt         ← NUEVO
│           └── NotificationDao.kt      ← NUEVO
│
└── database/
    ├── sql/                             ← Scripts SQL
    │   ├── 001_users.sql
    │   ├── 002_places.sql
    │   ├── ...
    │   └── 015_notifications.sql
    └── docs/
        └── DATABASE_STRUCTURE.md        ← Documentación completa
```

---

## 🚀 Guía de Migración (v1 → v2)

### **Paso 1: Actualizar la BD version**
Ya está actualizado a `version = 2` en `SportMapDatabase.kt`

### **Paso 2: Recompile el Proyecto**
```bash
./gradlew clean build
```

### **Paso 3: Los DAOs Nuevos Están Disponibles**
Ahora puedes inyectar los nuevos DAOs:

```kotlin
// En tu repository o ViewModel
class MyRepository(private val database: SportMapDatabase) {
    
    private val userKycDao = database.userKycDao()
    private val bankAccountDao = database.bankAccountDao()
    private val ratingDao = database.ratingDao()
    private val transactionDao = database.transactionDao()
    // ... etc
}
```

---

## 💡 Ejemplos de Uso

### **1. Verificación KYC**
```kotlin
// Guardar datos KYC
suspend fun updateKyc(userId: Long, userKyc: UserKycEntity) {
    userKycDao.insert(userKyc)
}

// Obtener estado KYC
val kycStatus: Flow<UserKycEntity?> = userKycDao.getUserKycByUserId(userId)
```

### **2. Calificaciones entre Usuarios**
```kotlin
// Usuario califica otro usuario
suspend fun rateUser(rating: RatingEntity) {
    ratingDao.insert(rating)
}

// Obtener calificación promedio
val avgRating: Flow<Double> = ratingDao.getAverageRatingForUser(userId)
```

### **3. Reseñas de Lugares**
```kotlin
// Dejar reseña en un lugar
suspend fun reviewPlace(review: ReviewEntity) {
    reviewDao.insert(review)
}

// Obtener reseñas de un lugar
val placeReviews: Flow<List<ReviewEntity>> = reviewDao.getReviewsForPlace(placeId)
```

### **4. Transacciones**
```kotlin
// Registrar compra
suspend fun recordTransaction(transaction: TransactionEntity) {
    transactionDao.insert(transaction)
}

// Obtener historial del usuario
val userTransactions: Flow<List<TransactionEntity>> = 
    transactionDao.getTransactionsForUser(userId)
```

### **5. Notificaciones**
```kotlin
// Enviar notificación
suspend fun sendNotification(notification: NotificationEntity) {
    notificationDao.insert(notification)
}

// Obtener notificaciones sin leer
val unreadNotifs: Flow<List<NotificationEntity>> = 
    notificationDao.getUnreadNotificationsForUser(userId)
```

---

## 🔒 Seguridad y Mejores Prácticas

- ✅ **Contraseñas**: Siempre hasheadas (no almacenar en texto plano)
- ✅ **Números de Cuenta**: Encriptados antes de guardar
- ✅ **Datos Sensibles**: Usar métodos de encriptación en la app
- ✅ **Auditoría**: Todos los registros tienen timestamps
- ✅ **Integridad Referencial**: Claves foráneas y cascadas configuradas

---

## 📝 SQL Scripts

Todos los scripts SQL están en `/database/sql/` para facilitar:
- Crear la BD desde cero (migración manual)
- Entender la estructura
- Migración a bases de datos externas

### Ejecutar todos los scripts:
```bash
# Copiar todos los .sql a tu herramienta de DB
# Ejemplo con SQLite:
sqlite3 sportmap.db < 001_users.sql
sqlite3 sportmap.db < 002_places.sql
# ... etc
```

---

## 📊 Diagramas de Relaciones

### Relaciones Principales:
```
users
  ├─→ activities
  ├─→ reservations
  ├─→ ratings (from/to)
  ├─→ reviews
  ├─→ transactions
  ├─→ disputes
  ├─→ complaints
  ├─→ notifications
  ├─→ bank_accounts
  └─→ user_kyc

places
  ├─→ reservations
  ├─→ activities
  ├─→ reviews
  └─→ complaints

products
  ├─→ cart_items
  └─→ transactions

reservations
  ├─→ ratings
  ├─→ disputes
  └─→ transactions
```

---

## 🧪 Testing

Para testing, Room proporciona helpers:

```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

@Test
fun testInsertUser() = runBlocking {
    val user = UserEntity(name = "Test", email = "test@example.com", passwordHash = "hash")
    userDao.insert(user)
    val result = userDao.getUserByEmail("test@example.com")
    assertNotNull(result)
}
```

---

## 📞 Soporte y Documentación Adicional

- 📖 **Estructura Completa**: Ver `DATABASE_STRUCTURE.md`
- 🗂️ **Scripts SQL**: Ver carpeta `database/sql/`
- 🔧 **Entities**: Ver `app/src/main/java/.../entity/`
- 💾 **DAOs**: Ver `app/src/main/java/.../dao/`

---

## ✅ Checklist de Implementación

- [x] Crear entidades Kotlin para todas las tablas
- [x] Crear DAOs con queries comunes
- [x] Crear scripts SQL
- [x] Actualizar `SportMapDatabase.kt`
- [x] Documentación completa
- [ ] Migración de datos (si tienes datos existentes)
- [ ] Testing de DAOs
- [ ] Integración con repositorios

---

**¡La BD está lista para usar! 🚀**

Para más detalles, consulta `DATABASE_STRUCTURE.md` o revisa los archivos de entidades y DAOs.
