package net.ddns.mask.domain.ddns.repository

import kotlinx.coroutines.flow.Flow // 需要导入 Flow
import net.ddns.mask.domain.ddns.model.DdnsConfig

interface DdnsRepository {
    /**
     * 获取 DDNS 配置
     * @return Flow<DdnsConfig?> 如果没有配置，则返回 null
     */
    fun getDdnsConfig(): Flow<DdnsConfig?>

    /**
     * 保存 DDNS 配置
     * @param config DdnsConfig
     */
    suspend fun saveDdnsConfig(config: DdnsConfig)

    // 您可以根据需要决定是否保留以下额外的方法
    // 如果暂时用不到，可以注释掉或移除，以保持接口简洁

    // /**
    //  * 删除 DDNS 配置 (如果将来支持删除操作)
    //  * @param id Long // 假设通过 id 删除，但这可能不适用于单一配置模型
    //  */
    // suspend fun deleteConfig(id: Long) // 如果是单一配置，可能不需要此方法或参数不同

    // /**
    //  * 手动触发 DNS 更新 (基于已保存的配置)
    //  * @param config DdnsConfig // 或者不需要参数，直接使用仓库内已保存的配置
    //  * @return Boolean 更新是否成功提交/处理
    //  */
    // suspend fun updateDns(config: DdnsConfig): Boolean // 这个方法名与 triggerDdnsUpdate 功能类似

    /**
     * 触发 DDNS 更新
     * @return Result<Unit> 更新成功或失败
     */
    suspend fun triggerDdnsUpdate(): Result<Unit>

    /**
     * 获取当前公网 IP 地址
     * @return Result<String> 获取成功或失败
     */
    suspend fun getCurrentPublicIp(): Result<String>
}
