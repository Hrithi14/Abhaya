package com.pbrlm.abhaya.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pbrlm.abhaya.data.integration.mock.MockRoutingDataProvider
import com.pbrlm.abhaya.data.integration.mock.MockZoneDataProvider
import com.pbrlm.abhaya.data.user.MockUserProvider
import com.pbrlm.abhaya.data.user.UserProvider
import com.pbrlm.abhaya.domain.integration.RoutingDataProvider
import com.pbrlm.abhaya.domain.integration.ZoneDataProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideUserProvider(): UserProvider = MockUserProvider()

    // ── Integration providers (mock until teammate modules are connected) ──

    @Provides
    @Singleton
    fun provideZoneDataProvider(mock: MockZoneDataProvider): ZoneDataProvider = mock

    @Provides
    @Singleton
    fun provideRoutingDataProvider(mock: MockRoutingDataProvider): RoutingDataProvider = mock
}
