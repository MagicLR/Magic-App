package net.ddns.mask.data.ddns.repository

import android.util.Base64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.ddns.mask.data.ddns.local.DdnsConfigDao
import net.ddns.mask.data.ddns.local.DdnsConfigEntity
import net.ddns.mask.data.ddns.local.toEntity
import net.ddns.mask.data.ddns.remote.NoIpApi
import net.ddns.mask.data.util.IpAddressProvider // 导入获取IP地址的工具
import net.ddns.mask.domain.ddns.model.DdnsConfig
import net.ddns.mask.domain.ddns.repository.DdnsRepository
import javax.inject.Inject

class DdnsRepositoryImpl @Inject constructor(
    private val ddnsConfigDao: DdnsConfigDao, // 本地数据库操作对象
    private val noIpApi: NoIpApi, // No-IP API 用于更新DNS
    private val ipAddressProvider: IpAddressProvider // 获取当前公网IP地址的工具
) : DdnsRepository {

    // 获取DDNS配置
    override fun getDdnsConfig(): Flow<DdnsConfig?> {
        // 从数据库中获取配置并转换成域模型
        return ddnsConfigDao.getConfig().map { entity: DdnsConfigEntity? ->
            entity?.toDomain() // 将数据库实体转换为领域模型
        }
    }

    // 保存DDNS配置
    override suspend fun saveDdnsConfig(config: DdnsConfig) {
        // 将配置转换为实体并保存到数据库
        ddnsConfigDao.insertOrUpdate(config.toEntity())
    }

    // 触发DDNS更新
    override suspend fun triggerDdnsUpdate(): Result<Unit> {
        // 从数据库获取DDNS配置
        val configEntity = ddnsConfigDao.getConfig().firstOrNull()
        val config = configEntity?.toDomain()

        // 如果配置为空，则返回错误
        if (config == null) {
            return Result.failure(Exception("DDNS 配置未找到。"))
        }

        // 检查配置中的主机名、用户名和密码是否为空
        if (config.hostname.isBlank() || config.username.isBlank() || config.password.isBlank()) {
            return Result.failure(Exception("主机名、用户名或密码不能为空。"))
        }

        // 获取当前公网IP
        val localIpResult: Result<String> = getCurrentPublicIp()

        // 如果获取公网IP失败，返回错误
        if (localIpResult.isFailure) {
            return Result.failure(
                Exception(
                    "获取公网IPv6地址失败: ${localIpResult.exceptionOrNull()?.message}",
                    localIpResult.exceptionOrNull()
                )
            )
        }
        val ipToUpdate: String = localIpResult.getOrThrow()

        // 尝试调用No-IP API更新DNS
        return try {
            // 基本认证头部
            val credentials = "${config.username}:${config.password}"
            val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

            // 更新DNS请求
            val response = noIpApi.updateDns(
                authHeader = authHeader,
                hostname = config.hostname,
                ip = ipToUpdate
            )

            // 处理No-IP的响应结果
            if (response.isSuccessful) {
                val responseBody = response.body()
                when {
                    responseBody?.startsWith("good") == true -> Result.success(Unit) // 更新成功
                    responseBody?.startsWith("nochg") == true -> Result.success(Unit) // IP地址未变
                    responseBody == "badauth" -> Result.failure(Exception("No-IP: 无效的用户名或密码。"))
                    responseBody == "badagent" -> Result.failure(Exception("No-IP: 客户端被禁用，请联系No-IP支持。"))
                    responseBody == "!donator" -> Result.failure(Exception("No-IP: 该功能对您的账户不可用。"))
                    responseBody == "abuse" -> Result.failure(Exception("No-IP: 用户名因滥用被封禁。"))
                    responseBody == "911" -> Result.failure(Exception("No-IP: 服务器错误，请稍后再试。"))
                    else -> Result.failure(Exception("No-IP: 未知响应 - ${responseBody ?: "空响应"}"))
                }
            } else {
                // 请求失败
                Result.failure(Exception("No-IP API请求失败，状态码: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            // 捕获异常并返回失败
            Result.failure(Exception("DDNS 更新失败: ${e.message}", e))
        }
    }

    // 获取当前公网IP地址
    override suspend fun getCurrentPublicIp(): Result<String> {
        // 调用注入的 IpAddressProvider 获取当前设备的公网IPv6地址
        return try {
            val ipv6Address = ipAddressProvider.getLocalIPv6GUA() // 获取IPv6地址
            if (ipv6Address != null) {
                Result.success(ipv6Address) // 返回成功
            } else {
                Result.failure(Exception("设备未找到适合的IPv6全球单播地址。"))
            }
        } catch (e: Exception) { // 捕获异常并返回失败
            Result.failure(Exception("通过提供者获取IPv6地址失败: ${e.message}", e))
        }
    }
}
