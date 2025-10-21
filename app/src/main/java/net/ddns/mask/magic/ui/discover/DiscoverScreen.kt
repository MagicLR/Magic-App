package net.ddns.mask.magic.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.ddns.mask.magic.ui.AppDestinations

@Composable
fun DiscoverScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    // 1. 注入 ViewModel
    discoverViewModel: DiscoverViewModel = viewModel() // 获取 ViewModel 实例
) {
    // 2. 从 ViewModel 收集 UI 状态
    val uiState by discoverViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator() // 显示加载指示器
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.featureTitle, // 使用 ViewModel 中的状态
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = uiState.featureDescription, // 使用 ViewModel 中的状态
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            if (uiState.showDdnsButton) { // 根据 ViewModel 中的状态决定是否显示按钮
                Button(
                    // 3. 将用户事件委托给 ViewModel 处理
                    onClick = {
                        discoverViewModel.onDdnsButtonClicked() // 调用 ViewModel 的方法
                        navController.navigate(AppDestinations.DDNS_ROUTE) // 导航仍在此处，或通过事件回调
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "设置图标",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = "前往 DDNS 配置",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // 示例：添加一个按钮来调用ViewModel中的其他方法
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { discoverViewModel.toggleDdnsButtonVisibility() },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh, // 假设这是一个切换可见性的图标
                    contentDescription = "切换按钮可见性",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("切换DDNS按钮显示/隐藏")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    MaterialTheme {
        // 在 Preview 中，你可以直接创建一个 ViewModel 实例，或者使用一个模拟的 ViewModel
        // 为了简单起见，这里直接调用，但对于复杂的 ViewModel，你可能需要模拟依赖
        DiscoverScreen(
            navController = rememberNavController(),
        )
    }
}