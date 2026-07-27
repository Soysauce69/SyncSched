package com.example.syncsched.domain.engine

import com.example.syncsched.data.model.Faculty
import com.example.syncsched.data.model.TimetableSlot

sealed class ConflictResult {
    object Clear : ConflictResult()
    data class FacultyConflict(val facultyName: String, val day: String, val period: Int) : ConflictResult()
    data class SectionConflict(val sectionId: String, val day: String, val period: Int) : ConflictResult()
    data class WorkloadExceeded(val facultyName: String) : ConflictResult()
}

class ConflictDetectionEngine {

    fun validateSlot(
        newSlot: TimetableSlot,
        existingSlots: List<TimetableSlot>,
        faculties: List<Faculty>
    ): ConflictResult {
        // two sections != one faculty
        val facultyConflict = existingSlots.find {
            it.day == newSlot.day &&
                    it.period == newSlot.period &&
                    it.facultyId == newSlot.facultyId &&
                    it.id != newSlot.id
        }
        if (facultyConflict != null) {
            val faculty = faculties.find { it.id == newSlot.facultyId }
            return ConflictResult.FacultyConflict(
                facultyName = faculty?.name ?: newSlot.facultyId,
                day = newSlot.day,
                period = newSlot.period
            )
        }

        // one sec - no two classes
        val sectionConflict = existingSlots.find {
            it.day == newSlot.day &&
                    it.period == newSlot.period &&
                    it.sectionId == newSlot.sectionId &&
                    it.id != newSlot.id
        }
        if (sectionConflict != null) {
            return ConflictResult.SectionConflict(
                sectionId = newSlot.sectionId,
                day = newSlot.day,
                period = newSlot.period
            )
        }


        val faculty = faculties.find { it.id == newSlot.facultyId }
        if (faculty != null) {
            val assignedHours = existingSlots.count { it.facultyId == newSlot.facultyId && it.id != newSlot.id }
            if (assignedHours + 1 > faculty.maxWeeklyHours) {
                return ConflictResult.WorkloadExceeded(faculty.name)
            }
        }

        return ConflictResult.Clear
    }
}

