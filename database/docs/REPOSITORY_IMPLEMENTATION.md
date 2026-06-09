# 🔧 Guía de Implementación - Repositorios y Casos de Uso

## 📌 Introducción

Los **repositorios** son la capa que conecta los DAOs con el resto de la aplicación. Cada repositorio encapsula la lógica de acceso a datos específica de un dominio.

---

## 📦 Estructura de Repositorios Recomendada

```
app/src/main/java/com/tunalex/sportmap/data/
├── local/
│   ├── entity/          ← Entidades Room
│   ├── dao/             ← Data Access Objects
│   └── database/        ← SportMapDatabase
│
├── repository/          ← Capa de acceso a datos (NUEVA)
│   ├── UserRepository.kt
│   ├── PlaceRepository.kt
│   ├── ActivityRepository.kt
│   ├── ReservationRepository.kt
│   ├── ProductRepository.kt
│   ├── TransactionRepository.kt
│   ├── RatingRepository.kt
│   ├── ReviewRepository.kt
│   ├── NotificationRepository.kt
│   ├── KycRepository.kt
│   ├── BankAccountRepository.kt
│   ├── DisputeRepository.kt
│   └── ComplaintRepository.kt
│
└── remote/             ← Futuro: API REST
    └── api/
        └── ApiClient.kt
```

---

## 🎯 Ejemplos de Implementación

### **1. UserRepository** (Usuarios)

```kotlin
// app/src/main/java/com/tunalex/sportmap/data/repository/UserRepository.kt

package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    // Registrar nuevo usuario
    suspend fun registerUser(user: UserEntity): Long {
        return userDao.insert(user)
    }
    
    // Obtener usuario por email (login)
    fun getUserByEmail(email: String): Flow<UserEntity?> {
        return userDao.getUserByEmail(email)
    }
    
    // Obtener usuario por ID
    fun getUserById(userId: Long): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }
    
    // Actualizar perfil
    suspend fun updateUserProfile(user: UserEntity) {
        userDao.update(user)
    }
    
    // Verificar si email existe
    suspend fun emailExists(email: String): Boolean {
        return userDao.getUserByEmailOnce(email) != null
    }
    
    // Obtener todos los usuarios (admin)
    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }
}
```

---

### **2. ReservationRepository** (Reservas con Transacciones)

```kotlin
package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.SportMapDatabase
import com.tunalex.sportmap.data.local.entity.NotificationEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReservationRepository @Inject constructor(
    private val database: SportMapDatabase
) {
    
    private val reservationDao = database.reservationDao()
    private val transactionDao = database.transactionDao()
    private val notificationDao = database.notificationDao()
    
    // Crear reserva + registrar transacción + enviar notificación
    suspend fun createReservationWithTransaction(
        reservation: ReservationEntity,
        transaction: TransactionEntity
    ): Boolean {
        return try {
            database.withTransaction {
                // 1. Crear reserva
                val reservationId = reservationDao.insert(reservation)
                
                // 2. Registrar transacción
                val txnWithRef = transaction.copy(
                    referenceId = reservationId,
                    referenceType = "reservation"
                )
                transactionDao.insert(txnWithRef)
                
                // 3. Enviar notificación
                val notification = NotificationEntity(
                    userId = reservation.userId,
                    type = "reservation_confirmed",
                    title = "Reserva Confirmada",
                    message = "Tu reserva en ${reservation.placeName} fue confirmada",
                    referenceId = reservationId,
                    referenceType = "reservation"
                )
                notificationDao.insert(notification)
                
                true
            }
        } catch (e: Exception) {
            false
        }
    }
    
    // Obtener reservas activas del usuario
    fun getUserActiveReservations(userId: Long): Flow<List<ReservationEntity>> {
        return reservationDao.getReservationsByUserAndStatus(userId, "confirmed")
    }
    
    // Cancelar reserva
    suspend fun cancelReservation(reservationId: Long, reason: String) {
        val reservation = reservationDao.getReservationByIdOnce(reservationId)
        reservation?.let {
            reservationDao.update(
                it.copy(status = "cancelled", cancellationReason = reason)
            )
        }
    }
    
    // Obtener historial de reservas
    fun getUserReservationHistory(userId: Long): Flow<List<ReservationEntity>> {
        return reservationDao.getReservationsByUser(userId)
    }
}
```

---

### **3. TransactionRepository** (Transacciones y Pagos)

