package com.example.noreel

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    object AndroidJSInterface {
        @JavascriptInterface
        fun log(msg: String){
            Log.d("WebInternal", msg)
        }
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(AndroidJSInterface, "Android")

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

                    Android.log(document.body.innerHTML);
                })()""")
            }
        }
        webView.loadUrl("https://www.instagram.com/")

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d("NAVIGATOR", "Back")
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        })

    }
}