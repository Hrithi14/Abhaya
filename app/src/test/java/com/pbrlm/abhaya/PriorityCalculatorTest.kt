package com.pbrlm.abhaya

import com.pbrlm.abhaya.domain.model.*
import com.pbrlm.abhaya.domain.service.EmergencyIdGenerator
import com.pbrlm.abhaya.domain.service.PriorityCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * Unit tests for PriorityCalculator, EmergencyStatus, and EmergencyIdGenerator.
 * Run with: ./gradlew test  (no device needed)
 */
class PriorityCalculatorTest {

    // ── SOS ─────────────────────────────────────────────────────────────────
    @Test
    fun `SOS is always CRITICAL`() {
        assertEquals(EmergencyPriority.CRITICAL, PriorityCalculator.calculateForSos())
    }

    // ── Water Rescue ─────────────────────────────────────────────────────────
    @Test
    fun `trapped person makes water rescue CRITICAL`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.KNEE,
            waterRising    = false,
            peopleTrapped  = true,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `above chest water is CRITICAL`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.ABOVE_CHEST,
            waterRising    = false,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `chest level with rising water is CRITICAL`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.CHEST,
            waterRising    = true,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `chest level not rising is HIGH`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.CHEST,
            waterRising    = false,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    @Test
    fun `rising waist with children present is CRITICAL`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.WAIST,
            waterRising    = true,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 2,
            children       = true,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `ankle level no vulnerabilities is MEDIUM`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.ANKLE,
            waterRising    = false,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.MEDIUM, p)
    }

    @Test
    fun `knee level no vulnerabilities is MEDIUM`() {
        val p = PriorityCalculator.calculateForWaterRescue(
            waterDepth     = WaterDepth.KNEE,
            waterRising    = false,
            peopleTrapped  = false,
            injured        = false,
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.MEDIUM, p)
    }

    // ── Medical ──────────────────────────────────────────────────────────────
    @Test
    fun `unconscious person is CRITICAL`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.OTHER,
            personUnconscious = true,
            breathingProblem  = false,
            severeBleeding    = false,
            pregnancyRelated  = false,
            injured           = false,
            numberOfPeople    = 1,
            children          = false,
            elderly           = false,
            disabled          = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `breathing problem medical type is CRITICAL`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.BREATHING_PROBLEM,
            personUnconscious = false,
            breathingProblem  = true,
            severeBleeding    = false,
            pregnancyRelated  = false,
            injured           = false,
            numberOfPeople    = 1,
            children          = false,
            elderly           = false,
            disabled          = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `severe bleeding is CRITICAL`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.SEVERE_BLEEDING,
            personUnconscious = false,
            breathingProblem  = false,
            severeBleeding    = true,
            pregnancyRelated  = false,
            injured           = false,
            numberOfPeople    = 1,
            children          = false,
            elderly           = false,
            disabled          = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `pregnancy type is HIGH`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.PREGNANCY,
            personUnconscious = false,
            breathingProblem  = false,
            severeBleeding    = false,
            pregnancyRelated  = true,
            injured           = false,
            numberOfPeople    = 1,
            children          = false,
            elderly           = false,
            disabled          = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    @Test
    fun `injury with elderly is HIGH`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.INJURY,
            personUnconscious = false,
            breathingProblem  = false,
            severeBleeding    = false,
            pregnancyRelated  = false,
            injured           = true,
            numberOfPeople    = 1,
            children          = false,
            elderly           = true,
            disabled          = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    @Test
    fun `minor other medical is MEDIUM`() {
        val p = PriorityCalculator.calculateForMedical(
            medicalType       = MedicalEmergencyType.OTHER,
            personUnconscious = false,
            breathingProblem  = false,
            severeBleeding    = false,
            pregnancyRelated  = false,
            injured           = false,
            numberOfPeople    = 1,
            children          = false,
            elderly           = false,
            disabled          = false
        )
        assertEquals(EmergencyPriority.MEDIUM, p)
    }

    // ── Boat Rescue ───────────────────────────────────────────────────────────
    @Test
    fun `boat rescue with people trapped is CRITICAL`() {
        val p = PriorityCalculator.calculateForBoatRescue(
            waterDepth           = WaterDepth.WAIST,
            waterRising          = false,
            peopleTrapped        = true,
            hasmedicalEmergency  = false,
            numberOfPeople       = 2,
            children             = false,
            elderly              = false,
            disabled             = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `boat rescue with medical emergency is CRITICAL`() {
        val p = PriorityCalculator.calculateForBoatRescue(
            waterDepth           = WaterDepth.KNEE,
            waterRising          = false,
            peopleTrapped        = false,
            hasmedicalEmergency  = true,
            numberOfPeople       = 1,
            children             = false,
            elderly              = false,
            disabled             = false
        )
        assertEquals(EmergencyPriority.CRITICAL, p)
    }

    @Test
    fun `standard boat rescue is at least HIGH`() {
        val p = PriorityCalculator.calculateForBoatRescue(
            waterDepth           = WaterDepth.KNEE,
            waterRising          = false,
            peopleTrapped        = false,
            hasmedicalEmergency  = false,
            numberOfPeople       = 1,
            children             = false,
            elderly              = false,
            disabled             = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    // ── Shelter ───────────────────────────────────────────────────────────────
    @Test
    fun `shelter with children is HIGH`() {
        val p = PriorityCalculator.calculateForShelter(
            numberOfPeople = 2,
            children       = true,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    @Test
    fun `shelter with elderly is HIGH`() {
        val p = PriorityCalculator.calculateForShelter(
            numberOfPeople = 1,
            children       = false,
            elderly        = true,
            disabled       = false
        )
        assertEquals(EmergencyPriority.HIGH, p)
    }

    @Test
    fun `shelter no vulnerabilities is MEDIUM`() {
        val p = PriorityCalculator.calculateForShelter(
            numberOfPeople = 1,
            children       = false,
            elderly        = false,
            disabled       = false
        )
        assertEquals(EmergencyPriority.MEDIUM, p)
    }

    // ── Escalation ────────────────────────────────────────────────────────────
    @Test
    fun `CRITICAL ACTIVE over 30 min triggers escalation`() {
        val now       = System.currentTimeMillis()
        val createdAt = now - (31 * 60 * 1000L)
        val result = PriorityCalculator.isEscalationRequired(
            priority          = EmergencyPriority.CRITICAL,
            status            = EmergencyStatus.ACTIVE,
            createdAtMillis   = createdAt,
            currentTimeMillis = now
        )
        assertTrue(result)
    }

    @Test
    fun `CRITICAL ACTIVE under 30 min does NOT escalate`() {
        val now       = System.currentTimeMillis()
        val createdAt = now - (15 * 60 * 1000L)
        val result = PriorityCalculator.isEscalationRequired(
            priority          = EmergencyPriority.CRITICAL,
            status            = EmergencyStatus.ACTIVE,
            createdAtMillis   = createdAt,
            currentTimeMillis = now
        )
        assertEquals(false, result)
    }

    @Test
    fun `CRITICAL RESOLVED does not escalate even after 1 hour`() {
        val now       = System.currentTimeMillis()
        val createdAt = now - (60 * 60 * 1000L)
        val result = PriorityCalculator.isEscalationRequired(
            priority          = EmergencyPriority.CRITICAL,
            status            = EmergencyStatus.RESOLVED,
            createdAtMillis   = createdAt,
            currentTimeMillis = now
        )
        assertEquals(false, result)
    }

    @Test
    fun `HIGH priority never escalates`() {
        val now       = System.currentTimeMillis()
        val createdAt = now - (60 * 60 * 1000L)
        val result = PriorityCalculator.isEscalationRequired(
            priority          = EmergencyPriority.HIGH,
            status            = EmergencyStatus.ACTIVE,
            createdAtMillis   = createdAt,
            currentTimeMillis = now
        )
        assertEquals(false, result)
    }

    @Test
    fun `CRITICAL ACKNOWLEDGED over 30 min escalates`() {
        val now       = System.currentTimeMillis()
        val createdAt = now - (35 * 60 * 1000L)
        val result = PriorityCalculator.isEscalationRequired(
            priority          = EmergencyPriority.CRITICAL,
            status            = EmergencyStatus.ACKNOWLEDGED,
            createdAtMillis   = createdAt,
            currentTimeMillis = now
        )
        assertTrue(result)
    }

    // ── EmergencyStatus ───────────────────────────────────────────────────────
    @Test
    fun `terminal statuses are correct`() {
        assertEquals(true,  EmergencyStatus.RESOLVED.isTerminal)
        assertEquals(true,  EmergencyStatus.CANCELLED.isTerminal)
        assertEquals(true,  EmergencyStatus.FALSE_REPORT.isTerminal)
        assertEquals(false, EmergencyStatus.ACTIVE.isTerminal)
        assertEquals(false, EmergencyStatus.ACKNOWLEDGED.isTerminal)
        assertEquals(false, EmergencyStatus.RESCUE_IN_PROGRESS.isTerminal)
    }

    @Test
    fun `fromString returns correct status`() {
        assertEquals(EmergencyStatus.ACTIVE,             EmergencyStatus.fromString("ACTIVE"))
        assertEquals(EmergencyStatus.RESCUE_IN_PROGRESS, EmergencyStatus.fromString("RESCUE_IN_PROGRESS"))
        assertEquals(EmergencyStatus.ACTIVE,             EmergencyStatus.fromString("UNKNOWN_VALUE"))
    }

    @Test
    fun `fromString returns correct priority`() {
        assertEquals(EmergencyPriority.CRITICAL, EmergencyPriority.fromString("CRITICAL"))
        assertEquals(EmergencyPriority.HIGH,     EmergencyPriority.fromString("HIGH"))
        assertEquals(EmergencyPriority.LOW,      EmergencyPriority.fromString("UNKNOWN"))
    }

    // ── EmergencyIdGenerator ──────────────────────────────────────────────────
    @Test
    fun `demo IDs start with ER-DEMO`() {
        val id = EmergencyIdGenerator.generate(isDemoData = true)
        assertTrue("Expected ER-DEMO- prefix, got: $id", id.startsWith("ER-DEMO-"))
    }

    @Test
    fun `real IDs contain current year`() {
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        val id   = EmergencyIdGenerator.generate(isDemoData = false)
        assertTrue("Expected year $year in ID, got: $id", id.contains(year))
    }

    @Test
    fun `volunteer IDs start with VOL`() {
        val id = EmergencyIdGenerator.generateVolunteerId()
        assertTrue("Expected VOL- prefix, got: $id", id.startsWith("VOL-"))
    }

    @Test
    fun `help offer IDs start with HO`() {
        val id = EmergencyIdGenerator.generateHelpOfferId()
        assertTrue("Expected HO- prefix, got: $id", id.startsWith("HO-"))
    }

    @Test
    fun `escalation IDs start with ESC`() {
        val id = EmergencyIdGenerator.generateEscalationId()
        assertTrue("Expected ESC- prefix, got: $id", id.startsWith("ESC-"))
    }

    @Test
    fun `sequential IDs are always unique`() {
        val ids = (1..10).map { EmergencyIdGenerator.generate() }
        assertEquals(ids.size, ids.toSet().size)
    }
}
