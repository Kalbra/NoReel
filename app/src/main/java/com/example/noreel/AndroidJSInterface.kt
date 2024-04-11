package com.example.noreel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.Button

class AndroidJSInterface(private val preference_button: Button, val mContext: Context) {
    var AlreadyUsedURLs = emptyArray<String>()
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

    @JavascriptInterface
    fun openInStdBrowser(url: String){
        //If element was not redirected before
        if(!AlreadyUsedURLs.contains(url)){
            AlreadyUsedURLs += url

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            mContext.startActivity(browserIntent)

            Log.d("StdBrowserRequest", url)
        }
    }
}