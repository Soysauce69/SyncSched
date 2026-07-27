package com.example.syncsched.data.model


data class TimetableSlot(
    val id: String = "",
    val day: String = "", // Monday - Saturday
    val period: Int = 1,  // 1 to 8
    val facultyId: String = "",
    val courseId: String = "",
    val sectionId: String = ""
)

