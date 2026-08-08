package com.example.skillflow.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class StreakCalculatorTest {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @Test
    fun `streak increases when active on next consecutive day`() {
        val lastDate = "2026-08-07"
        val today = "2026-08-08"
        val currentStreak = 5
        
        val newStreak = calculateStreak(currentStreak, lastDate, today)
        assertEquals(6, newStreak)
    }

    @Test
    fun `streak remains same when active on the same day`() {
        val lastDate = "2026-08-08"
        val today = "2026-08-08"
        val currentStreak = 5
        
        val newStreak = calculateStreak(currentStreak, lastDate, today)
        assertEquals(5, newStreak)
    }

    @Test
    fun `streak resets to 1 when a day is skipped`() {
        val lastDate = "2026-08-06"
        val today = "2026-08-08"
        val currentStreak = 5
        
        val newStreak = calculateStreak(currentStreak, lastDate, today)
        assertEquals(1, newStreak)
    }

    // Helper logic to test (should ideally be in a domain Util class)
    private fun calculateStreak(current: Int, last: String?, today: String): Int {
        if (last == null) return 1
        if (last == today) return current
        
        val lastCal = Calendar.getInstance().apply { time = dateFormatter.parse(last)!! }
        val todayCal = Calendar.getInstance().apply { time = dateFormatter.parse(today)!! }
        
        lastCal.add(Calendar.DAY_OF_YEAR, 1)
        
        return if (lastCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
            lastCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)) {
            current + 1
        } else {
            1
        }
    }
}
