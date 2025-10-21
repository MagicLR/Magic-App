// 在 HomeScreen.kt 文件中
package net.ddns.mask.magic.ui.home // 假设的包名

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // 导入 Modifier

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // 使用传入的 modifier，例如应用到根布局元素
    Text(text = "这是首页", modifier = modifier)

}