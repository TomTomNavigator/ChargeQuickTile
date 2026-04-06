package de.tebbeubben.chargequicktile

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager

class ChargeQuickTileService : TileService() {

    private lateinit var app: ChargeQuickTileApp

    override fun onCreate() {
        app = application as ChargeQuickTileApp
    }

    override fun onTileAdded() {
        updateTile()
    }

    override fun onStartListening() {
        app.tileService = this
        updateTile()
    }

    override fun onStopListening() {
        app.tileService = null
    }

    override fun onClick() {
        val modes = enabledModes()
        val nextMode = modes.nextAfter(currentMode())
        applyMode(nextMode)
        updateTile()
    }

    private fun enabledModes(): List<ChargingTileMode> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val modes = buildList {
            if (prefs.getBoolean(PREF_ENABLE_ADAPTIVE, true)) add(ChargingTileMode.ADAPTIVE)
            if (prefs.getBoolean(PREF_ENABLE_LIMIT, true)) add(ChargingTileMode.LIMIT_80)
            if (prefs.getBoolean(PREF_ENABLE_OFF, true)) add(ChargingTileMode.OFF)
        }
        return if (modes.size >= 2) modes else listOf(ChargingTileMode.ADAPTIVE, ChargingTileMode.LIMIT_80)
    }

    private fun List<ChargingTileMode>.nextAfter(current: ChargingTileMode): ChargingTileMode {
        val index = indexOf(current)
        return if (index == -1) first() else this[(index + 1) % size]
    }

    private fun currentMode(): ChargingTileMode {
        val adaptiveChargingEnabled = Settings.Secure.getInt(contentResolver, ADAPTIVE_CHARGING_SETTING) == 1
        val chargeOptimizationEnabled = Settings.Secure.getInt(contentResolver, CHARGE_OPTIMIZATION_MODE) == 1
        return when {
            adaptiveChargingEnabled -> ChargingTileMode.ADAPTIVE
            chargeOptimizationEnabled -> ChargingTileMode.LIMIT_80
            else -> ChargingTileMode.OFF
        }
    }

    private fun applyMode(mode: ChargingTileMode) {
        when (mode) {
            ChargingTileMode.ADAPTIVE -> {
                Settings.Secure.putInt(contentResolver, CHARGE_OPTIMIZATION_MODE, 0)
                Settings.Secure.putInt(contentResolver, ADAPTIVE_CHARGING_SETTING, 1)
            }
            ChargingTileMode.LIMIT_80 -> {
                Settings.Secure.putInt(contentResolver, CHARGE_OPTIMIZATION_MODE, 1)
                Settings.Secure.putInt(contentResolver, ADAPTIVE_CHARGING_SETTING, 0)
            }
            ChargingTileMode.OFF -> {
                Settings.Secure.putInt(contentResolver, CHARGE_OPTIMIZATION_MODE, 0)
                Settings.Secure.putInt(contentResolver, ADAPTIVE_CHARGING_SETTING, 0)
            }
        }
    }

    fun updateTile() {
        val adaptiveChargingEnabled = Settings.Secure.getInt(contentResolver, ADAPTIVE_CHARGING_SETTING) == 1
        val chargeOptimizationEnabled = Settings.Secure.getInt(contentResolver, CHARGE_OPTIMIZATION_MODE) == 1
        qsTile.state = if (chargeOptimizationEnabled || adaptiveChargingEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.subtitle = when {
            chargeOptimizationEnabled -> getString(R.string.limit_to_80)
            adaptiveChargingEnabled -> getString(R.string.adaptive_charging)
            else -> getString(R.string.deactivated)
        }
        qsTile.icon = Icon.createWithResource(this, when {
            chargeOptimizationEnabled -> R.drawable.battery_android_frame_shield_24px
            adaptiveChargingEnabled -> R.drawable.battery_android_frame_plus_24px
            else -> R.drawable.battery_android_0_24px
        })
        qsTile.updateTile()
    }
}
