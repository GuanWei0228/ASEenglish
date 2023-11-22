package com.s1092790.eenglish

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler


class sing : AppCompatActivity() {
    private var mWebView: WebView? = null

    //Embeded Youtube Video Address
    private val VideoEmbededAdress =
        "<iframe width=\"352\" height=\"225\" src=\"https://www.youtube.com/embed/Ns54UivB138\" title=\"YouTube video player\" frameborder=\"0\" allow=\" clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
    private val mimeType = "text/html"
    private val encoding = "UTF-8" //"base64";
    private val USERAGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sing_main)

        mWebView = findViewById<View>(R.id.youtubeView) as WebView
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .build()
        mWebView!!.webViewClient = object : WebViewClient() {
            private var view: WebView? = null
            private var request: WebResourceRequest? = null
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                Log.d(TAG, "shouldOverrideUrlLoading: Url = [" + request.url + "]")
                this.view = view
                this.request = request
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.allowContentAccess = true
        mWebView!!.settings.allowFileAccess = true
        mWebView!!.settings.mediaPlaybackRequiresUserGesture = true
        mWebView!!.settings.setUserAgentString(USERAGENT) //Important to auto play video
        mWebView!!.settings.loadsImagesAutomatically = true
        mWebView!!.webChromeClient = WebChromeClient()
        mWebView!!.webViewClient = WebViewClient()
        mWebView!!.loadUrl(VideoEmbededAdress)
        mWebView!!.loadDataWithBaseURL("", VideoEmbededAdress, mimeType, encoding, "")
    }

    companion object {
        private const val TAG = "sing"
    }


    fun goHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // 調用 gohome() 方法，以便在回到 home.kt 之前更新畫面
        val view = View(this)
        goHome(view)
        // 調用 finish() 方法，回到 home.kt
        finish()
    }
}
