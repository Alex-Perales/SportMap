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
    val passwordConfirm: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onName(v: String) = update { copy(name = v, error = null) }
    fun onEmail(v: String) = update { copy(email = v, error = null) }
    fun onPassword(v: String) = update { copy(password = v, error = null) }
    fun onPasswordConfirm(v: String) = update { copy(passwordConfirm = v, error = null) }
    fun resetSuccess() = update { copy(success = false) }

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
        if (s.password != s.passwordConfirm) {
            update { copy(error = "Las contraseñas no coinciden.") }
            return
        }
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val r = repo.signUp(s.name, s.email, s.password)) {
                is AuthRepository.AuthResult.Success ->
                    update { copy(loading = false, success = true) }
                is AuthRepository.AuthResult.Error ->
                    update { copy(loading = false, error = r.message) }
            }
        }
    }

    private inline fun update(f: AuthUiState.() -> AuthUiState) {
        _state.value = _state.value.f()
    }
}
