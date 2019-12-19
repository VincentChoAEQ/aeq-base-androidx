package ca.aequilibrium.base.util

import android.app.Activity
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ca.aequilibrium.base.App
import ca.aequilibrium.base.R

/**
 * Created by Chris on 2018-09-29.
 */
object Preferences {

    private const val PREF = "PREF"
    private const val AUTH_TOKEN = "AUTH_TOKEN"
    private const val THEME = "THEME"

    private val preferences: SharedPreferences = App.instance.getSharedPreferences(PREF, Activity.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(AUTH_TOKEN, null)
        set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    val isNewUser: Boolean
        get() = token.isNullOrEmpty()

    var theme: Int
        get() = preferences.getInt(THEME, R.style.AppTheme_NoActionBar)
        set(value) = preferences.edit().putInt(THEME, value).apply()

}