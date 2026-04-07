package de.tebbeubben.chargequicktile

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

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
        val modes = enabledModes(this)
        val nextMode = modes.nextAfter(currentMode())
        applyMode(nextMode)
        updateTile()
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
        when (currentMode()) {
            ChargingTileMode.ADAPTIVE -> {
                qsTile.state = Tile.STATE_ACTIVE
                qsTile.subtitle = getString(R.string.adaptive_charging)
                qsTile.icon = Icon.createWithResource(this, R.drawable.battery_android_frame_plus_24px)
            }

            ChargingTileMode.LIMIT_80 -> {
                qsTile.state = Tile.STATE_ACTIVE
                qsTile.subtitle = getString(R.string.limit_to_80)
                qsTile.icon = Icon.createWithResource(this, R.drawable.battery_android_frame_shield_24px)
            }

            ChargingTileMode.OFF -> {
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.subtitle = getString(R.string.off_mode)
                qsTile.icon = Icon.createWithResource(this, R.drawable.battery_android_0_24px)
            }
        }
        qsTile.updateTile()
    }
}
