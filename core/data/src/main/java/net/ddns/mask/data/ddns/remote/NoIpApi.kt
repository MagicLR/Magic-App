package net.ddns.mask.data.ddns.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header // 导入 Header
import retrofit2.http.Query

interface NoIpApi {
    @GET("nic/update") // No-IP 的更新路径通常是 /nic/update
    suspend fun updateDns(
        @Header("Authorization") authHeader: String, // 添加 Authorization 头参数
        @Query("hostname") hostname: String,
        @Query("myip") ip: String? = null
    ): Response<String>
}