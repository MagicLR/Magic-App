package net.ddns.mask.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import net.ddns.mask.tv.ui.theme.MagicLRTheme
import dagger.hilt.android.AndroidEntryPoint
import net.ddns.mask.tv.ui.HomeScreen
import androidx.activity.viewModels
import net.ddns.mask.tv.viewmodel.HomeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 使用 activity-ktx 的委托方式从 Hilt 获取 ViewModel
    private val homeViewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 使用 OnBackPressedDispatcher 处理返回键
        onBackPressedDispatcher.addCallback(this) {
            val webView = homeViewModel.webView
            when {
                webView != null && webView.canGoBack() -> webView.goBack()
                homeViewModel.selectedUrl != null -> homeViewModel.clearSelection()
                else -> finish() // 或调用 super.onBackPressed() 退出 Activity
            }
        }

        setContent {
            MagicLRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    HomeScreen(homeViewModel)
                }
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MagicLRTheme {
        Greeting("Android")
    }
}