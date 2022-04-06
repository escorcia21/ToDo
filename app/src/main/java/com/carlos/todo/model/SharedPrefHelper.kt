package com.carlos.todo.model
import android.content.Context

class SharedPrefHelper(context: Context) {
    companion object {
        private const val DARK_STATUS = "DARK_STATUS"
    }

    private val preferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()
}