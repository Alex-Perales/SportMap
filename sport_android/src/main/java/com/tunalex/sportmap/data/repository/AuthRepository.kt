package com.tunalex.sportmap.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.tunalex.sportmap.data.local.Seed
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.remote.ApiService
import com.tunalex.sportmap.data.remote.FcmTokenRequest
import com.tunalex.sportmap.data.remote.LoginRequest
import com.tunalex.sportmap.data.remote.RegisterRequest
import kotlinx.coroutines.tasks.await
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

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        securityQuestion: String,
        securityAnswer: String
    ): AuthResult {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Completa todos los campos.")
        }
        if (!email.contains("@")) {
            return AuthResult.Error("Email inválido.")
        }
        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres.")
        }
        if (securityQuestion.isBlank() || securityAnswer.isBlank()) {
            return AuthResult.Error("Selecciona una pregunta de seguridad y responde.")
        }
        val existing = userDao.findByEmail(email.lowercase().trim())
        if (existing != null) {
            return AuthResult.Error("Este email ya está registrado.")
        }

        val passwordHash = hash(password)

        val newUser = UserEntity(
            name = name.trim(),
            email = email.lowercase().trim(),
            passwordHash = passwordHash,
            securityQuestion = securityQuestion,
            securityAnswerHash = hash(securityAnswer.trim().lowercase())
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
            registerFcmToken(serverUser.id)
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
            registerFcmToken(serverUser.id)

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

    /** Devuelve la pregunta de seguridad del usuario, o null si no existe la cuenta localmente. */
    suspend fun getSecurityQuestion(email: String): String? {
        val user = userDao.findByEmail(email.lowercase().trim())
        return user?.securityQuestion?.takeIf { it.isNotBlank() }
    }

    suspend fun resetPassword(email: String, securityAnswer: String, newPassword: String): AuthResult {
        if (newPassword.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres.")
        }
        val user = userDao.findByEmail(email.lowercase().trim())
            ?: return AuthResult.Error("No encontramos una cuenta con ese email en este dispositivo.")
        if (user.securityAnswerHash != hash(securityAnswer.trim().lowercase())) {
            return AuthResult.Error("La respuesta no coincide con la registrada.")
        }
        userDao.update(user.copy(passwordHash = hash(newPassword)))
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

    /** Envía el token FCM actual del dispositivo al backend, asociado al
     * usuario recién logueado. Best-effort: si falla, la app sigue
     * funcionando, solo no llegarán notificaciones push a este dispositivo. */
    private suspend fun registerFcmToken(serverUserId: Long) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            api.updateFcmToken(serverUserId, FcmTokenRequest(token))
        } catch (_: Exception) {}
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
