package com.example.syncsched.domain.algorithm

import com.example.syncsched.data.model.ClassSection
import com.example.syncsched.data.model.Course
import com.example.syncsched.data.model.Faculty
import com.example.syncsched.data.model.TimetableSlot
import com.example.syncsched.domain.engine.ConflictDetectionEngine
import com.example.syncsched.domain.engine.ConflictResult
import java.util.UUID

class TimetableGenerator(
    private val conflictEngine: ConflictDetectionEngine = ConflictDetectionEngine()
) {
    private val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private val periods = (1..8).toList()

    fun generate(
        courses: List<Course>,
        faculties: List<Faculty>,
        sections: List<ClassSection>
    ): List<TimetableSlot> {
        val generatedSchedule = mutableListOf<TimetableSlot>()

        for (section in sections) {
            for (course in courses) {
                var assignedHours = 0

                dayLoop@ for (day in days) {
                    for (period in periods) {
                        if (assignedHours >= course.hoursPerWeek) break@dayLoop

                        val proposedSlot = TimetableSlot(
                            id = UUID.randomUUID().toString(),
                            day = day,
                            period = period,
                            facultyId = course.facultyId,
                            courseId = course.id,
                            sectionId = section.id
                        )

                        val result = conflictEngine.validateSlot(proposedSlot, generatedSchedule, faculties)
                        if (result is ConflictResult.Clear) {
                            generatedSchedule.add(proposedSlot)
                            assignedHours++
                        }
                    }
                }
            }
        }
        return generatedSchedule
    }
}
