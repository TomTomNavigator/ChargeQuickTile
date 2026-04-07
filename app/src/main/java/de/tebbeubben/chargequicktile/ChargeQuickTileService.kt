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
        val adaptiveChargingEnabled = Settings.Secure.getInt(contentResolver, ADAPTIVE_CHARGING_SETTING) == 1
        val nextModeIsLimitTo80 = adaptiveChargingEnabled

        Settings.Secure.putInt(contentResolver, CHARGE_OPTIMIZATION_MODE, if (nextModeIsLimitTo80) 1 else 0)
        Settings.Secure.putInt(contentResolver, ADAPTIVE_CHARGING_SETTING, if (nextModeIsLimitTo80) 0 else 1)

        updateTile()
    }

    fun updateTile() {
        val adaptiveChargingEnabled = Settings.Secure.getInt(contentResolver, ADAPTIVE_CHARGING_SETTING) == 1
        val chargeOptimizationEnabled = Settings.Secure.getInt(contentResolver, CHARGE_OPTIMIZATION_MODE) == 1
        val showingLimitTo80 = chargeOptimizationEnabled && !adaptiveChargingEnabled

        qsTile.state = Tile.STATE_ACTIVE
        qsTile.subtitle = if (showingLimitTo80) {
            getString(R.string.limit_to_80)
        } else {
            getString(R.string.adaptive_charging)
        }
        qsTile.icon = Icon.createWithResource(this, if (showingLimitTo80) {
            R.drawable.battery_android_frame_shield_24px
        } else {
            R.drawable.battery_android_frame_plus_24px
        })
        qsTile.updateTile()
    }

}