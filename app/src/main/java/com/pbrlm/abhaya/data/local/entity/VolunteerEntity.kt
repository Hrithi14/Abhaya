package com.pbrlm.abhaya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pbrlm.abhaya.domain.model.HelpType
import com.pbrlm.abhaya.domain.model.Volunteer

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey
    val volunteerId: String,
    val userId: String,
    val name: String,
    val contactNumber: String,
    val latitude: Double,
    val longitude: Double,
    val safeZoneStatus: Boolean,
    val availableToHelp: Boolean,
    val helpType: String,
    val capacity: Int,
    val notes: String
) {
    fun toDomain(): Volunteer = Volunteer(
        volunteerId = volunteerId,
        userId = userId,
        name = name,
        contactNumber = contactNumber,
        latitude = latitude,
        longitude = longitude,
        safeZoneStatus = safeZoneStatus,
        availableToHelp = availableToHelp,
        helpType = HelpType.fromString(helpType),
        capacity = capacity,
        notes = notes
    )
}

fun Volunteer.toEntity(): VolunteerEntity = VolunteerEntity(
    volunteerId = volunteerId,
    userId = userId,
    name = name,
    contactNumber = contactNumber,
    latitude = latitude,
    longitude = longitude,
    safeZoneStatus = safeZoneStatus,
    availableToHelp = availableToHelp,
    helpType = helpType.name,
    capacity = capacity,
    notes = notes
)
