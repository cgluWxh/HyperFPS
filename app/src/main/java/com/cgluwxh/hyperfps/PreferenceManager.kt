package com.cgluwxh.hyperfps

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "HyperFPSPrefs"
    private const val KEY_TILE_OFF_REFRESH_RATE = "tile_off_refresh_rate"
    private const val KEY_TILE_ON_REFRESH_RATE = "tile_on_refresh_rate"
    private const val KEY_TILE_STATE = "tile_state"
    
    private const val DEFAULT_OFF_REFRESH_RATE = 60
    private const val DEFAULT_ON_REFRESH_RATE = 120

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveTileOffRefreshRate(context: Context, refreshRate: Int) {
        getPreferences(context).edit().putInt(KEY_TILE_OFF_REFRESH_RATE, refreshRate).apply()
    }

    fun getTileOffRefreshRate(context: Context): Int {
        return getPreferences(context).getInt(KEY_TILE_OFF_REFRESH_RATE, DEFAULT_OFF_REFRESH_RATE)
    }

    fun saveTileOnRefreshRate(context: Context, refreshRate: Int) {
        getPreferences(context).edit().putInt(KEY_TILE_ON_REFRESH_RATE, refreshRate).apply()
    }

    fun getTileOnRefreshRate(context: Context): Int {
        return getPreferences(context).getInt(KEY_TILE_ON_REFRESH_RATE, DEFAULT_ON_REFRESH_RATE)
    }

    fun saveTileState(context: Context, isOn: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_TILE_STATE, isOn).apply()
    }

    fun getTileState(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_TILE_STATE, false)
    }
}
