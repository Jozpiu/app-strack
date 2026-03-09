package com.example.studytracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytracker.data.StudyRepository
import com.example.studytracker.data.StudySession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val updatedAt: String = "--:--:--",
    val todayHours: Double = 0.0,
    val weekHours: Double = 0.0,
    val monthHours: Double = 0.0,
    val weekGoal: Double = 30.0,
    val monthGoal: Double = 100.0,
    val sessionsThisMonth: Int = 0,
    val currentStreakDays: Int = 0,
    val consistency: Int = 0
) {
    val weekProgress: Float
        get() = (weekHours / weekGoal).coerceIn(0.0, 1.0).toFloat()

    val monthProgress: Float
        get() = (monthHours / monthGoal).coerceIn(0.0, 1.0).toFloat()
}

class HomeViewModel(private val repository: StudyRepository) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        viewModelScope.launch {
            repository.observeSessions().collectLatest { sessions ->
                uiState = buildUiState(sessions)
            }
        }
    }

    fun addStudyHours(hoursInput: String) {
        val hours = hoursInput.replace(',', '.').toDoubleOrNull() ?: return
        if (hours <= 0.0) return

        viewModelScope.launch {
            repository.addHours(hours)
        }
    }

    private fun buildUiState(sessions: List<StudySession>): HomeUiState {
        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        val month = YearMonth.now()

        var todayHours = 0.0
        var weekHours = 0.0
        var monthHours = 0.0
        var sessionsThisMonth = 0
        val studiedDates = mutableSetOf<LocalDate>()

        for (session in sessions) {
            val date = LocalDate.ofEpochDay(session.dateEpochDay)
            studiedDates.add(date)

            if (date == today) todayHours += session.hours

            if (!date.isBefore(weekStart) && !date.isAfter(today)) weekHours += session.hours

            if (YearMonth.from(date) == month) {
                monthHours += session.hours
                sessionsThisMonth++
            }
        }

        val streak = calculateStreak(studiedDates, today)

        // Dias com sessão esta semana / dias já decorridos na semana
        val daysElapsedInWeek = (today.dayOfWeek.value - DayOfWeek.MONDAY.value + 1).coerceAtLeast(1)
        val daysStudiedThisWeek = studiedDates.count { !it.isBefore(weekStart) && !it.isAfter(today) }
        val consistency = ((daysStudiedThisWeek.toDouble() / daysElapsedInWeek) * 100).toInt()

        return HomeUiState(
            updatedAt = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            todayHours = todayHours,
            weekHours = weekHours,
            monthHours = monthHours,
            sessionsThisMonth = sessionsThisMonth,
            currentStreakDays = streak,
            consistency = consistency
        )
    }

    private fun calculateStreak(studiedDates: Set<LocalDate>, today: LocalDate): Int {
        // Se hoje ainda não tem sessão, começa a contar a partir de ontem
        var cursor = if (studiedDates.contains(today)) today else today.minusDays(1)
        var streak = 0
        while (studiedDates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
