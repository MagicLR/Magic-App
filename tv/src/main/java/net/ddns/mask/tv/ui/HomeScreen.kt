package net.ddns.mask.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.ddns.mask.tv.utils.MyWebView
import net.ddns.mask.tv.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val selectedUrl = homeViewModel.selectedUrl  // 获取选择的URL
    val urlOptions = homeViewModel.urlOptions  // 获取URL选项列表

    // Box作为根布局，能够很好地处理全屏内容的切换
    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedUrl == null) {
            // 如果没有选择URL，显示首页内容

            // Box用于堆叠网格和状态栏
            Box(modifier = Modifier.fillMaxSize()) {
                // LazyVerticalGrid 用于创建可滚动的网格布局，GridCells.Adaptive 可以根据屏幕宽度自适应列数
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(urlOptions) { (buttonText, url) ->
                        var isFocused by remember { mutableStateOf(false) } // 用于记录按钮是否获得焦点

                        Button(
                            onClick = { homeViewModel.selectUrl(url) }, // 按钮点击事件，选择URL
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth()
                                .onFocusChanged { isFocused = it.isFocused }, // 监听焦点变化
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFocused) Color(0xFF00BCD4) else Color.Gray, // 焦点时颜色变化
                                contentColor = if (isFocused) Color.Black else Color.White
                            )
                        ) {
                            Text(buttonText, style = TextStyle(fontSize = 26.sp)) // 按钮显示文本
                        }
                    }
                }

                // 【修改点】将StatusBar的调用移入 if (selectedUrl == null) 的分支内
                StatusBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)  // 状态栏居中对齐
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                )
            }
        } else {
            // 如果选择了URL，显示WebView内容
            MyWebView(
                modifier = Modifier.fillMaxSize(),
                url = selectedUrl,
                onWebViewCreated = { homeViewModel.webView = it } // 创建WebView时保存其引用
            )
        }
    }
}

/**
 * 状态栏 Composable，用于容纳时间和天气等信息。
 */
@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween, // 子项两端对齐
        verticalAlignment = Alignment.CenterVertically // 垂直方向居中对齐
    ) {
        // 左侧显示时间
        Clock()

        // TODO: 未来可以在此处添加天气组件
        // WeatherInfo()
    }
}

/**
 * 一个显示当前日期和时间的 Composable。
 */
@Composable
fun Clock(modifier: Modifier = Modifier) {
    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 格式化日期时间
    var currentTime by remember { mutableStateOf(dateTimeFormat.format(Date())) } // 当前时间变量

    LaunchedEffect(Unit) {
        // 使用 LaunchedEffect 来持续更新时间
        while (true) {
            currentTime = dateTimeFormat.format(Date()) // 更新时间
            delay(1000L) // 每秒更新一次
        }
    }

    Text(
        text = currentTime,  // 显示时间
        modifier = modifier,
        color = Color.Black,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold, // 字体加粗
        style = TextStyle(
            shadow = Shadow( // 设置阴影效果
                offset = Offset(2f, 2f), // 阴影偏移量
                blurRadius = 4f  // 阴影模糊半径
            )
        )
    )
}
