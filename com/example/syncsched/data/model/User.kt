package com.example.syncsched.data.model


enum class UserRole {
    ADMIN, HOD
}

data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val role: UserRole = UserRole.HOD,
    val department: String = ""
)

