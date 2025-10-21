package net.ddns.mask.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.ddns.mask.data.ddns.repository.DdnsRepositoryImpl
import net.ddns.mask.domain.ddns.repository.DdnsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 通常 Repository 是单例
abstract class RepositoryModule {

    @Binds
    @Singleton // 如果 DdnsRepositoryImpl 是无状态的或者其依赖是单例，则可将其设为单例
    abstract fun bindDdnsRepository(
        ddnsRepositoryImpl: DdnsRepositoryImpl
    ): DdnsRepository
}