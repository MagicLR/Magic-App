package net.ddns.mask.magic.ui.ddns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator // 用于显示加载状态
import androidx.compose.material3.MaterialTheme // 用于错误文本颜色
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // 用于在 Composable 生命周期中执行副作用
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DdnsScreen(
    modifier: Modifier = Modifier,
    viewModel: DdnsViewModel = hiltViewModel()
) {
    // 从 ViewModel 收集整体配置状态
    val configState by viewModel.configState.collectAsState()
    // 从 ViewModel 收集操作结果状态
    val operationResult by viewModel.operationResult.collectAsState()

    // 获取当前配置，如果为 null，则使用默认值（空字符串）以避免 UI 错误
    // 注意：DdnsConfig 中的 provider 有默认值，所以这里可以直接使用
    val currentHostname = configState?.hostname ?: ""
    val currentUsername = configState?.username ?: ""
    val currentPassword = configState?.password ?: ""
    val currentProvider = configState?.provider ?: "NoIP" // 或者 viewModel.createDefaultDdnsConfig().provider

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "DDNS 配置")

        Spacer(modifier = Modifier.height(16.dp))

        // 显示操作结果：加载中、成功或错误信息
        when (val result = operationResult) {
            is DdnsOperationResult.InProgress -> {
                CircularProgressIndicator() // 显示加载动画
                Text(text = "${result.operationName}...")
            }
            is DdnsOperationResult.Success -> {
                Text(text = result.message ?: "${result.operationName} successful", color = MaterialTheme.colorScheme.primary)
                // 成功后，可以考虑重置操作结果，避免消息一直显示
                LaunchedEffect(result) { // 使用 LaunchedEffect 在 result 变化时执行
                    kotlinx.coroutines.delay(3000) // 延迟3秒
                    viewModel.resetOperationResult() // 重置操作结果
                }
            }
            is DdnsOperationResult.Error -> {
                Text(text = "错误: ${result.errorMessage}", color = MaterialTheme.colorScheme.error)
                 LaunchedEffect(result) {
                    kotlinx.coroutines.delay(5000) // 错误消息显示时间长一点
                    viewModel.resetOperationResult()
                }
            }
            DdnsOperationResult.Idle -> {
                // 空闲状态，不显示额外信息
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // 在操作结果和输入框之间也加一点间距

        // 服务提供商 (暂时显示, 如果要修改，需要添加如 DropdownMenu 等控件)
        Text(text = "服务提供商: $currentProvider")
        // TODO: 如果需要修改 Provider，添加 UI 元素并调用 viewModel.onProviderChange(newProvider)

        OutlinedTextField(
            value = currentHostname,
            onValueChange = { viewModel.onHostnameChange(it) },
            label = { Text("主机名/域名 (例如: yourhost.ddns.net)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = operationResult !is DdnsOperationResult.InProgress // 操作进行中时禁用输入
        )

        OutlinedTextField(
            value = currentUsername,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("用户名 / API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = operationResult !is DdnsOperationResult.InProgress
        )

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("密码 / API Token") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = operationResult !is DdnsOperationResult.InProgress
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveConfig()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = operationResult !is DdnsOperationResult.InProgress // 操作进行中时禁用按钮
        ) {
            Text("保存配置")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 添加触发 DDNS 更新的按钮
        Button(
            onClick = {
                viewModel.triggerUpdate()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = operationResult !is DdnsOperationResult.InProgress && configState?.hostname?.isNotBlank() == true // 仅当主机名非空且无操作进行中
        ) {
            Text("立即更新 DDNS")
        }

        // TODO: 添加显示当前公网 IP 和上次更新状态的 UI
        // val lastUpdateTimestamp = configState?.lastUpdateTime
        // if (lastUpdateTimestamp != null) {
        //     Text("上次更新时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(lastUpdateTimestamp))}")
        // }
        // Text("当前公网 IP: ...") // 这部分需要另外的逻辑来获取
    }
}
