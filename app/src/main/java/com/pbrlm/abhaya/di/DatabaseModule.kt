package com.pbrlm.abhaya.di

import android.content.Context
import androidx.room.Room
import com.pbrlm.abhaya.data.local.AbhayaDatabase
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao
import com.pbrlm.abhaya.data.local.dao.HelpOfferDao
import com.pbrlm.abhaya.data.local.dao.VolunteerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AbhayaDatabase =
        Room.databaseBuilder(context, AbhayaDatabase::class.java, "abhaya.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEmergencyRequestDao(db: AbhayaDatabase): EmergencyRequestDao = db.emergencyRequestDao()

    @Provides
    fun provideVolunteerDao(db: AbhayaDatabase): VolunteerDao = db.volunteerDao()

    @Provides
    fun provideHelpOfferDao(db: AbhayaDatabase): HelpOfferDao = db.helpOfferDao()
}
