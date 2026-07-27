package com.example.syncsched.navigation


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object FacultyManagement : Screen("faculty_management")
    object CourseManagement : Screen("course_management")
    object SectionManagement : Screen("section_management")
    object TimetableGenerator : Screen("timetable_generator")
}

