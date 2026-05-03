package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.Seed
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.UserEntity
import java.security.MessageDigest

class AuthRepository(
    private val userDao: UserDao,
    private val medalDao: MedalDao,
    private val prefs: UserPreferences
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
        val newUser = UserEntity(
            name = name.trim(),
            email = email.lowercase().trim(),
            passwordHash = hash(password)
        )
        val id = userDao.insert(newUser)
        // Carga medallas iniciales
        medalDao.insertAll(Seed.medalsForUser(id))
        prefs.setCurrentUserId(id)
        return AuthResult.Success(id)
    }

    suspend fun login(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Completa todos los campos.")
        }
        val user = userDao.findByEmail(email.lowercase().trim())
            ?: return AuthResult.Error("Usuario no encontrado.")
        if (user.passwordHash != hash(password)) {
            return AuthResult.Error("Contraseña incorrecta.")
        }
        prefs.setCurrentUserId(user.id)
        return AuthResult.Success(user.id)
    }

    suspend fun logout() {
        prefs.setCurrentUserId(-1L)
    }

    suspend fun deleteAccount(userId: Long) {
        userDao.deleteById(userId)
        prefs.setCurrentUserId(-1L)
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
