package de.tebbeubben.chargequicktile

const val ADAPTIVE_CHARGING_SETTING = "adaptive_charging_enabled"
const val CHARGE_OPTIMIZATION_MODE = "charge_optimization_mode"
const val PREF_ENABLE_ADAPTIVE = "pref_enable_adaptive"
const val PREF_ENABLE_LIMIT = "pref_enable_limit"
const val PREF_ENABLE_OFF = "pref_enable_off"
val WATCH_FOR = listOf(ADAPTIVE_CHARGING_SETTING, CHARGE_OPTIMIZATION_MODE)

enum class ChargingTileMode {
    ADAPTIVE,
    LIMIT_80,
    OFF,
}
