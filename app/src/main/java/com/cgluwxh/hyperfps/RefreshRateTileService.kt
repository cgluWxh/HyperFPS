package com.cgluwxh.hyperfps

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class RefreshRateTileService : TileService() {
    
    companion object {
        private const val TAG = "RefreshRateTileService"
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        
        // Get current state and toggle
        val currentState = PreferenceManager.getTileState(this)
        val newState = !currentState
        
        // Get the refresh rates from preferences
        val refreshRate = if (newState) {
            PreferenceManager.getTileOnRefreshRate(this)
        } else {
            PreferenceManager.getTileOffRefreshRate(this)
        }
        
        // Apply the refresh rate
        Thread {
            val success = RefreshRateManager.setRefreshRate(this, refreshRate)
            if (success) {
                // Save the new state
                PreferenceManager.saveTileState(this, newState)
                Log.d(TAG, "Tile toggled to ${if (newState) "ON" else "OFF"}, refresh rate: $refreshRate Hz")
            } else {
                Log.e(TAG, "Failed to set refresh rate")
            }
            
            // Update tile UI
            updateTile()
        }.start()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        
        val isOn = PreferenceManager.getTileState(this)
        val offRate = PreferenceManager.getTileOffRefreshRate(this)
        val onRate = PreferenceManager.getTileOnRefreshRate(this)
        val currentRate = if (isOn) onRate else offRate
        
        tile.state = if (isOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = "HyperFPS"
        tile.subtitle = "${currentRate}Hz"
        
        // Update the tile
        tile.updateTile()
    }
}
