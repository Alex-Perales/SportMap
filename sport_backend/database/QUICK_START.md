# 🎬 Guía Rápida de Inicio - SportMap Database v2.0

## ⚡ 5 Minutos para Empezar

### **Paso 1: Compilar el Proyecto**
```bash
cd SportMap
./gradlew clean build
```

Si hay errores de compilación, ejecuta:
```bash
./gradlew build --rerun-tasks
```

---

### **Paso 2: Usar un DAO Existente**

**Ejemplo: Obtener notificaciones sin leer**

```kotlin
// En tu ViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.SportMapDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel(private val database: SportMapDatabase) : ViewModel() {
    
    fun getUnreadNotifications(userId: Long) {
        viewModelScope.launch {
            database.notificationDao()
                .getUnreadNotificationsForUser(userId)
                .collectLatest { notifications ->
                    // Actualizar UI con notificaciones
                }
        }
    }
}
```

---

### **Paso 3: Crear una Notificación**

```kotlin
// Crear y enviar una notificación
suspend fun sendNotification(userId: Long) {
    val notification = NotificationEntity(
        userId = userId,
        type = "reservation_confirmed",
        title = "Reserva Confirmada",
        message = "Tu reserva fue confirmada exitosamente",
        createdAt = System.currentTimeMillis()
    )
    
    database.notificationDao().insert(notification)
}
```

---

### **Paso 4: Registrar una Transacción**

```kotlin
// Registrar una compra
suspend fun recordPurchase(userId: Long, amount: Double, productId: Long) {
    val transaction = TransactionEntity(
        userId = userId,
        type = "purchase",
        amount = amount,
        currency = "PEN",
        status = "completed",
        referenceId = productId,
        referenceType = "product",
        paymentMethod = "card",
        completedAt = System.currentTimeMillis()
    )
    
    database.transactionDao().insert(transaction)
}
```

---

### **Paso 5: Calificar a un Usuario**

```kotlin
// Usuario 1 califica a Usuario 2
suspend fun rateUser(fromUserId: Long, toUserId: Long, rating: Int) {
    val ratingEntity = RatingEntity(
        fromUserId = fromUserId,
        toUserId = toUserId,
        rating = rating,                    // 1-5 estrellas
        comment = "Excelente persona",
        createdAt = System.currentTimeMillis()
    )
    
    database.ratingDao().insert(ratingEntity)
}
```

---

## 🎯 Casos de Uso Comunes

### **1. Verificación KYC**

```kotlin
// Enviar documentos para verificación
suspend fun submitKyc(userId: Long, docUrl: String, selfieUrl: String) {
    val kyc = UserKycEntity(
        userId = userId,
        documentType = "DNI",
        documentNumber = "12345678",
        documentFrontUrl = docUrl,
        selfieUrl = selfieUrl,
        status = "pending"
    )
    
    database.userKycDao().insert(kyc)
}

// Ver estado de verificación
fun getKycStatus(userId: Long) {
    database.userKycDao()
        .getUserKycByUserId(userId)
        .collect { kyc ->
            when (kyc?.status) {
                "approved" -> println("✅ Verificado")
                "pending" -> println("⏳ En revisión")
                "rejected" -> println("❌ Rechazado: ${kyc.rejectionReason}")
            }
        }
}
```

---

### **2. Vincular Cuenta Bancaria**

```kotlin
// Agregar cuenta bancaria
suspend fun linkBankAccount(userId: Long, accountNumber: String, bankName: String) {
    val account = BankAccountEntity(
        userId = userId,
        accountType = "checking",
        bankName = bankName,
        accountNumber = accountNumber,  // Encriptar en producción
        accountHolderName = "Usuario",
        currency = "PEN",
        isDefault = true,
        verificationStatus = "verified"
    )
    
    database.bankAccountDao().insert(account)
}

// Obtener cuenta por defecto
fun getDefaultAccount(userId: Long) {
    database.bankAccountDao()
        .getDefaultBankAccount(userId)
        .collect { account ->
            // Usar para pagos
        }
}
```

---

### **3. Reseña de Lugar**

```kotlin
// Dejar reseña después de visitarlo
suspend fun reviewPlace(placeId: Long, userId: Long, rating: Int, comment: String) {
    val review = ReviewEntity(
        placeId = placeId,
        userId = userId,
        rating = rating,                // 1-5 estrellas
        title = "Excelente lugar",
        comment = comment,
        createdAt = System.currentTimeMillis()
    )
    
    database.reviewDao().insert(review)
}

// Ver reseñas de un lugar
fun getPlaceReviews(placeId: Long) {
    database.reviewDao()
        .getReviewsForPlace(placeId)
        .collect { reviews ->
            reviews.forEach { review ->
                println("${review.title}: ${review.rating}⭐")
            }
        }
}
```

---

### **4. Reportar Disputa**

