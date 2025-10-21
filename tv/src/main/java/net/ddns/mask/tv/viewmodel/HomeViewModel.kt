package net.ddns.mask.tv.viewmodel

import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    var selectedUrl by mutableStateOf<String?>(null)
        private set

    var webView: WebView? = null

    val urlOptions: List<Pair<String, String>> = listOf(
        "CCTV-1 综合" to "https://tv.cctv.com/live/cctv1",
        "CCTV-3 综艺" to "https://tv.cctv.com/live/cctv3",
        "CCTV-6 电影" to "https://tv.cctv.com/live/cctv6",
        "CCTV-7 国防军事" to "https://tv.cctv.com/live/cctv7",
        "CCTV-8 电视剧" to "https://tv.cctv.com/live/cctv8",
        "CCTV-9 记录" to "https://tv.cctv.com/live/cctvjilu",
        "CCTV-10 科教" to "https://tv.cctv.com/live/cctv10",
        "CCTV-11 戏曲" to "https://tv.cctv.com/live/cctv11",
        "CCTV-12 社会与法" to "https://tv.cctv.com/live/cctv12",
        "CCTV-13 新闻" to "https://tv.cctv.com/live/cctv13",
        "CCTV-14 少儿" to "https://tv.cctv.com/live/cctvchild",
        "CCTV-15 音乐" to "https://tv.cctv.com/live/cctv15",
        "CCTV-17 农业农村" to "https://tv.cctv.com/live/cctv17",
        "CCTV-5＋ 体育赛事" to "https://tv.cctv.com/live/cctv5plus",
        "CCTV-4 中文国际" to "https://tv.cctv.com/live/cctv4"
    )
    fun selectUrl(url: String) {
        selectedUrl = url // 触发 Compose 重组
    }

    fun clearSelection() {
        selectedUrl = null // 触发 Compose 重组
    }
}
