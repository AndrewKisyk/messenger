package com.dreamdev.testtask.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Preferences(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "shared_prefs_user"
        private const val FRAGMENTS_COUNT = "fragments count"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var fragmentsCount: Int
        get() = sharedPrefs.getInt(FRAGMENTS_COUNT, 1)
        set(value) = sharedPrefs.edit { putInt(FRAGMENTS_COUNT, value) }

}