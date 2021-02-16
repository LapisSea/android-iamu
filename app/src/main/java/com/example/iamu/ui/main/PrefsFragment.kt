package com.example.iamu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.iamu.R

const val SETTINGS_SHARED_PREFERENCES_FILE_NAME: String = "com.example.iamu.ui.main.SETTINGS_SHARED_PREFERENCES_FILE_NAME"

class PrefsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SETTINGS_SHARED_PREFERENCES_FILE_NAME
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        preferenceManager.sharedPreferencesName = SETTINGS_SHARED_PREFERENCES_FILE_NAME
        return super.onPreferenceTreeClick(preference)
    }
}