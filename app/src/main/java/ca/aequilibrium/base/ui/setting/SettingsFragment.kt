package ca.aequilibrium.base.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import ca.aequilibrium.base.R
import ca.aequilibrium.base.util.Preferences
import android.view.MenuInflater

class SettingsFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_main, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.equals(activity?.getString(R.string.preference_key_theme))) {
            Preferences.theme = if (sharedPreferences.getBoolean(activity?.getString(R.string.preference_key_theme),true) == false) R.style.AppTheme2_NoActionBar else  R.style.AppTheme_NoActionBar
            // recreate activity to activate new theme
            activity?.recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

}