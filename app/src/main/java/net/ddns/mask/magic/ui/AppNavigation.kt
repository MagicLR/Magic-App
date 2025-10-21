package net.ddns.mask.magic.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import net.ddns.mask.magic.ui.discover.DiscoverScreen // 导入 DiscoverScreen
import net.ddns.mask.magic.ui.home.HomeScreen // 导入 HomeScreen
import net.ddns.mask.magic.ui.ddns.DdnsScreen // 导入 DdnsScreen

// 定义应用程序中的导航目标
object AppDestinations {
    const val HOME_ROUTE = "home"         // 主页路由
    const val DISCOVER_ROUTE = "discover"   // 发现页路由
    const val DDNS_ROUTE = "ddns"           // DDNS 设置路由
}

// 底部导航栏项目的数据类
data class BottomNavigationItem(
    val route: String, // 路由名称
    val label: String, // 显示的标签
    val icon: ImageVector // 显示的图标
)

// 定义底部导航栏的项目列表
val bottomNavItems = listOf(
    BottomNavigationItem(
        route = AppDestinations.HOME_ROUTE,
        label = "首页", // 标签可以根据您的需要修改
        icon = Icons.Filled.Home // 图标可以根据您的需要修改
    ),
    BottomNavigationItem(
        route = AppDestinations.DISCOVER_ROUTE,
        label = "发现", // 标签可以根据您的需要修改
        icon = Icons.Filled.Search // 图标可以根据您的需要修改
    )
    // 如果有更多底部导航项，可以在这里添加
)

/**
 * 应用程序底部导航栏
 *
 * @param navController 导航控制器
 * @param items 底部导航栏的项目列表
 */
@Composable
fun AppBottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavigationItem>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // 弹出到导航图的起始目标，以避免在用户选择项目时在返回堆栈上构建大量目标
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免在重新选择同一项目时启动多个副本
                        launchSingleTop = true
                        // 重新选择先前选择的项目时恢复状态
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * 应用程序导航主机
 *
 * @param navController 导航控制器，用于管理应用程序的导航
 * @param modifier 修改器，用于自定义 NavHost 容器的布局和外观
 * @param innerPadding Scaffold 的内部边距，应用于屏幕内容
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    // 定义底部导航栏的项目 (实际的底部导航栏将在 MainActivity 中使用 AppBottomNavigationBar 构建)
    // 这里保留 bottomNavItems 的定义是为了方便 NavHost 中的路由能够引用到正确的屏幕
    // 但 AppBottomNavigationBar 本身会从 MainActivity 传递 items

    NavHost(
        navController = navController, // 设置导航控制器
        startDestination = AppDestinations.HOME_ROUTE, // 设置起始导航目标为主页
        modifier = modifier // NavHost 容器本身的 Modifier
    ) {
        // 定义“首页”路由
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(modifier = Modifier.padding(innerPadding))
        }
        // 定义“发现”路由
        composable(AppDestinations.DISCOVER_ROUTE) {
            DiscoverScreen(modifier = Modifier.padding(innerPadding),
                navController = navController // 传递 navController 给 DiscoverScreen
            )
        }
        //  定义“DDNS”路由
        composable(AppDestinations.DDNS_ROUTE) {
            DdnsScreen(modifier = Modifier.padding(innerPadding)) // DDNS 屏幕也应用内边距
        }
    }
}
