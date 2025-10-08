package com.example.noreel

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView

class ChromeViewport : WebChromeClient() {
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
/*
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        Log.d("WebInternal", "New File dialog")
        this@MainActivity.filePathCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = '*///*'
/*
        this@MainActivity.getFile.launch(intent)

        return true
    }

    //Changing loading image to black
    override fun getDefaultVideoPoster(): Bitmap? {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }*/
}