```kotlin
// Usuario reporta problema con reserva
suspend fun reportDispute(
    reservationId: Long,
    userId: Long,
    reason: String,
    description: String
) {
    val dispute = DisputeEntity(
        reservationId = reservationId,
        initiatedByUserId = userId,
        reason = reason,
        description = description,
        status = "open"
    )
    
    database.disputeDao().insert(dispute)
}

// Ver disputas abiertas (para admin)
fun getOpenDisputes() {
    database.disputeDao()
        .getDisputesByStatus("open")
        .collect { disputes ->
            disputes.forEach { dispute ->
                println("Disputa: ${dispute.reason}")
            }
        }
}
```

---

### **5. Presentar Queja**

```kotlin
// Usuario presenta queja formal
suspend fun submitComplaint(
    userId: Long,
    againstUserId: Long,
    category: String,
    title: String,
    description: String
) {
    val complaint = ComplaintEntity(
        userId = userId,
        complaintAgainstUserId = againstUserId,
        category = category,            // harassment, fraud, bad_service
        title = title,
        description = description,
        severity = "medium",
        status = "submitted"
    )
    
    database.complaintDao().insert(complaint)
}
```

---

## 🧪 Testing Rápido

```kotlin
// Prueba en un test
@Test
fun testInsertNotification() = runBlocking {
    val database = Room.inMemoryDatabaseBuilder(
        context,
        SportMapDatabase::class.java
    ).build()
    
    val notification = NotificationEntity(
        userId = 1,
        type = "test",
        title = "Test Notification"
    )
    
    val id = database.notificationDao().insert(notification)
    
    assertThat(id).isGreaterThan(0)
    
    database.close()
}
```

---

## 📂 Estructura de Archivos

```
SportMap/
├── app/src/main/java/com/tunalex/sportmap/
│   └── data/local/
│       ├── SportMapDatabase.kt          ← Principal
│       ├── entity/                      ← 15 entidades
│       │   ├── UserEntity.kt
│       │   ├── NotificationEntity.kt
│       │   ├── TransactionEntity.kt
│       │   ├── RatingEntity.kt
│       │   ├── ReviewEntity.kt
│       │   ├── DisputeEntity.kt
│       │   ├── ComplaintEntity.kt
│       │   ├── UserKycEntity.kt
│       │   ├── BankAccountEntity.kt
│       │   └── ... (más)
│       └── dao/                         ← 15 DAOs
│           ├── NotificationDao.kt
│           ├── TransactionDao.kt
│           ├── RatingDao.kt
│           └── ... (más)
│
└── database/
    ├── sql/                             ← Scripts SQL
    │   ├── 001_users.sql
    │   ├── 015_notifications.sql
    │   └── master.sql
    ├── docs/                            ← Documentación
    │   ├── DATABASE_STRUCTURE.md
    │   ├── ENTITY_RELATIONSHIPS.md
    │   └── REPOSITORY_IMPLEMENTATION.md
    ├── README.md
    └── COMPLETION_SUMMARY.md
```

---

## ✅ Checklist de Verificación

- [ ] Proyecto compila sin errores
- [ ] Puedo inyectar `SportMapDatabase`
- [ ] Puedo acceder a `userKycDao()`
- [ ] Puedo insertar `NotificationEntity`
- [ ] Puedo leer con `Flow<List<...>>`
- [ ] Tests unitarios pasan

---

## 🔗 Relaciones Clave a Recordar

```
users (1) ──→ (N) notifications
users (1) ──→ (N) transactions
users (1) ──→ (N) ratings
users (1) ──→ (N) reviews
users (1) ──→ (N) disputes
users (1) ──→ (N) complaints
users (1) ──→ (1) user_kyc
users (1) ──→ (N) bank_accounts

reservations (1) ──→ (N) transactions
reservations (1) ──→ (N) disputes

places (1) ──→ (N) reviews
```

---

## 🆘 Troubleshooting

### **Error: "Cannot find symbol"**
```bash
# Solución: Recompilar
./gradlew clean build --rerun-tasks
```

### **Error: "Unresolved reference to UserKycDao"**
- Verifica que el archivo existe: `data/local/dao/UserKycDao.kt`
- Verifica los imports en `SportMapDatabase.kt`

### **Error: "No such table"**
- La BD se crea automáticamente al first run
- Si falta, borra la BD y reinstala:
```bash
./gradlew uninstallDebug
./gradlew installDebug
```

---

## 📞 Recursos

- 📖 [DATABASE_STRUCTURE.md](database/docs/DATABASE_STRUCTURE.md) — Especificación técnica
- 🔗 [ENTITY_RELATIONSHIPS.md](database/docs/ENTITY_RELATIONSHIPS.md) — Diagramas
- 💻 [REPOSITORY_IMPLEMENTATION.md](database/docs/REPOSITORY_IMPLEMENTATION.md) — Ejemplos avanzados

---

## 🎉 ¡Ya Estás Listo!

Con esto ya tienes una base de datos profesional. Ahora puedes:

1. ✅ Consultar datos
2. ✅ Insertar registros
3. ✅ Actualizar información
4. ✅ Manejar transacciones complejas
5. ✅ Enviar notificaciones
6. ✅ Registrar pagos
7. ✅ Gestionar calificaciones

**¡Empieza a construir! 🚀**

---

**Última actualización:** Junio 2026  
**Versión:** v2.0  
**Estado:** Listo para Producción
