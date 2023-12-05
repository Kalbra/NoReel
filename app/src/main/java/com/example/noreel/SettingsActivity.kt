package com.example.noreel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dcastalia.localappupdate.DownloadApk
import java.text.SimpleDateFormat
import java.util.Date

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.SettingsFragmentContainerView, SettingsFragment())
            .commit()
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        requireActivity().title = "Settings"
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if(preference.key.equals("autoupdater")){
            val url = "https://github.com/Kalbra/NoReel/releases/latest/download/NoReel.apk"
            val downloadApk = context?.let { DownloadApk(it) }
            Log.d("Update", "Looking for updates")

            downloadApk?.startDownloadingApk(url)

            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val timestamp = sdf.format(Date())

            downloadApk?.startDownloadingApk(url, timestamp)
        }
        return super.onPreferenceTreeClick(preference)
    }
}