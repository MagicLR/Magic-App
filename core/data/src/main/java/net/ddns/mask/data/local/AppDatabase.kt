package net.ddns.mask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import net.ddns.mask.data.ddns.local.DdnsConfigDao // 需要导入 DAO
import net.ddns.mask.data.ddns.local.DdnsConfigEntity // 需要导入 Entity

@Database(entities = [DdnsConfigEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ddnsConfigDao(): DdnsConfigDao

}