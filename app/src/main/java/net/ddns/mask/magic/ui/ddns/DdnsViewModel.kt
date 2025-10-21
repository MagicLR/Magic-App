package net.ddns.mask.magic.ui.ddns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch // 导入 Kotlin Flow 的 catch 操作符，用于错误处理
import kotlinx.coroutines.flow.launchIn // 导入 Kotlin Flow 的 launchIn 操作符，用于在特定作用域启动 Flow 收集
import kotlinx.coroutines.flow.onEach // 导入 Kotlin Flow 的 onEach 操作符，用于处理 Flow 发出的每个元素
import kotlinx.coroutines.flow.update // 导入 Kotlin Flow 的 update 操作符，用于原子性地更新 StateFlow 的值
import kotlinx.coroutines.launch
import net.ddns.mask.domain.ddns.model.DdnsConfig
import net.ddns.mask.domain.ddns.repository.DdnsRepository
import javax.inject.Inject

/**
 * 定义 DDNS 操作结果的密封接口。
 * 用于表示各种操作（如加载、保存、更新）的状态。
 */
sealed interface DdnsOperationResult {
    /** 空闲状态，表示当前没有操作在进行。 */
    data object Idle : DdnsOperationResult
    /** 操作进行中状态，可以携带操作的名称。 */
    data class InProgress(val operationName: String) : DdnsOperationResult
    /** 操作成功状态，可以携带操作名称和成功的消息。 */
    data class Success(val operationName: String, val message: String? = null) : DdnsOperationResult
    /** 操作错误状态，可以携带操作名称和错误信息。 */
    data class Error(val operationName: String, val errorMessage: String) : DdnsOperationResult
}

