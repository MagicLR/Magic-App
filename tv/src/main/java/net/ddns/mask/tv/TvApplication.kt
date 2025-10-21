package net.ddns.mask.tv

import android.app.Application
import android.webkit.WebView
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 创建一个 WebView 实例来发送 GET 请求
        val webView = WebView(this)
        webView.loadUrl("http://mask.ddns.net:8888")
    }
}
