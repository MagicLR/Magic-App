package net.ddns.mask.domain.ddns.model

data class DdnsConfig(
    val id: Long = 0,
    val provider: String = "NoIP",
    val hostname: String,
    val username: String,
    val password: String,
    val lastUpdateTime: Long? = null
)