```kotlin
package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.TransactionDao
import com.tunalex.sportmap.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    
    // Procesar compra
    suspend fun processPurchase(
        userId: Long,
        amount: Double,
        productId: Long,
        paymentMethod: String
    ): TransactionEntity? {
        return try {
            val transaction = TransactionEntity(
                userId = userId,
                type = "purchase",
                amount = amount,
                currency = "PEN",
                status = "completed",
                description = "Compra de producto",
                referenceId = productId,
                referenceType = "product",
                paymentMethod = paymentMethod,
                transactionHash = generateTransactionHash(),
                completedAt = System.currentTimeMillis()
            )
            
            transactionDao.insert(transaction)
            transaction
        } catch (e: Exception) {
            null
        }
    }
    
    // Obtener historial de usuario
    fun getUserTransactionHistory(userId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsForUser(userId)
    }
    
    // Obtener gasto total
    fun getUserTotalSpent(userId: Long): Flow<Double> {
        return transactionDao.getTotalSpentByUser(userId)
    }
    
    // Obtener transacciones por tipo
    fun getTransactionsByType(userId: Long, type: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(userId, type)
    }
    
    // Obtener transacciones en rango de fechas
    fun getTransactionsInDateRange(userId: Long, startTime: Long, endTime: Long): 
        Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsInDateRange(startTime, endTime)
            .map { allTxns -> allTxns.filter { it.userId == userId } }
    }
    
    // Reembolsar transacción
    suspend fun refundTransaction(transactionId: Long): Boolean {
        return try {
            // En un escenario real, conectarías con un processor de pagos
            transactionDao.update(
                TransactionEntity(
                    id = transactionId,
                    status = "refunded",
                    // ...
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun generateTransactionHash(): String {
        return UUID.randomUUID().toString()
    }
}
```

---

### **4. RatingRepository** (Calificaciones P2P)

```kotlin
package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.RatingDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.RatingEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RatingRepository @Inject constructor(
    private val ratingDao: RatingDao,
    private val userDao: UserDao
) {
    
    // Calificar usuario después de interacción
    suspend fun rateUser(
        fromUserId: Long,
        toUserId: Long,
        rating: Int,
        comment: String?,
        reservationId: Long?
    ): Boolean {
        return try {
            val ratingEntity = RatingEntity(
                fromUserId = fromUserId,
                toUserId = toUserId,
                rating = rating,
                comment = comment,
                reservationId = reservationId,
                createdAt = System.currentTimeMillis()
            )
            ratingDao.insert(ratingEntity)
            
            // Actualizar calificación promedio del usuario
            updateUserRating(toUserId)
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Obtener calificación promedio
    fun getUserAverageRating(userId: Long): Flow<Double> {
        return ratingDao.getAverageRatingForUser(userId)
    }
    
    // Obtener todas las calificaciones de un usuario
    fun getUserRatings(userId: Long): Flow<List<RatingEntity>> {
        return ratingDao.getRatingsForUser(userId)
    }
    
    // Obtener calificaciones dadas por usuario
    fun getUserGivenRatings(userId: Long): Flow<List<RatingEntity>> {
        return ratingDao.getRatingsFromUser(userId)
    }
    
    // Obtener cantidad de calificaciones
    fun getUserRatingCount(userId: Long): Flow<Int> {
        return ratingDao.getRatingCountForUser(userId)
    }
    
    // Verificar si ya fue calificado
    suspend fun alreadyRated(fromUserId: Long, toUserId: Long): Boolean {
        return ratingDao.getRatingBetweenUsers(fromUserId, toUserId) != null
    }
    
    // Actualizar rating promedio del usuario
    private suspend fun updateUserRating(userId: Long) {
        val avgRating = ratingDao.getAverageRatingForUser(userId)
            .collect { avg ->
                val user = userDao.getUserByIdOnce(userId)
                user?.let {
                    userDao.update(it.copy(rating = avg))
                }
            }
    }
}
```

---

### **5. NotificationRepository** (Notificaciones)

```kotlin
package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.NotificationDao
import com.tunalex.sportmap.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    
    // Enviar notificación
    suspend fun sendNotification(notification: NotificationEntity) {
        notificationDao.insert(notification)
    }
    
    // Obtener notificaciones sin leer
    fun getUnreadNotifications(userId: Long): Flow<List<NotificationEntity>> {
        return notificationDao.getUnreadNotificationsForUser(userId)
    }
    
    // Obtener todas las notificaciones
    fun getAllNotifications(userId: Long): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsForUser(userId)
    }
    
    // Marcar como leída
    suspend fun markAsRead(notificationId: Long) {
        notificationDao.markAsRead(notificationId)
    }
    
    // Marcar todas como leídas
    suspend fun markAllAsRead(userId: Long) {
        notificationDao.markAllAsRead(userId)
    }
    
    // Contar sin leer
    fun getUnreadCount(userId: Long): Flow<Int> {
        return notificationDao.getUnreadNotificationCount(userId)
    }
    
    // Eliminar notificación
    suspend fun deleteNotification(notificationId: Long) {
        notificationDao.delete(
            NotificationEntity(id = notificationId)
        )
    }
    
    // Limpiar notificaciones antiguas
    suspend fun cleanOldNotifications(userId: Long, daysOld: Int = 30) {
        val beforeTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000)
        notificationDao.deleteOldNotifications(userId, beforeTime)
    }
}
```

