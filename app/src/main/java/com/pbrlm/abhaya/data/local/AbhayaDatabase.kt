package com.pbrlm.abhaya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao
import com.pbrlm.abhaya.data.local.dao.HelpOfferDao
import com.pbrlm.abhaya.data.local.dao.VolunteerDao
import com.pbrlm.abhaya.data.local.entity.EmergencyRequestEntity
import com.pbrlm.abhaya.data.local.entity.HelpOfferEntity
import com.pbrlm.abhaya.data.local.entity.VolunteerEntity

@Database(
    entities = [
        EmergencyRequestEntity::class,
        VolunteerEntity::class,
        HelpOfferEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AbhayaDatabase : RoomDatabase() {
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun helpOfferDao(): HelpOfferDao
}
