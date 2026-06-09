# 🔗 Conectar Android App con Docker PostgreSQL

## Opción 1: JDBC (Conexión Directa)

### Agregar dependencia en `build.gradle.kts`

```kotlin
dependencies {
    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.6.0")
}
```

### Crear clase de conexión

```kotlin
// app/src/main/java/com/tunalex/sportmap/data/remote/database/PostgresConnection.kt

package com.tunalex.sportmap.data.remote.database

import java.sql.Connection
import java.sql.DriverManager

object PostgresConnection {
    
    // Configuración
    private const val HOST = "10.0.2.2"           // Para emulador: 10.0.2.2
                                                   // Para device: "192.168.1.x"
    private const val PORT = 5432
    private const val DATABASE = "sportmap_db"
    private const val USER = "alex"
    private const val PASSWORD = "121416"
    
    private const val CONNECTION_STRING = 
        "jdbc:postgresql://$HOST:$PORT/$DATABASE"
    
    fun getConnection(): Connection {
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(
            CONNECTION_STRING,
            USER,
            PASSWORD
        )
    }
}
```

### Usar la conexión

```kotlin
// Ejemplo: Obtener usuarios
fun getAllUsers(): List<UserEntity> {
    val users = mutableListOf<UserEntity>()
    
    try {
        val connection = PostgresConnection.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM users")
        
        while (resultSet.next()) {
            users.add(
                UserEntity(
                    id = resultSet.getLong("id"),
                    name = resultSet.getString("name"),
                    email = resultSet.getString("email"),
                    passwordHash = resultSet.getString("password_hash"),
                    district = resultSet.getString("district"),
                    isPremium = resultSet.getBoolean("is_premium"),
                    createdAt = resultSet.getLong("created_at")
                )
            )
        }
        
        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    return users
}
```

---

## Opción 2: Room + Retrofit (Recomendado)

Room ya está configurado localmente. Para PostgreSQL remoto:

### Crear API REST (Intermedio)

```kotlin
// app/src/main/java/com/tunalex/sportmap/data/remote/api/SportMapApi.kt

package com.tunalex.sportmap.data.remote.api

import com.tunalex.sportmap.data.local.entity.UserEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface SportMapApi {
    
    @GET("api/users")
    suspend fun getAllUsers(): List<UserEntity>
    
    @POST("api/users")
    suspend fun createUser(@Body user: UserEntity): UserEntity
    
    @GET("api/notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Long): List<NotificationEntity>
}
```

### Crear Retrofit Instance

```kotlin
// app/src/main/java/com/tunalex/sportmap/data/remote/RetrofitInstance.kt

package com.tunalex.sportmap.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    
    // Para emulador: 10.0.2.2
    // Para device: tu IP real
    private const val BASE_URL = "http://10.0.2.2:8000/"
    
    val api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportMapApi::class.java)
    }
}
```

---

## Opción 3: Hybrid (Room + Remote)

Usar Room localmente Y PostgreSQL remoto:

```kotlin
// app/src/main/java/com/tunalex/sportmap/data/repository/HybridUserRepository.kt

package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.SportMapDatabase
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class HybridUserRepository(private val database: SportMapDatabase) {
    
    // Obtener del servidor
    suspend fun syncUsersFromServer() {
        try {
            val remoteUsers = RetrofitInstance.api.getAllUsers()
            
            // Guardar en BD local
            remoteUsers.forEach { user ->
                database.userDao().insert(user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Obtener de BD local (más rápido)
    fun getAllUsersLocal(): Flow<List<UserEntity>> {
        return database.userDao().getAllUsers()
    }
    
    // Crear usuario y sincronizar
    suspend fun createUserAndSync(user: UserEntity) {
        // 1. Crear localmente
        val localId = database.userDao().insert(user)
        
        // 2. Sincronizar con servidor
        try {
            RetrofitInstance.api.createUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

---

## 🔧 Configuración por Tipo de Ejecución

### Emulador Android
```kotlin
// Usa estos hosts:
// - 10.0.2.2 (host del emulador)
// - localhost NO funciona en emulador

val host = "10.0.2.2"
val port = 5432
val url = "jdbc:postgresql://$host:$port/sportmap_db"
```

### Dispositivo Físico
```kotlin
// Obtén tu IP real
// Windows/Mac/Linux: ipconfig / ifconfig

// Luego usa:
val host = "192.168.1.100"  // Tu IP real
val port = 5432
val url = "jdbc:postgresql://$host:$port/sportmap_db"
```

### Obtener tu IP Real

**Windows:**
```powershell
ipconfig
# Busca: IPv4 Address: 192.168.x.x
```

**Mac/Linux:**
```bash
ifconfig
# Busca: inet 192.168.x.x
```

---

## 📊 Ejemplo Completo: ViewModel con PostgreSQL

```kotlin
// app/src/main/java/com/tunalex/sportmap/ui/dashboard/DashboardViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.repository.HybridUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: HybridUserRepository
) : ViewModel() {
    
    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    fun loadUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Cargar del servidor primero
                repository.syncUsersFromServer()
                
                // Luego mostrar de BD local
                repository.getAllUsersLocal()
                    .collect { userList ->
                        _users.value = userList
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

---

## 🔐 Seguridad

### NO hagas esto en Producción:
```kotlin
// ❌ MALO: credenciales en código
val password = "121416"
```

### Mejor:
```kotlin
// ✅ BUENO: Variables de entorno o BuildConfig
import com.tunalex.sportmap.BuildConfig

val host = BuildConfig.DATABASE_HOST
val password = BuildConfig.DATABASE_PASSWORD
```

**En `build.gradle.kts`:**
```kotlin
buildTypes {
    debug {
        buildConfigField("String", "DATABASE_HOST", "\"10.0.2.2\"")
        buildConfigField("String", "DATABASE_PASSWORD", "\"121416\"")
    }
    release {
        buildConfigField("String", "DATABASE_HOST", "\"api.sportmap.com\"")
        buildConfigField("String", "DATABASE_PASSWORD", "\"***\"")
    }
}
```

---

## 🧪 Testing

```kotlin
// Tests con PostgreSQL
@RunWith(RobolectricTestRunner::class)
class UserRepositoryTest {
    
    private lateinit var repository: HybridUserRepository
    
    @Before
    fun setup() {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SportMapDatabase::class.java
        ).build()
        
        repository = HybridUserRepository(database)
    }
    
    @Test
    fun testLoadUsers() = runBlocking {
        repository.syncUsersFromServer()
        
        repository.getAllUsersLocal()
            .test()
            .assertValue { it.isNotEmpty() }
    }
}
```

---

## 📝 Resumen

| Opción | Ventaja | Desventaja |
|--------|---------|-----------|
| **JDBC Directo** | Simple, directo | Lento, sin offline |
| **Room + Retrofit** | Rápido, offline | Más complejo |
| **Hybrid** | Lo mejor de ambos | Más código |

**Recomendación:** Usa **Hybrid** (Room local + Retrofit remoto)

---

## 🚀 Próximos Pasos

1. Levanta Docker: `docker-compose up -d`
2. Elige opción de conexión
3. Implementa en tu app
4. Prueba con pgAdmin: http://localhost:5050
5. ¡A codificar!

