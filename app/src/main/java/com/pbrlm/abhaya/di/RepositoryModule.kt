package com.pbrlm.abhaya.di

import com.pbrlm.abhaya.data.repository.LocalEmergencyRepository
import com.pbrlm.abhaya.data.repository.LocalHelpOfferRepository
import com.pbrlm.abhaya.data.repository.LocalVolunteerRepository
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import com.pbrlm.abhaya.domain.repository.HelpOfferRepository
import com.pbrlm.abhaya.domain.repository.VolunteerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindEmergencyRepository(impl: LocalEmergencyRepository): EmergencyRepository

    @Binds @Singleton
    abstract fun bindVolunteerRepository(impl: LocalVolunteerRepository): VolunteerRepository

    @Binds @Singleton
    abstract fun bindHelpOfferRepository(impl: LocalHelpOfferRepository): HelpOfferRepository
}
