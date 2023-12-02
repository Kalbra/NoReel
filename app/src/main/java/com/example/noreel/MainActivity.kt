package com.example.noreel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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


class MainActivity : ComponentActivity() {
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
            Log.d("Visible", "Here")
        }

        @JavascriptInterface
        fun deleteSettingsMenuButton() {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(Runnable { preference_button.visibility = View.GONE })
            Log.d("Visible", "gone")
        }
    }


    private lateinit var webView: WebView


    val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_CANCELED) {
            filePathCallback?.onReceiveValue(null)
        } else if (it.resultCode == Activity.RESULT_OK && filePathCallback != null){
            filePathCallback!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(it.resultCode, it.data)
            )
            filePathCallback = null
            Log.d("WebInternal", it.toString())
        }
    }

    fun updateBrowser(webView: WebView, preferences: MutableMap<String, *>){
        if(preferences.getValue("use_followed_feed") as Boolean){
            webView.loadUrl("https://www.instagram.com/?variant=following")
        } else {
            webView.loadUrl("https://www.instagram.com/")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instagram_webview)

        val readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE

        val preferences = PreferenceManager.getDefaultSharedPreferences(this).all

        // readExternalStorage
        if(!(ContextCompat.checkSelfPermission(this, readExternalStorage) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, arrayOf(readExternalStorage), 23)
        }

        // Opening assets files
        var injector_content = ""
        assets.open("Checker.js").bufferedReader().use {
            injector_content = it.readText()
        }

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.allowContentAccess = true

        val preferences_button = findViewById<Button>(R.id.preferencesButton)

        preferences_button.setOnClickListener {
            val settings_intent = Intent(this, SettingsActivity::class.java)
            startActivity(settings_intent)
        }

        webView.addJavascriptInterface(AndroidJSInterface(preferences_button), "Android")

        WebView.setWebContentsDebuggingEnabled(true)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("WebInternal", "${consoleMessage.message()} -- Line: ${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
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

        webView.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                injectChecker(webView)
                super.onLoadResource(view, url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                injectChecker(view);
                super.onPageFinished(view, url)
            }

            fun injectChecker(webview: WebView?){
                webview?.loadUrl("javascript:(function f(){${injector_content}})()")
            }
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        })

        updateBrowser(webView, preferences)
    }


}

