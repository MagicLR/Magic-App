package net.ddns.mask.data.ddns.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // 导入 Flow

@Dao
interface DdnsConfigDao {
    /**
     * 获取第一个 DDNS 配置，并作为 Flow 返回。
     * 假设通常只有一个配置，或者我们只关心第一个配置。
     * 如果没有配置，则返回 null。
     */
    @Query("SELECT * FROM ddns_configs LIMIT 1") // 获取第一个（或唯一的）配置
    fun getConfig(): Flow<DdnsConfigEntity?> // 返回 Flow，并将其设置为可空类型

    /**
     * 插入或更新 DDNS 配置。
     * 由于使用了 OnConflictStrategy.REPLACE，如果具有相同主键的配置已存在，
     * 它将被替换。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: DdnsConfigEntity) // 重命名以提高可读性

    /**
     * 删除指定的 DDNS 配置。
     */
    @Delete
    suspend fun delete(config: DdnsConfigEntity)

    /**
     * 获取所有的 DDNS 配置。（保留以备将来使用或调试）
     */
    @Query("SELECT * FROM ddns_configs")
    suspend fun getAllInternal(): List<DdnsConfigEntity> // 重命名以避免混淆
}
