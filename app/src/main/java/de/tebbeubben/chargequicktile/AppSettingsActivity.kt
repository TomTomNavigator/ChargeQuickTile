package de.tebbeubben.chargequicktile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView

class AppSettingsActivity : Activity() {

    private lateinit var adaptiveCheckbox: CheckBox
    private lateinit var limitCheckbox: CheckBox
    private lateinit var offCheckbox: CheckBox
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)

        adaptiveCheckbox = findViewById(R.id.adaptiveCheckbox)
        limitCheckbox = findViewById(R.id.limitCheckbox)
        offCheckbox = findViewById(R.id.offCheckbox)
        errorText = findViewById(R.id.errorText)

        val modes = enabledModes(this)
        adaptiveCheckbox.isChecked = ChargingTileMode.ADAPTIVE in modes
        limitCheckbox.isChecked = ChargingTileMode.LIMIT_80 in modes
        offCheckbox.isChecked = ChargingTileMode.OFF in modes

        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            persistSelection()
        }

        adaptiveCheckbox.setOnCheckedChangeListener(listener)
        limitCheckbox.setOnCheckedChangeListener(listener)
        offCheckbox.setOnCheckedChangeListener(listener)
    }

    private fun persistSelection() {
        val adaptive = adaptiveCheckbox.isChecked
        val limit = limitCheckbox.isChecked
        val off = offCheckbox.isChecked
        val selectedCount = listOf(adaptive, limit, off).count { it }

        if (selectedCount < 2) {
            errorText.visibility = View.VISIBLE
            return
        }

        errorText.visibility = View.GONE
        saveEnabledModes(this, adaptive, limit, off)
    }
}
