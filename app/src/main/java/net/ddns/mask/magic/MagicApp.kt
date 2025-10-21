package net.ddns.mask.magic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 自定义 Application 类。
 * 使用 @HiltAndroidApp 注解来启用 Hilt 依赖注入。
 */
@HiltAndroidApp
class MagicApp : Application() {
    // Application 类主体，通常用于初始化全局组件和配置。
    // Hilt 会自动处理 Application 级别的依赖注入。
}
