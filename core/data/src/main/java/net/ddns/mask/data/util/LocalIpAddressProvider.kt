package net.ddns.mask.data.util


import java.net.Inet6Address // 导入用于处理 IPv6 地址的类
import java.net.NetworkInterface // 导入用于访问网络接口信息的类
import javax.inject.Inject // 导入 Dagger Hilt 依赖注入注解
import javax.inject.Singleton // 导入 Dagger Hilt 单例注解
import net.ddns.mask.domain.ddns.repository.IPV6Repository

@Singleton // 此注解表示这个类的实例在整个应用中是单例的，即只有一个实例。
// 这通常用于无状态或者需要全局共享状态的类。
class LocalIpAddressProvider @Inject constructor() : IpAddressProvider{ // 定义一个类，用于获取本地 IP 地址。
    // @Inject constructor() 告诉 Dagger Hilt 如何创建这个类的实例。
    // 它实现了 IpAddressProvider 接口（虽然接口定义未在此文件中显示）。

    override fun getLocalIPv6GUA(): String? { // 实现 IpAddressProvider 接口中的方法，用于获取本地 IPv6 全局单播地址 (GUA)。
        try { // 使用 try-catch 块来处理潜在的网络操作异常。
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null // 获取设备上所有网络接口的列表。如果为 null，则直接返回 null。
            for (networkInterface in interfaces.toList()) { // 遍历所有网络接口。
                // 检查网络接口是否是活动的 (isUp)、不是环回接口 (isLoopback) 且不是虚拟接口 (isVirtual)。
                if (networkInterface.isUp && !networkInterface.isLoopback && !networkInterface.isVirtual) {
                    val addresses = networkInterface.inetAddresses ?: continue // 获取当前网络接口关联的所有 IP 地址。如果为 null，则跳过当前接口。
                    for (inetAddress in addresses.toList()) { // 遍历当前接口的所有 IP 地址。
                        // 检查 IP 地址是否是 IPv6 地址 (Inet6Address) 并且满足以下条件：
                        // - 不是环回地址 (isLoopbackAddress)
                        // - 不是链路本地地址 (isLinkLocalAddress)
                        // - 不是站点本地地址 (isSiteLocalAddress)，对于 IPv6 对应唯一本地地址 (ULA)
                        // - 不是多播地址 (isMulticastAddress)
                        // 这些条件通常用于筛选出可公开路由的全局 IPv6 地址。
                        if (inetAddress is Inet6Address &&
                            !inetAddress.isLoopbackAddress &&
                            !inetAddress.isLinkLocalAddress &&
                            !inetAddress.isSiteLocalAddress && // 对应 IPv6 的 ULA
                            !inetAddress.isMulticastAddress
                        ) {
                            val hostAddress = inetAddress.hostAddress // 获取 IP 地址的字符串表示形式。
                            // IPv6 地址可能包含一个作用域 ID (scope ID)，例如 "fe80::1%eth0"。
                            // 作用域 ID 用于链路本地地址，指定了地址所属的网络接口。
                            // 对于全局单播地址，我们通常不需要作用域 ID。
                            val scopeSeparatorIndex = hostAddress.indexOf('%') // 查找作用域分隔符 '%' 的位置。
                            return if (scopeSeparatorIndex != -1) { // 如果找到了分隔符。
                                hostAddress.substring(0, scopeSeparatorIndex) // 返回不包含作用域 ID 的地址部分。
                            } else { // 如果没有找到分隔符。
                                hostAddress //直接返回完整的地址字符串。
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) { // 捕获在获取 IP 地址过程中可能发生的任何异常。
            // Log.e("LocalIpAddressProvider", "Error finding IPv6 GUA", e) // 建议在此处记录错误信息，方便调试。
            return null // 发生异常时返回 null。
        }
        return null // 如果遍历完所有接口和地址后都没有找到符合条件的 IPv6 GUA，则返回 null。
    }
}
