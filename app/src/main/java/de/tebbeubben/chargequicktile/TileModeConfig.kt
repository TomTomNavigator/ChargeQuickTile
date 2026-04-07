package de.tebbeubben.chargequicktile

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "tile_mode_preferences"
const val PREF_ENABLE_ADAPTIVE = "enable_adaptive"
const val PREF_ENABLE_LIMIT = "enable_limit"
const val PREF_ENABLE_OFF = "enable_off"

enum class ChargingTileMode {
    ADAPTIVE,
    LIMIT_80,
    OFF,
}

fun enabledModes(context: Context): List<ChargingTileMode> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val modes = buildList {
        if (prefs.getBoolean(PREF_ENABLE_ADAPTIVE, true)) add(ChargingTileMode.ADAPTIVE)
        if (prefs.getBoolean(PREF_ENABLE_LIMIT, true)) add(ChargingTileMode.LIMIT_80)
        if (prefs.getBoolean(PREF_ENABLE_OFF, false)) add(ChargingTileMode.OFF)
    }
    return if (modes.size >= 2) modes else listOf(ChargingTileMode.ADAPTIVE, ChargingTileMode.LIMIT_80)
}

fun saveEnabledModes(context: Context, adaptive: Boolean, limit: Boolean, off: Boolean) {
    val selectedCount = listOf(adaptive, limit, off).count { it }
    require(selectedCount >= 2) { "At least two tile modes must be enabled" }

    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
        putBoolean(PREF_ENABLE_ADAPTIVE, adaptive)
        putBoolean(PREF_ENABLE_LIMIT, limit)
        putBoolean(PREF_ENABLE_OFF, off)
    }
}
