package com.example.studytracker.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class StudyRepository(private val dao: StudySessionDao) {
    fun observeSessions(): Flow<List<StudySession>> = dao.observeAll()

    suspend fun addHours(hours: Double, date: LocalDate = LocalDate.now()) {
        dao.insert(
            StudySession(
                dateEpochDay = date.toEpochDay(),
                hours = hours
            )
        )
    }
}
