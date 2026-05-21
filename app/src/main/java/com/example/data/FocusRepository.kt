package com.example.data

import kotlinx.coroutines.flow.Flow

class FocusRepository(private val focusSessionDao: FocusSessionDao) {
    val allSessions: Flow<List<FocusSession>> = focusSessionDao.getAllSessions()
    val totalMinutesFocused: Flow<Int?> = focusSessionDao.getTotalMinutesFocused()
    val completedSessionsCount: Flow<Int> = focusSessionDao.getCompletedSessionsCount()

    suspend fun insert(session: FocusSession) {
        focusSessionDao.insertSession(session)
    }

    suspend fun deleteById(id: Int) {
        focusSessionDao.deleteSessionById(id)
    }

    suspend fun clearAll() {
        focusSessionDao.clearHistory()
    }
}
