package com.pbrlm.abhaya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pbrlm.abhaya.domain.model.HelpOffer
import com.pbrlm.abhaya.domain.model.HelpOfferStatus
import com.pbrlm.abhaya.domain.model.HelpType
import java.util.Date

@Entity(tableName = "help_offers")
data class HelpOfferEntity(
    @PrimaryKey
    val helpOfferId: String,
    val emergencyRequestId: String,
    val volunteerId: String,
    val userId: String,
    val helpType: String,
    val message: String,
    val timestampMillis: Long,
    val status: String
) {
    fun toDomain(): HelpOffer = HelpOffer(
        helpOfferId = helpOfferId,
        emergencyRequestId = emergencyRequestId,
        volunteerId = volunteerId,
        userId = userId,
        helpType = HelpType.fromString(helpType),
        message = message,
        timestamp = Date(timestampMillis),
        status = HelpOfferStatus.fromString(status)
    )
}

fun HelpOffer.toEntity(): HelpOfferEntity = HelpOfferEntity(
    helpOfferId = helpOfferId,
    emergencyRequestId = emergencyRequestId,
    volunteerId = volunteerId,
    userId = userId,
    helpType = helpType.name,
    message = message,
    timestampMillis = timestamp.time,
    status = status.name
)
