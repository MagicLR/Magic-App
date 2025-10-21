package net.ddns.mask.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.ddns.mask.data.ddns.local.DdnsConfigDao
import net.ddns.mask.data.local.AppDatabase // 确保导入正确的 AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "magic_app_database" // 数据库文件名
        )
            // .fallbackToDestructiveMigration() // 如果暂时不想处理迁移，可以加上这个，但生产环境慎用
            .build()
    }

    @Provides
    fun provideDdnsConfigDao(appDatabase: AppDatabase): DdnsConfigDao {
        return appDatabase.ddnsConfigDao()
    }
}