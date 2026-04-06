package de.tebbeubben.chargequicktile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.charging_optimization)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val adaptive = requireNotNull(findPreference<CheckBoxPreference>(PREF_ENABLE_ADAPTIVE))
        val limit = requireNotNull(findPreference<CheckBoxPreference>(PREF_ENABLE_LIMIT))
        val off = requireNotNull(findPreference<CheckBoxPreference>(PREF_ENABLE_OFF))
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        fun enabledCount(): Int = listOf(
            prefs.getBoolean(PREF_ENABLE_ADAPTIVE, true),
            prefs.getBoolean(PREF_ENABLE_LIMIT, true),
            prefs.getBoolean(PREF_ENABLE_OFF, true),
        ).count { it }

        fun enforceMinimum() {
            val count = enabledCount()
            adaptive.isEnabled = !(count <= 2 && adaptive.isChecked)
            limit.isEnabled = !(count <= 2 && limit.isChecked)
            off.isEnabled = !(count <= 2 && off.isChecked)
        }

        val listener = Preference.OnPreferenceChangeListener { _, newValue ->
            val willEnable = newValue as Boolean
            willEnable || enabledCount() > 2
        }

        adaptive.onPreferenceChangeListener = listener
        limit.onPreferenceChangeListener = listener
        off.onPreferenceChangeListener = listener

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener { _, _ ->
            enforceMinimum()
        }

        enforceMinimum()
    }
}
