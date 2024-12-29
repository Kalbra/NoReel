package com.example.noreel

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.util.Log
import android.webkit.WebView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch


class InjectionBuilder(
    private val application: Application,
    private val preferences: SharedPreferences
) {
    fun fetchRemote(callback: (String) -> Unit) {
        val latch = CountDownLatch(1)
        val urlString =
            "https://raw.githubusercontent.com/Kalbra/NoReel/master/app/src/main/assets/Injector.js"

        Thread {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 1000
                connection.readTimeout = 1000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = BufferedInputStream(connection.inputStream)

                    val injection_string = reader(inputStream)
                    callback(injection_string)
                } else {
                    Log.e("InjectionBuilder", "Error response code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("InjectionBuilder", "Error fetching JavaScript file: ${e.message}")
            } finally {
                latch.countDown()
            }
        }.start()
    }

    fun fetchLocal(callback: (String) -> Unit) {
        val input_stream = this.application.assets.open("Injector.js")
        callback(reader(input_stream))
    }

    fun getCode(callback: (String) -> Unit) {
        var state = getState()

        // If application is in debug mode
        if (0 != application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            state = false
        }

        if (state) { //remote
            fetchRemote { callback(it) }
            Log.d("InjectionBuilder", "Use remote script")
        } else { // local
            fetchLocal { callback(it) }
            Log.d("InjectionBuilder", "Use local script")
        }
    }

    private fun getState(): Boolean {
        var state = false
        try {
            state = preferences.all?.getValue("remote_fetching") as Boolean
        } catch (e: NoSuchElementException) {
            Log.w("InjectionBuilder", "Could not find 'remote_fetching' preference")
            //state = false
        }
        return state // true = remote, false = local
    }

    private fun reader(input_stream: InputStream): String {
        val reader = input_stream.bufferedReader()
        reader.useLines {
            reader.use {
                var add_future_lines = false
                var injector_string = ""
                for (raw_line in it.lines().toArray()) {
                    val line: String = raw_line.toString()

                    if (line.startsWith("/**")) {
                        //Using the space to split the element and to find out its identifier e.g REEL_FEED
                        val identifier = line.split(" ").toTypedArray()[1]

                        // No lines till the next identifier
                        if (identifier == "END") {
                            add_future_lines = false
                        }

                        // No checking for preference rules at ALWAYS_EXECUTE
                        else if (identifier == "ALWAYS_EXECUTE") {
                            add_future_lines = true
                        }

                        // Settings Identifier was found
                        else {
                            try {
                                val state = preferences.all?.getValue(identifier) as Boolean
                                // On true execute
                                if (identifier == "use_followed_feed" || identifier == "audio_on") {
                                    if (state) {
                                        add_future_lines = true
                                    }
                                }
                                // On false execute
                                else {
                                    if (!state) {
                                        add_future_lines = true
                                    }
                                }
                            } catch (e: NoSuchElementException) {
                                Log.w("InjectionBuilder", "Preference '${identifier}' not found")
                            }
                        }
                    }
                    // Normal content to be added
                    else {
                        // Checking if this content line (no identifier) should be added
                        if (add_future_lines && line != "") {
                            injector_string += line.trim()
                        }
                    }
                }
                Log.d("InjectionBuilder", injector_string)
                return injector_string
            }
        }
    }
}