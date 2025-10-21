package net.ddns.mask.tv.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable 函数：一个经过精心配置的 WebView 组件，用于在 Compose 中显示网页内容。
 * 它处理了 WebView 的创建、生命周期管理、网页加载和全屏视频播放等复杂逻辑。
 *
 * @param modifier Modifier，用于控制 WebView 的大小、边距等布局属性。
 * @param url 要加载的网页地址。当这个 URL 发生变化时，WebView 会自动加载新页面。
 * @param onWebViewCreated 一个回调函数，在 WebView 实例创建完成后被调用。这允许
 *                         调用方（如 ViewModel）获取并持有 WebView 的引用，以便进行更复杂的操作（如回退）。
 */
@Composable
fun MyWebView(
    modifier: Modifier = Modifier,
    url: String,
    onWebViewCreated: (WebView) -> Unit
) {
    // 获取当前的 Android 上下文，这是创建 Android View（如 WebView）所必需的。
    val context = LocalContext.current

    // 使用 remember 来创建和记住 WebView 实例。这确保了在 Composable 重组期间，
    // WebView 不会被重新创建，从而保持其内部状态（如加载的页面、滚动位置等）。
    val webView = remember {
        WebView(context).apply {
            // --- WebViewClient 设置：处理网页加载事件 --- 
            webViewClient = object : WebViewClient() {
                /**
                 * 当页面加载完成时被调用。
                 */
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // 页面加载完成后，延迟3秒执行一段 JavaScript，尝试自动点击全屏按钮。
                    // 延迟执行是为了确保页面上的 JavaScript 已经初始化完毕, 3秒是一个比较折中的选择
                    view?.postDelayed({
                        val js = """
                        (function() {
                            // 每秒检查一次全屏按钮是否存在并且当前不是全屏状态
                            var checkInterval = setInterval(function() {
                                var btn = document.getElementById('player_fullscreen_player');
                                var isFull = document.fullscreenElement != null ||
                                             document.webkitFullscreenElement != null ||
                                             document.mozFullScreenElement != null ||
                                             document.msFullscreenElement != null;

                                // 如果按钮存在且不处于全屏，则模拟点击
                                if (!isFull && btn) {
                                    console.log('尝试进入全屏...');
                                    btn.click();
                                } else if (isFull) {
                                    // 如果已经进入全屏，则停止检测
                                    console.log('已进入全屏，停止检测');
                                    clearInterval(checkInterval);
                                }
                            }, 1000); // 检测间隔：1秒
                        })();
                        """.trimIndent()
                        // 执行 JavaScript
                        view.evaluateJavascript(js, null)
                    }, 3_000) // 延迟时间：3秒
                }

                /**
                 * 当页面加载出错时被调用。
                 */
                override fun onReceivedError(
                    view: WebView?, errorCode: Int, description: String?, failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    // 使用 Toast 显示一个用户友好的错误提示。
                    Toast.makeText(context, "加载失败: $description", Toast.LENGTH_LONG).show()
                }
            }

            // --- WebSettings 配置：定制 WebView 的行为 --- 
            settings.apply {
                // 启用 JavaScript，现代网页几乎都依赖它。
                javaScriptEnabled = true
                // 启用 DOM 存储，用于持久化存储，如 localStorage。
                domStorageEnabled = true
                // 设置缓存模式，LOAD_DEFAULT 是默认的智能缓存策略。
                cacheMode = WebSettings.LOAD_DEFAULT
                // 设置 User-Agent，模拟桌面版 Chrome 浏览器，以获取完整的网页内容。
                userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.74 Safari/537.36"
                // 允许媒体（视频/音频）自动播放，无需用户手势触发。
                mediaPlaybackRequiresUserGesture = false
                // 允许 JavaScript 自动打开窗口。
                javaScriptCanOpenWindowsAutomatically = true
                
                // --- 缩放相关设置 ---
                // 支持用户通过手势进行缩放。
                setSupportZoom(true)
                // 启用内置的缩放控件（例如 + 和 - 按钮）。
                builtInZoomControls = true
                // 但隐藏这些控件，因为我们通常希望通过双击或手势来缩放。
                displayZoomControls = false

                // --- 解决网页超出屏幕宽度的核心设置 ---
                // 1. 设置为宽视口模式。这会使 WebView 模拟一个比其物理尺寸更宽的虚拟视口，
                //    从而让那些为桌面浏览器设计的网站能够完整显示。
                useWideViewPort = true
                // 2. 概览模式。当页面宽度超过 WebView 宽度时，它会自动缩放页面内容以适应屏幕宽度。
                //    这通常与 useWideViewPort 结合使用，以获得最佳的自适应效果。
                loadWithOverviewMode = true
            }

            // --- WebChromeClient 设置：处理与浏览器 UI 相关的事件，如全屏 --- 
            webChromeClient = object : WebChromeClient() {
                private var customView: View? = null
                private var customViewCallback: CustomViewCallback? = null
                private var originalSystemUiVisibility: Int = 0
                private var originalOrientation: Int = 0

                /**
                 * 当网页请求进入全屏时（例如，播放视频），此方法被调用。
                 */
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    if (this.customView != null) {
                        onHideCustomView()
                        return
                    }
                    val activity = context as? Activity ?: return

                    this.customView = view
                    this.customViewCallback = callback
                    // 记录当前屏幕 UI 状态和方向，以便退出全屏时恢复
                    this.originalSystemUiVisibility = activity.window.decorView.systemUiVisibility
                    this.originalOrientation = activity.requestedOrientation

                    // 将全屏视图（通常是视频播放器）添加到窗口的顶层
                    val decorView = activity.window.decorView as FrameLayout
                    decorView.addView(this.customView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                    this.customView?.setBackgroundColor(Color.BLACK)

                    // 强制横屏并隐藏原始的 WebView
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    this@apply.visibility = View.GONE
                }

                /**
                 * 当网页退出全屏时调用。
                 */
                override fun onHideCustomView() {
                    val activity = context as? Activity ?: return

                    // 从窗口移除全屏视图
                    val decorView = activity.window.decorView as FrameLayout
                    decorView.removeView(this.customView)
                    this.customView = null

                    // 恢复原始的屏幕 UI 状态和方向
                    activity.window.decorView.systemUiVisibility = this.originalSystemUiVisibility
                    activity.requestedOrientation = this.originalOrientation

                    this.customViewCallback?.onCustomViewHidden()
                    this.customViewCallback = null
                    // 重新显示 WebView
                    this@apply.visibility = View.VISIBLE
                }
            }

            // --- Cookie 管理 --- 
            val cookieManager = CookieManager.getInstance()
            // 允许 WebView 接受和存储 Cookie。
            cookieManager.setAcceptCookie(true)
            // 在 Android 5.0 (Lollipop) 及以上版本，允许接受第三方 Cookie。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(this, true)
            }
        }
    }

    // --- Compose 生命周期管理 ---
    // DisposableEffect 用于处理需要在 Composable 销毁时执行的清理操作。
    // key1 = Unit 表示这个 effect 只在 Composable 首次进入组合时运行一次，并在其离开时清理。
    DisposableEffect(Unit) {
        // 在 WebView 创建后，立即通过回调将其传出。
        onWebViewCreated(webView)
        
        onDispose {
            // 当 Composable 被销毁时，调用 webView.destroy() 来释放资源，防止内存泄漏。
            webView.destroy()
        }
    }

    // LaunchedEffect 用于在 Composable 的生命周期内执行挂起函数或耗时操作。
    // 当 key1（即 url）发生变化时，协程会重新启动。
    LaunchedEffect(url) {
        // 当外部传入的 url 变化时，加载新的 URL。
        // 这避免了在每次重组时不必要地重新加载相同的 URL。
        webView.loadUrl(url)
    }

    // --- 将 WebView 集成到 Compose UI 树中 ---
    // AndroidView 是一个 Composable，用于将传统的 Android View 嵌入到 Compose 布局中。
    AndroidView(
        modifier = modifier, // 应用传入的 Modifier
        factory = { webView } // factory lambda 返回要嵌入的 View 实例。
    )
}
