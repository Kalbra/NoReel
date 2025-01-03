package com.example.noreel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import src.UpdateChecker
import kotlin.concurrent.thread


open class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var settingsChanged: Boolean = false

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

    private fun updateBrowser(webView: WebView) {
        webView.loadUrl("https://www.instagram.com/")
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, p1: String?) {
        Log.d("Settings", "Changed: ${preferences?.all?.entries?.toTypedArray().contentToString()}")
        InjectionBuilder(application, preferences!!).getCode() {
            injector_content = it
        }
    }

    override fun onResume() {
        super.onResume()
        if (settingsChanged) {
            settingsChanged = false
            updateBrowser(webView)
            Log.d("Settings", "Reload browser")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instagram_webview)

        val readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE

        // readExternalStorage
        if (ContextCompat.checkSelfPermission(
                this,
                readExternalStorage
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(readExternalStorage), 23)
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)


        val preferences_button = findViewById<Button>(R.id.preferencesButton)

        preferences_button.setOnClickListener {
            settingsChanged = true
            val settings_intent = Intent(this, SettingsActivity::class.java)
            startActivity(settings_intent)
        }
        onSharedPreferenceChanged(preferences, "")

        val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

        val notificationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.VIBRATE
        )
        if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.VIBRATE),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }

        val name = "Updates"
        val descriptionText = "Information if there is an update available"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Update", name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val updateChecker = UpdateChecker(this)
        updateChecker.fetchRemote()

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.allowContentAccess = true
        webView.overScrollMode = View.OVER_SCROLL_NEVER;
        webView.isVerticalScrollBarEnabled = false

        val JSInterface = AndroidJSInterface(preferences_button, this)
        webView.addJavascriptInterface(JSInterface, "Android")

        // If application is in debug mode
        if (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.webChromeClient =
            object : WebChromeClient() {
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

                //Changing loading image to black
                override fun getDefaultVideoPoster(): Bitmap? {
                    return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                }
            }

        fun injectJS(webview: WebView?) {
            webview?.loadUrl("javascript:(function f(){${injector_content}})()")
        }

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(
            object : Runnable {
                override fun run() {
                    injectJS(webView)
                    mainHandler.postDelayed(this, 200)
                }
            })

        //Injection if the page is fully loaded
        webView.webViewClient =
            object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    injectJS(view)
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    // Not working on Pixel3a API34 extension level 7 on slow internet
                    if (error!!.errorCode == -2) {
                        Log.e("WebInternal", error.description.toString() + error.errorCode)

                        view?.loadUrl("file:///android_asset/error.html")

                        // Check internet loop
                        thread {
                            while (!isOnline(this@MainActivity)) {
                            }

                            this@MainActivity.runOnUiThread(Runnable {
                                updateBrowser(webView)
                            });
                        }

                    } else {
                        super.onReceivedError(view, request, error)
                    }
                }
            }

        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                }
            })

        updateBrowser(webView)
    }
}

