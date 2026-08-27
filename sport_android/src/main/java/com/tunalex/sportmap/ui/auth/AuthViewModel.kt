package com.tunalex.sportmap.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val acceptedPolicy: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val justRegistered: Boolean = false
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onName(v: String) = update { copy(name = v, error = null) }
    fun onEmail(v: String) = update { copy(email = v, error = null) }
    fun onPassword(v: String) = update { copy(password = v, error = null) }
    fun onAcceptPolicy(v: Boolean) = update { copy(acceptedPolicy = v, error = null) }
    fun resetSuccess() = update { copy(success = false, justRegistered = false) }

    /** Al alternar entre "Iniciar sesión" y "Crear cuenta": limpia el error y
     *  la contraseña (no arrastramos credenciales de un modo al otro). */
    fun onSwitchMode() = update { copy(error = null, password = "") }

    fun login() {
        val s = _state.value
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val r = repo.login(s.email, s.password)) {
                is AuthRepository.AuthResult.Success ->
                    update { copy(loading = false, success = true) }
                is AuthRepository.AuthResult.Error ->
                    update { copy(loading = false, error = r.message) }
            }
        }
    }

    fun signUp() {
        val s = _state.value
        if (!s.acceptedPolicy) {
            update { copy(error = "Debes aceptar la Política de Privacidad para continuar.") }
            return
        }
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val r = repo.signUp(s.name, s.email, s.password)) {
                is AuthRepository.AuthResult.Success -> {
                    // La cuenta queda creada pero SIN sesión iniciada: el usuario
                    // debe entrar por "Iniciar sesión".
                    repo.logout()
                    update { copy(loading = false, success = true, justRegistered = true) }
                }
                is AuthRepository.AuthResult.Error ->
                    update { copy(loading = false, error = r.message) }
            }
        }
    }

    private inline fun update(f: AuthUiState.() -> AuthUiState) {
        _state.value = _state.value.f()
    }
}
