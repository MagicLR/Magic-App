package net.ddns.mask.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.ddns.mask.data.ddns.remote.NoIpApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory // 用于处理纯文本/字符串响应
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val NO_IP_BASE_URL = "https://dynupdate.no-ip.com/" // No-IP 动态更新的基础 URL

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // 配置 OkHttpClient，例如添加日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 在调试时查看请求和响应体，生产环境可以设为 NONE 或 BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // 添加日志拦截器
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NO_IP_BASE_URL)
            .client(okHttpClient) // 使用上面提供的 OkHttpClient
            .addConverterFactory(ScalarsConverterFactory.create()) // 用于将响应体直接转换为 String
            .build()
    }

    @Provides
    @Singleton
    fun provideNoIpApi(retrofit: Retrofit): NoIpApi {
        return retrofit.create(NoIpApi::class.java)
    }
}
