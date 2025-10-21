package net.ddns.mask.magic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility // 导入 AnimatedVisibility
import androidx.compose.animation.fadeIn // 导入 fadeIn 动画
import androidx.compose.animation.fadeOut // 导入 fadeOut 动画
import androidx.compose.animation.slideInVertically // 导入 slideInVertically 动画
import androidx.compose.animation.slideOutVertically // 导入 slideOutVertically 动画
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // 确保导入 padding 以用于 DefaultPreview
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text // 确保导入 Text 以用于 DefaultPreview (如果之前没有)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.ddns.mask.magic.ui.AppBottomNavigationBar
import net.ddns.mask.magic.ui.AppDestinations
import net.ddns.mask.magic.ui.AppNavHost
import net.ddns.mask.magic.ui.bottomNavItems
import net.ddns.mask.magic.ui.theme.MagicLRTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MagicLRTheme {
                val navController = rememberNavController() // 创建导航控制器
                val navBackStackEntry by navController.currentBackStackEntryAsState() // 获取当前导航栈条目状态
                val currentDestination = navBackStackEntry?.destination // 获取当前目标路由信息

                Scaffold(
                    modifier = Modifier.fillMaxSize(), // Scaffold 充满整个屏幕
                    bottomBar = {
                        val currentRoute = currentDestination?.route // 获取当前路由的字符串表示
                        // 使用 AnimatedVisibility 来控制底部导航栏的显示和隐藏，并添加动画
                        AnimatedVisibility(
                            visible = currentRoute == AppDestinations.HOME_ROUTE ||
                                    currentRoute == AppDestinations.DISCOVER_ROUTE, // 条件：仅在首页或发现页显示
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(), // 进入动画：从底部滑入并淡入
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()  // 退出动画：向底部滑出并淡出
                        ) {
                            // 当 AnimatedVisibility 的 visible 为 true 时，此内容块会被渲染
                            AppBottomNavigationBar(
                                navController = navController,
                                items = bottomNavItems
                            )
                        }
                    }
                ) { innerPadding -> // Scaffold 的内容区域，innerPadding 包含了底部导航栏等占用的空间
                    AppNavHost(
                        navController = navController,
                        innerPadding = innerPadding // 将内边距传递给导航主机
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MagicLRTheme {
        // 预览通常不包含复杂的导航逻辑，因此 AnimatedVisibility 在这里可能不会按预期工作
        // 可以简单地展示一个静态的底部栏或者不展示
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // 示例：可以放一个静态的 AppBottomNavigationBar 或者留空
                // AppBottomNavigationBar(navController = rememberNavController(), items = bottomNavItems)
            }
        ) { innerPadding ->
            Text( // 确保 androidx.compose.material3.Text 已导入
                text = "主应用界面预览",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // 应用 Scaffold 提供的内边距
            )
        }
    }
}

