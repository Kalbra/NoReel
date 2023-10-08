package com.example.noreel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    object AndroidJSInterface {
        @JavascriptInterface
        fun log(msg: String){
            Log.d("WebInternal", msg)
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        val readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE

        // readExternalStorage
        if(!(ContextCompat.checkSelfPermission(this, readExternalStorage) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, arrayOf(readExternalStorage), 23)
        }

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.allowContentAccess = true


        webView.addJavascriptInterface(AndroidJSInterface, "Android")

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
                //registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    //Log.d("WebInternal", uri.toString());
                    //filePathCallback?.
                // onReceiveValue(Array<Uri>(1){uri!!})
                //}.launch("image/*")
                //startForResult.launch(intent)

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"

                this@MainActivity.getFile.launch(intent)

                return true
            }
        }




        webView.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                injectJS(view)
                super.onLoadResource(view, url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                injectJS(view);
                super.onPageFinished(view, url)
            }

            fun injectJS(webview: WebView?){
                webview?.loadUrl("""javascript:(function f(){
                    function waitForElm(selector) {
                        return new Promise(resolve => {
                            if (document.querySelector(selector)) {
                                return resolve(document.querySelector(selector));
                            }
                    
                            const observer = new MutationObserver(mutations => {
                                if (document.querySelector(selector)) {
                                    resolve(document.querySelector(selector));
                                    observer.disconnect();
                                }
                            });
                    
                            observer.observe(document.body, {
                                childList: true,
                                subtree: true
                            });
                        });
                    }
                    waitForElm('a[href="/reels/"]').then((elm) => {
                        Android.log('Element is ready');
                        Android.log(elm.innerHTML);
                        elm.remove();
                    });
                })()""")
            }
        }
        webView.loadUrl("https://www.instagram.com/")
        //webView.loadUrl("https://www.rapidtables.com/tools/mirror.html")
        //webView.loadUrl("https://tus.io/demo")
        //webView.loadUrl("https://ps.uci.edu/~franklin/doc/file_upload.html")

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        })
    }
}