@HiltViewModel // Hilt 注解，用于 ViewModel 的依赖注入
class DdnsViewModel @Inject constructor(
    private val ddnsRepository: DdnsRepository // 通过构造函数注入 DDNS 仓库
) : ViewModel() {

    // _configState 是一个 MutableStateFlow，用于持有当前的 DDNS 配置信息。
    // UI 层可以观察 configState 来响应配置数据的变化。
    // 初始化为 null，表示配置尚未加载。
    private val _configState = MutableStateFlow<DdnsConfig?>(null)
    val configState: StateFlow<DdnsConfig?> = _configState.asStateFlow() // 将 MutableStateFlow 转换为不可变的 StateFlow 暴露给外部

    // _operationResult 是一个 MutableStateFlow，用于跟踪异步操作的结果和 UI 状态。
    // 例如，加载配置、保存配置或触发 DDNS 更新的状态。
    private val _operationResult = MutableStateFlow<DdnsOperationResult>(DdnsOperationResult.Idle)
    val operationResult: StateFlow<DdnsOperationResult> = _operationResult.asStateFlow() // 同样暴露为不可变的 StateFlow

    /**
     * 辅助方法，用于创建一个具有默认值的 DdnsConfig 实例。
     * 当需要一个初始配置或加载配置失败时使用。
     * @return 包含默认主机名、用户名和密码的 DdnsConfig 对象。
     */
    private fun createDefaultDdnsConfig(): DdnsConfig {
        return DdnsConfig(hostname = "", username = "", password = "") // provider 字段在 DdnsConfig 中有默认值 "NoIP"
    }

    init {
        // ViewModel 初始化时加载初始配置
        loadInitialConfig()
    }

    /**
     * 从仓库加载 DDNS 配置。
     * 会更新 _configState 和 _operationResult 来反映加载过程的状态。
     */
    private fun loadInitialConfig() {
        _operationResult.value = DdnsOperationResult.InProgress("加载配置") //标记开始加载配置
        ddnsRepository.getDdnsConfig() // 从仓库获取配置 Flow
            .onEach { loadedConfig -> // Flow 中的每个元素（配置）
                _configState.value = loadedConfig ?: createDefaultDdnsConfig() // 如果加载到配置则使用，否则使用默认配置
                _operationResult.value = DdnsOperationResult.Success("加载配置") // 标记加载成功
            }
            .catch { e -> // 处理 Flow 收集过程中的异常
                _configState.value = createDefaultDdnsConfig() // 加载失败也设置一个默认配置
                _operationResult.value = DdnsOperationResult.Error("加载配置", e.message ?: "加载配置失败") // 标记加载错误
            }
            .launchIn(viewModelScope) // 在 viewModelScope 中启动 Flow 的收集，确保与 ViewModel 生命周期绑定
    }

    /**
     * 当主机名输入变化时调用此方法。
     * @param newHostname 新的主机名。
     */
    fun onHostnameChange(newHostname: String) {
        _configState.update { currentConfig ->
            // 如果 currentConfig 为 null (理论上 loadInitialConfig 后不会轻易为 null，但作为健壮性处理)，则基于默认配置更新
            // 否则，在现有配置上更新 hostname
            (currentConfig ?: createDefaultDdnsConfig()).copy(hostname = newHostname)
        }
    }

    /**
     * 当用户名输入变化时调用此方法。
     * @param newUsername 新的用户名。
     */
    fun onUsernameChange(newUsername: String) {
        _configState.update { currentConfig ->
            (currentConfig ?: createDefaultDdnsConfig()).copy(username = newUsername)
        }
    }

    /**
     * 当密码输入变化时调用此方法。
     * @param newPassword 新的密码。
     */
    fun onPasswordChange(newPassword: String) {
        _configState.update { currentConfig ->
            (currentConfig ?: createDefaultDdnsConfig()).copy(password = newPassword)
        }
    }

    /**
     * 当服务提供商选择变化时调用此方法。
     * @param newProvider 新的服务提供商。
     */
    fun onProviderChange(newProvider: String) {
        _configState.update { currentConfig ->
            // DdnsConfig 中的 provider 字段有默认值 "NoIP"。
            // 如果允许用户修改 provider，则使用 copy 更新它。
            (currentConfig ?: createDefaultDdnsConfig().copy(provider = newProvider))
        }
    }

    /**
     * 保存当前的 DDNS 配置到仓库。
     * 在保存前可以进行输入校验。
     */
    fun saveConfig() {
        val currentConfig = _configState.value // 获取当前的配置快照
        if (currentConfig == null) {
            _operationResult.value = DdnsOperationResult.Error("保存配置", "没有配置可保存。")
            return
        }

        // 示例：校验主机名不能为空
        if (currentConfig.hostname.isBlank()) {
            _operationResult.value = DdnsOperationResult.Error("保存配置", "主机名不能为空。")
            return
        }
        // TODO: 根据需要添加其他校验，例如用户名、密码的格式或长度等

        _operationResult.value = DdnsOperationResult.InProgress("保存配置") // 标记开始保存
        viewModelScope.launch { // 启动协程执行异步保存操作
            try {
                ddnsRepository.saveDdnsConfig(currentConfig) // 调用仓库方法保存配置
                _operationResult.value = DdnsOperationResult.Success("保存配置", "配置保存成功。") // 标记保存成功
            } catch (e: Exception) {
                _operationResult.value = DdnsOperationResult.Error("保存配置", e.message ?: "保存配置失败。") // 标记保存失败
            }
        }
    }

    /**
     * 触发 DDNS 更新操作。
     * 会检查当前配置是否有效。
     */
    fun triggerUpdate() {
        val currentConfig = _configState.value
        // 检查配置是否存在且主机名不为空，这是触发更新的最低要求
        if (currentConfig == null || currentConfig.hostname.isBlank()) {
            _operationResult.value = DdnsOperationResult.Error("DDNS 更新", "配置或主机名缺失。")
            return
        }

        _operationResult.value = DdnsOperationResult.InProgress("DDNS 更新") // 标记开始DDNS更新
        viewModelScope.launch {
            val result = ddnsRepository.triggerDdnsUpdate() // 调用仓库方法触发更新，假设它返回 Result<Unit> 或类似结构
            if (result.isSuccess) { // 检查操作是否成功
                _operationResult.value = DdnsOperationResult.Success("DDNS 更新", "DDNS 更新触发成功。") // 标记更新成功
            } else {
                _operationResult.value = DdnsOperationResult.Error("DDNS 更新", result.exceptionOrNull()?.message ?: "DDNS 更新失败。") // 标记更新失败
            }
        }
    }

    /**
     * 重置操作结果状态为空闲状态。
     * 例如，在用户处理完一个成功或错误提示后，可以调用此方法清除提示。
     */
    fun resetOperationResult() {
        _operationResult.value = DdnsOperationResult.Idle
    }
}
