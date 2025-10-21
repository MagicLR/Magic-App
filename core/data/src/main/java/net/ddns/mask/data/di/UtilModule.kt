package net.ddns.mask.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.ddns.mask.data.util.IpAddressProvider
import net.ddns.mask.data.util.LocalIpAddressProvider
import net.ddns.mask.domain.ddns.repository.IPV6Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilModule {

    @Binds
    @Singleton // LocalIpAddressProvider is marked as @Singleton
    abstract fun bindIpAddressProvider(
        localIpAddressProvider: LocalIpAddressProvider
    ): IpAddressProvider
}
