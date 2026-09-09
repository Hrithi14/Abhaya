package com.pbrlm.abhaya.domain.model

import java.util.Date

enum class HelpOfferStatus(val displayName: String) {
    OFFERED("Offered"),
    ACCEPTED("Accepted"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): HelpOfferStatus =
            values().firstOrNull { it.name == value } ?: OFFERED
    }
}

data class HelpOffer(
    val helpOfferId: String = "",
    val emergencyRequestId: String = "",
    val volunteerId: String = "",
    val userId: String = "",
    val helpType: HelpType = HelpType.OTHER,
    val message: String = "",
    val timestamp: Date = Date(),
    val status: HelpOfferStatus = HelpOfferStatus.OFFERED
)
