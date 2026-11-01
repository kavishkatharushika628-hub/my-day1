package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val fullDisplayFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val headerDisplayFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private val dayOfMonthFormat = SimpleDateFormat("d", Locale.getDefault())
    private val monthDayFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    fun getTodayKey(): String {
        return keyFormat.format(Date())
    }

    fun formatKey(date: Date): String {
        return keyFormat.format(date)
    }

    fun formatKeyFromTimestamp(millis: Long): String {
        return keyFormat.format(Date(millis))
    }

    fun parseKey(key: String): Date? {
        return try {
            keyFormat.parse(key)
        } catch (_: Exception) {
            null
        }
    }

    fun formatKeyForDetailedDisplay(key: String): String {
        val todayKey = getTodayKey()
        val date = parseKey(key) ?: return key
        val formatted = fullDisplayFormat.format(date)
        return if (key == todayKey) "Today • $formatted" else formatted
    }

    fun formatKeyForHeader(key: String): String {
        val date = parseKey(key) ?: return key
        return headerDisplayFormat.format(date)
    }

    fun formatKeyForShortDisplay(key: String): String {
        val todayKey = getTodayKey()
        if (key == todayKey) return "Today"

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = keyFormat.format(cal.time)
        if (key == yesterdayKey) return "Yesterday"

        val date = parseKey(key) ?: return key
        return monthDayFormat.format(date)
    }

    fun getDayOfWeekShort(key: String): String {
        val todayKey = getTodayKey()
        if (key == todayKey) return "Today"
        val date = parseKey(key) ?: return ""
        return dayOfWeekFormat.format(date)
    }

    fun getDayOfMonthNumber(key: String): String {
        val date = parseKey(key) ?: return ""
        return dayOfMonthFormat.format(date)
    }

    fun getPreviousDateKey(currentKey: String): String {
        val date = parseKey(currentKey) ?: Date()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return keyFormat.format(cal.time)
    }

    fun getNextDateKey(currentKey: String): String {
        val date = parseKey(currentKey) ?: Date()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return keyFormat.format(cal.time)
    }

    fun getRecentDays(daysCount: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until daysCount) {
            list.add(keyFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return list
    }
}
