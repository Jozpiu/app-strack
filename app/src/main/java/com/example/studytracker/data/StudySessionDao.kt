package com.example.studytracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: StudySession)

    @Query("SELECT * FROM study_sessions")
    fun observeAll(): Flow<List<StudySession>>
}
