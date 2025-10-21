package net.ddns.mask.magic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import net.ddns.mask.magic.R

class MyForegroundService : Service() { // 定义一个前台服务类

    override fun onCreate() { // 服务创建时调用
        super.onCreate()
        createNotificationChannel() // 创建通知渠道
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { // 服务启动时调用
        val notification = NotificationCompat.Builder(this, CHANNEL_ID) // 创建通知构建器
            .setContentTitle("MagicApp 正在运行") // 设置通知标题
            .setContentText("开机自启服务已启动") // 设置通知内容
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 设置通知小图标
            .setPriority(NotificationCompat.PRIORITY_LOW) // 设置通知优先级（兼容旧版 Android）
            .build() // 构建通知

        // 必须在 5 秒内调用 startForeground，否则系统会认为服务无响应并终止它
        startForeground(NOTIF_ID, notification) // 将服务设置为前台服务，并显示通知

        // TODO: 在这里编写后台任务的逻辑
        return START_STICKY // 表示如果服务被系统杀死，则尝试重新创建服务
    }

    override fun onBind(intent: Intent?): IBinder? = null // 如果服务不需要绑定，则返回 null

    private fun createNotificationChannel() { // 创建通知渠道的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 检查 Android 版本是否为 Oreo (API 26) 或更高版本
            // 对于 Android Oreo 及更高版本，必须创建通知渠道才能显示通知
            val channel = NotificationChannel(
                CHANNEL_ID, // 渠道 ID
                "开机自启服务", // 用户可见的渠道名称
                NotificationManager.IMPORTANCE_LOW // 渠道的重要性级别
            )
            val manager = getSystemService(NotificationManager::class.java) // 获取 NotificationManager 系统服务
            manager.createNotificationChannel(channel) // 创建通知渠道
        }
    }

    companion object { // 伴生对象，用于定义静态常量
        private const val CHANNEL_ID = "boot_channel" // 通知渠道 ID
        private const val NOTIF_ID = 1001 // 通知 ID
    }
}
