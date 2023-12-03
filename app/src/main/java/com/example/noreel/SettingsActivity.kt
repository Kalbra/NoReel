package com.example.noreel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.SettingsFragmentContainerView, SettingsFragment())
            .commit()
    }

    override fun onStop() {

        super.onStop()
        setResult(RESULT_OK)
        finish()
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}