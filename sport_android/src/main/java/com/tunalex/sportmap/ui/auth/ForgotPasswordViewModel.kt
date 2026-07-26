package com.tunalex.sportmap.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val step: Int = 1, // 1: pedir email, 2: responder pregunta y elegir nueva contraseña
    val email: String = "",
    val question: String? = null,
    val answer: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class ForgotPasswordViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun onEmail(v: String) = update { copy(email = v, error = null) }
    fun onAnswer(v: String) = update { copy(answer = v, error = null) }
    fun onNewPassword(v: String) = update { copy(newPassword = v, error = null) }
    fun onNewPasswordConfirm(v: String) = update { copy(newPasswordConfirm = v, error = null) }

    fun lookupQuestion() {
        val email = _state.value.email
        if (email.isBlank()) {
            update { copy(error = "Ingresa tu email.") }
            return
        }
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            val question = repo.getSecurityQuestion(email)
            if (question == null) {
                update {
                    copy(
                        loading = false,
                        error = "No encontramos una cuenta con ese email en este dispositivo."
                    )
                }
            } else {
                update { copy(loading = false, question = question, step = 2) }
            }
        }
    }

    fun resetPassword() {
        val s = _state.value
        if (s.newPassword != s.newPasswordConfirm) {
            update { copy(error = "Las contraseñas no coinciden.") }
            return
        }
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val r = repo.resetPassword(s.email, s.answer, s.newPassword)) {
                is AuthRepository.AuthResult.Success ->
                    update { copy(loading = false, success = true) }
                is AuthRepository.AuthResult.Error ->
                    update { copy(loading = false, error = r.message) }
            }
        }
    }

    private inline fun update(f: ForgotPasswordUiState.() -> ForgotPasswordUiState) {
        _state.value = _state.value.f()
    }
}
