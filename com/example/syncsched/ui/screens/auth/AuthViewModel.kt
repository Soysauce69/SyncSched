package com.example.syncsched.ui.screens.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncsched.data.model.User
import com.example.syncsched.data.model.UserRole
import com.example.syncsched.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")
    var department by mutableStateOf("")
    var role by mutableStateOf(UserRole.HOD)

    fun register(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || username.isBlank() || department.isBlank()) {
            authState = AuthState.Error("Please fill in all fields.")
            return
        }

        viewModelScope.launch {
            authState = AuthState.Loading
            val user = User(
                email = email.trim(),
                username = username.trim(),
                role = role,
                department = department.trim()
            )
            val result = authRepository.registerUser(user, password)
            if (result.isSuccess) {
                authState = AuthState.Success
                onSuccess()
            } else {
                authState = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            authState = AuthState.Error("Please enter email and password.")
            return
        }

        viewModelScope.launch {
            authState = AuthState.Loading
            val result = authRepository.loginUser(email.trim(), password)
            if (result.isSuccess) {
                authState = AuthState.Success
                onSuccess()
            } else {
                authState = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun clearError() {
        if (authState is AuthState.Error) {
            authState = AuthState.Idle
        }
    }
}
