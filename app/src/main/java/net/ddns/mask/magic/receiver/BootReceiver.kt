package net.ddns.mask.magic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import net.ddns.mask.magic.service.MyForegroundService

// 定义一个广播接收器类，用于接收系统广播
class BootReceiver : BroadcastReceiver() {
    // 当接收到广播时会调用此方法
    override fun onReceive(context: Context, intent: Intent) {
        // 检查接收到的广播是否是设备启动完成的广播
        // Intent.ACTION_BOOT_COMPLETED 是标准的 Android 启动完成广播
        // "android.intent.action.QUICKBOOT_POWERON"是一些设备（如HTC）在快速启动后发送的广播
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            // 创建一个用于启动 MyForegroundService 服务的 Intent
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            // 使用 ContextCompat.startForegroundService() 启动前台服务
            // 这样做是为了确保即使应用在后台，服务也能被正确启动并提升为前台服务
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
