package com.pbrlm.abhaya.domain.service

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generates unique Emergency Request IDs.
 * Format: ER-YYYY-NNNNNN
 * Example: ER-2026-000001
 *
 * Demo IDs use: ER-DEMO-NNN
 */
object EmergencyIdGenerator {

    private val counter = AtomicInteger(0)
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    fun generate(isDemoData: Boolean = false): String {
        val seq = counter.incrementAndGet()
        return if (isDemoData) {
            "ER-DEMO-${seq.toString().padStart(3, '0')}"
        } else {
            val year = yearFormat.format(Date())
            "ER-$year-${seq.toString().padStart(6, '0')}"
        }
    }

    fun generateVolunteerId(): String {
        val seq = counter.incrementAndGet()
        return "VOL-${seq.toString().padStart(4, '0')}"
    }

    fun generateHelpOfferId(): String {
        val seq = counter.incrementAndGet()
        return "HO-${seq.toString().padStart(4, '0')}"
    }

    fun generateEscalationId(): String {
        val seq = counter.incrementAndGet()
        return "ESC-${seq.toString().padStart(4, '0')}"
    }
}
