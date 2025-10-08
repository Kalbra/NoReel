package com.example.noreel

import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlin.concurrent.thread

class WebViewViewport() : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        //injectJS(view)
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


        } else {
            super.onReceivedError(view, request, error)
        }
    }
}