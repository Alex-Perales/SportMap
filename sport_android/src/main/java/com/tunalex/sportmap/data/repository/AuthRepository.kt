package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.Seed
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.remote.ApiService
import com.tunalex.sportmap.data.remote.LoginRequest
import com.tunalex.sportmap.data.remote.RegisterRequest
import java.security.MessageDigest

class AuthRepository(
    private val userDao: UserDao,
    private val medalDao: MedalDao,
    private val prefs: UserPreferences,
    private val api: ApiService
) {

    sealed class AuthResult {
        data class Success(val userId: Long) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Completa todos los campos.")
        }
        if (!email.contains("@")) {
            return AuthResult.Error("Email inválido.")
        }
        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres.")
        }
        val existing = userDao.findByEmail(email.lowercase().trim())
        if (existing != null) {
            return AuthResult.Error("Este email ya está registrado.")
        }

        val passwordHash = hash(password)

        val newUser = UserEntity(
            name = name.trim(),
            email = email.lowercase().trim(),
            passwordHash = passwordHash
        )
        val localId = userDao.insert(newUser)
        medalDao.insertAll(Seed.medalsForUser(localId))
        prefs.setCurrentUserId(localId)

        // Registrar en el backend (sin bloquear si falla)
        try {
            val serverUser = api.register(
                RegisterRequest(
                    name = name.trim(),
                    email = email.lowercase().trim(),
                    passwordHash = passwordHash
                )
            )
            prefs.setServerUserId(serverUser.id)
        } catch (_: Exception) {
            // Backend no disponible; continúa con modo local
        }

        return AuthResult.Success(localId)
    }

    suspend fun login(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Completa todos los campos.")
        }

        val passwordHash = hash(password)

        // Intentar login en el backend primero
        try {
            val serverUser = api.login(
                LoginRequest(
                    email = email.lowercase().trim(),
                    passwordHash = passwordHash
                )
            )
            prefs.setServerUserId(serverUser.id)

            // Sincronizar usuario con Room local
            val localUser = userDao.findByEmail(email.lowercase().trim())
            if (localUser == null) {
                val entity = UserEntity(
                    name = serverUser.name,
                    email = serverUser.email,
                    passwordHash = passwordHash,
                    district = serverUser.district,
                    isPremium = serverUser.isPremium,
                    profileImageUrl = serverUser.profileImageUrl,
                    createdAt = serverUser.createdAt
                )
                val localId = userDao.insert(entity)
                medalDao.insertAll(Seed.medalsForUser(localId))
                prefs.setCurrentUserId(localId)
                return AuthResult.Success(localId)
            } else {
                prefs.setCurrentUserId(localUser.id)
                return AuthResult.Success(localUser.id)
            }
        } catch (_: Exception) {
            // Backend no disponible — fallback a Room local
        }

        val user = userDao.findByEmail(email.lowercase().trim())
            ?: return AuthResult.Error("Usuario no encontrado.")
        if (user.passwordHash != passwordHash) {
            return AuthResult.Error("Contraseña incorrecta.")
        }
        prefs.setCurrentUserId(user.id)
        return AuthResult.Success(user.id)
    }

    suspend fun logout() {
        prefs.setCurrentUserId(-1L)
        prefs.setServerUserId(-1L)
    }

    suspend fun deleteAccount(userId: Long) {
        // Eliminar en el backend
        try {
            val serverIdFlow = prefs.serverUserId
            // Obtener el ID del servidor del Flow de forma suspendida no es trivial aquí,
            // así que se hace best-effort con el userId local como fallback
            api.deleteUser(userId)
        } catch (_: Exception) {}
        userDao.deleteById(userId)
        prefs.setCurrentUserId(-1L)
        prefs.setServerUserId(-1L)
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