---

### **6. KycRepository** (Verificación de Identidad)

```kotlin
package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.UserKycDao
import com.tunalex.sportmap.data.local.entity.UserKycEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KycRepository @Inject constructor(
    private val userKycDao: UserKycDao
) {
    
    // Registrar KYC
    suspend fun submitKyc(kyc: UserKycEntity): Long {
        return userKycDao.insert(kyc)
    }
    
    // Obtener estado KYC
    fun getKycStatus(userId: Long): Flow<UserKycEntity?> {
        return userKycDao.getUserKycByUserId(userId)
    }
    
    // Actualizar KYC
    suspend fun updateKyc(kyc: UserKycEntity) {
        userKycDao.update(kyc)
    }
    
    // Aprobar KYC
    suspend fun approveKyc(userId: Long) {
        val kyc = userKycDao.getUserKycByUserIdOnce(userId)
        kyc?.let {
            userKycDao.update(
                it.copy(
                    status = "approved",
                    verifiedAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000)
                )
            )
        }
    }
    
    // Rechazar KYC
    suspend fun rejectKyc(userId: Long, reason: String) {
        val kyc = userKycDao.getUserKycByUserIdOnce(userId)
        kyc?.let {
            userKycDao.update(
                it.copy(
                    status = "rejected",
                    rejectionReason = reason
                )
            )
        }
    }
    
    // Obtener pendientes
    fun getPendingKyc(): Flow<List<UserKycEntity>> {
        return userKycDao.getKycByStatus("pending")
    }
}
```

---

## 📚 Integración con ViewModel

```kotlin
// Example: ReservationViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.repository.ReservationRepository
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {
    
    fun createReservation(reservation: ReservationEntity) {
        viewModelScope.launch {
            val success = reservationRepository.createReservationWithTransaction(
                reservation = reservation,
                transaction = /* ... */
            )
            if (success) {
                // Mostrar éxito
            }
        }
    }
}
```

---

## 🔌 Inyección de Dependencias (Hilt)

```kotlin
// Module para proporcionar repositorios

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Singleton
    @Provides
    fun provideUserRepository(
        database: SportMapDatabase
    ): UserRepository = UserRepository(database.userDao())
    
    @Singleton
    @Provides
    fun provideReservationRepository(
        database: SportMapDatabase
    ): ReservationRepository = ReservationRepository(database)
    
    @Singleton
    @Provides
    fun provideTransactionRepository(
        database: SportMapDatabase
    ): TransactionRepository = TransactionRepository(database.transactionDao())
    
    @Singleton
    @Provides
    fun provideRatingRepository(
        database: SportMapDatabase
    ): RatingRepository = RatingRepository(
        database.ratingDao(),
        database.userDao()
    )
    
    @Singleton
    @Provides
    fun provideNotificationRepository(
        database: SportMapDatabase
    ): NotificationRepository = NotificationRepository(database.notificationDao())
    
    // ... etc para otros repositorios
}
```

---

## 🧪 Testing de Repositorios

```kotlin
@RunWith(RobolectricTestRunner::class)
class ReservationRepositoryTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: SportMapDatabase
    private lateinit var repository: ReservationRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SportMapDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = ReservationRepository(database)
    }
    
    @Test
    fun testCreateReservation() = runBlocking {
        val reservation = ReservationEntity(
            userId = 1,
            placeId = 1,
            placeName = "Test Place",
            reservationDate = System.currentTimeMillis(),
            status = "confirmed"
        )
        
        val success = repository.createReservationWithTransaction(
            reservation = reservation,
            transaction = /* ... */
        )
        
        assertTrue(success)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
}
```

---

## 📋 Checklist de Implementación

- [ ] Crear `UserRepository`
- [ ] Crear `ReservationRepository`
- [ ] Crear `TransactionRepository`
- [ ] Crear `RatingRepository`
- [ ] Crear `ReviewRepository`
- [ ] Crear `NotificationRepository`
- [ ] Crear `KycRepository`
- [ ] Crear `BankAccountRepository`
- [ ] Crear módulo Hilt
- [ ] Integrar con ViewModels
- [ ] Escribir tests unitarios
- [ ] Documentar casos de uso

---

¡Los repositorios conectan toda tu lógica de datos! 🚀

