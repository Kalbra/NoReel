package com.example.noreel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager


class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    class AndroidJSInterface(private val preference_button: Button) {
        @JavascriptInterface
        fun log(msg: String) {
            Log.d("WebInternal", msg)
        }

        @JavascriptInterface
        fun setSettingsMenuButton() {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(Runnable { preference_button.visibility = View.VISIBLE })
        }

        @JavascriptInterface
        fun deleteSettingsMenuButton() {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(Runnable { preference_button.visibility = View.GONE })
        }
    }


    private lateinit var webView: WebView

    var injector_content = ""


    val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_CANCELED) {
            filePathCallback?.onReceiveValue(null)
        } else if (it.resultCode == Activity.RESULT_OK && filePathCallback != null) {
            filePathCallback!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(it.resultCode, it.data)
            )
            filePathCallback = null
            Log.d("WebInternal", it.toString())
        }
    }

    private fun updateBrowser(webView: WebView, preferences: SharedPreferences) {
        try {
            if (preferences.all.getValue("use_followed_feed") as Boolean) {
                webView.loadUrl("https://www.instagram.com/?variant=following")
            } else {
                webView.loadUrl("https://www.instagram.com/")
            }
        } catch (e :NoSuchElementException){
            webView.loadUrl("https://www.instagram.com/")
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, p1: String?) {
        Log.d("Settings", "Changed: ${preferences?.all?.entries?.toTypedArray().contentToString()}")
        injector_content = createInjectionString(preferences)
    }

    private fun createInjectionString(preferences: SharedPreferences?): String {
        val reader = application.assets.open("Injector.js").bufferedReader()
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
                                val state = preferences?.all?.getValue(identifier) as Boolean
                                // On true execute
                                if (identifier == "use_followed_feed") {
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
                            } catch (e: NoSuchElementException){
                                Log.w("InjectionCompiler", "Preference '${identifier}' not found")
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
                Log.d("InjectionCompiler", injector_string)
                return injector_string
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instagram_webview)

        val readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE

        // readExternalStorage
        if (!(ContextCompat.checkSelfPermission(
                this,
                readExternalStorage
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(readExternalStorage), 23)
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)

        injector_content = createInjectionString(preferences)

        val preferences_button = findViewById<Button>(R.id.preferencesButton)

        preferences_button.setOnClickListener {
            val settings_intent = Intent(this, SettingsActivity::class.java)
            startActivity(settings_intent)
        }

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.allowContentAccess = true

        webView.addJavascriptInterface(AndroidJSInterface(preferences_button), "Android")

        // If application is in debug mode
        if(0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE){
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "WebInternal",
                    "${consoleMessage.message()} -- Line: ${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}"
                )
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                Log.w("WebInternal", request.toString())
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                Log.d("WebInternal", "New File dialog")
                this@MainActivity.filePathCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"

                this@MainActivity.getFile.launch(intent)

                return true
            }
        }

        fun injectJS(webview: WebView?) {
            webview?.loadUrl("javascript:(function f(){${injector_content}})()")
        }

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                injectJS(webView)
                mainHandler.postDelayed(this, 200)
            }
        })

        //Injection if the page is fully loaded
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                injectJS(view)
                super.onPageFinished(view, url)
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        })

        updateBrowser(webView, preferences)
    }
}

