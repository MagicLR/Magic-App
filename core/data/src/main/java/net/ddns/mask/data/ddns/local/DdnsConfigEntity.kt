package net.ddns.mask.data.ddns.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.ddns.mask.domain.ddns.model.DdnsConfig

@Entity(tableName = "ddns_configs")
data class DdnsConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val provider: String,
    val hostname: String,
    val username: String,
    val password: String,
    val lastUpdateTime: Long? = null
) {
    fun toDomain() = DdnsConfig(id, provider, hostname, username, password, lastUpdateTime)
}

fun DdnsConfig.toEntity() =
    DdnsConfigEntity(id, provider, hostname, username, password, lastUpdateTime)