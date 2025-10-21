package net.ddns.mask.magic.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// DiscoverViewModel.kt
// 1. 定义界面状态数据类 (UI State)
data class DiscoverUiState(
    val isLoading: Boolean = false,
    val featureTitle: String = "探索更多功能", // 可以从ViewModel控制
    val featureDescription: String = "在这里，你可以发现并配置应用的各项强大功能。", // 可以从ViewModel控制
    val showDdnsButton: Boolean = true // 示例：控制按钮是否显示
    // 可以添加更多状态，例如从后台加载的数据
)

class DiscoverViewModel : ViewModel() {

    // 2. 使用 MutableStateFlow 来持有 UI 状态
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        // 示例：可以在初始化时加载一些数据
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 模拟网络请求或数据加载
            // kotlinx.coroutines.delay(1000) // 延迟1秒
            _uiState.update {
                it.copy(
                    isLoading = false,
                    // featureTitle = "动态加载的功能标题" // 也可以动态更新
                )
            }
        }
    }

    // 3. 定义处理用户操作的函数 (Events)
    fun onDdnsButtonClicked() {
        // 这里可以处理更复杂的逻辑，比如：
        // 1. 检查某些条件
        // 2. 上报分析事件
        // 3. 然后再触发导航
        println("DDNS 按钮被点击了 - 来自 ViewModel")
        // 导航事件通常不由 ViewModel 直接执行，而是通过回调或事件传递给 Screen/Activity/Fragment
        // 在 Compose 中，导航控制器通常在 Composable 层级传递和使用
    }

    fun toggleDdnsButtonVisibility() { // 示例方法
        _uiState.update { currentState ->
            currentState.copy(showDdnsButton = !currentState.showDdnsButton)
        }
    }
}