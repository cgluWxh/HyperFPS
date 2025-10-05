package com.cgluwxh.hyperfps

import android.content.Context
import android.provider.Settings
import android.util.Log

object RefreshRateManager {
    private const val TAG = "RefreshRateManager"
    private const val MIUI_REFRESH_RATE = "miui_refresh_rate"
    private const val USER_REFRESH_RATE = "user_refresh_rate"

    /**
     * Set the refresh rate using Settings.Secure API
     * Requires WRITE_SECURE_SETTINGS permission granted via ADB
     */
    fun setRefreshRate(context: Context, refreshRate: Int): Boolean {
        return try {
            // Write to both MIUI and user refresh rate settings
            val result1 = Settings.Secure.putInt(
                context.contentResolver,
                MIUI_REFRESH_RATE,
                refreshRate
            )
            
            val result2 = Settings.Secure.putInt(
                context.contentResolver,
                USER_REFRESH_RATE,
                refreshRate
            )
            
            if (result1 && result2) {
                Log.d(TAG, "Successfully set refresh rate to $refreshRate Hz")
                true
            } else {
                Log.e(TAG, "Failed to set refresh rate. MIUI: $result1, User: $result2")
                false
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: WRITE_SECURE_SETTINGS permission not granted", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error setting refresh rate: ${e.message}", e)
            false
        }
    }

    /**
     * Get the current refresh rate from system settings
     */
    fun getCurrentRefreshRate(context: Context): Int? {
        return try {
            val refreshRate = Settings.Secure.getInt(
                context.contentResolver,
                USER_REFRESH_RATE
            )
            Log.d(TAG, "Current refresh rate: $refreshRate Hz")
            refreshRate
        } catch (e: Settings.SettingNotFoundException) {
            Log.w(TAG, "Refresh rate setting not found", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current refresh rate: ${e.message}", e)
            null
        }
    }
    
    /**
     * Get the MIUI refresh rate setting
     */
    fun getMiuiRefreshRate(context: Context): Int? {
        return try {
            val refreshRate = Settings.Secure.getInt(
                context.contentResolver,
                MIUI_REFRESH_RATE
            )
            refreshRate
        } catch (e: Settings.SettingNotFoundException) {
            Log.w(TAG, "MIUI refresh rate setting not found", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MIUI refresh rate: ${e.message}", e)
            null
        }
    }
}
