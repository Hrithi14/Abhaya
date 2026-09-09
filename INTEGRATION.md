# PBRLM Integration Guide — Module 5 (Emergency Portal)

This document explains exactly how each teammate connects their module to the Emergency Portal.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PBRLM Android App                        │
│                                                             │
│  Module 1        Module 2        Module 3                   │
│  Hazard Ingestion  Trust Scoring   Zone Classification      │
│       │                                │                    │
│       ▼                                ▼                    │
│  HazardDataProvider          ZoneDataProvider               │
│       │                                │                    │
│       └──────────────┬─────────────────┘                   │
│                      ▼                                      │
│            ┌─────────────────┐                              │
│            │  Module 5       │                              │
│            │  Emergency      │◄── ResponderDataProvider     │
│            │  Portal         │◄── RoutingDataProvider       │
│            └─────────────────┘         ▲                    │
│                                        │                    │
│              Module 4          Module 6                     │
│              Municipal         Evacuation                   │
│              Control Room      Routing                      │
└─────────────────────────────────────────────────────────────┘
```

All integration points are **interfaces** in:
```
app/src/main/java/com/pbrlm/abhaya/domain/integration/
```

Mock implementations are in:
```
app/src/main/java/com/pbrlm/abhaya/data/integration/mock/
```

---

## How to Connect Each Module

---

### MODULE 1 — Hazard Ingestion (Anti-Duplicate Reporting)

**Interface:** `HazardDataProvider`
**File:** `domain/integration/HazardDataProvider.kt`

**What it provides to Emergency Portal:**
- `clusterId` — links an emergency to a nearby hazard cluster
- `hazardId` — the root hazard report ID

**Steps to connect:**
1. Create your implementation class:
```kotlin
class FirebaseHazardDataProvider @Inject constructor(
    private val firestore: FirebaseFirestore
) : HazardDataProvider {
    override suspend fun getNearestClusterId(lat: Double, lng: Double, radius: Double): String? {
        // Query your hazard clusters collection by geo proximity
        // Return the nearest cluster ID or null
    }
    override suspend fun getHazardIdForCluster(clusterId: String): String? {
        // Return hazard ID for this cluster
    }
}
```

2. In `di/AppModule.kt`, replace:
```kotlin
// No existing binding for HazardDataProvider — add:
@Provides @Singleton
fun provideHazardDataProvider(impl: FirebaseHazardDataProvider): HazardDataProvider = impl
```

3. The Emergency Portal will automatically attach `hazardId` and `clusterId`
   to new emergency requests once your provider returns real values.

**Data model join point in EmergencyRequest:**
```kotlin
val hazardId: String?   // populated by your module
val clusterId: String?  // populated by your module
val zoneId: String?     // populated by Zone module
```

---

### MODULE 2 — Proximity Verification + Trust Scoring

**No direct interface required.**

Module 2 reads `EmergencyRequest` records from Firestore/Room and adds trust scores.
The `EmergencyRequest` domain model already stores `userId` for trust calculation.

**How to read emergency data:**
```kotlin
// Inject EmergencyRepository and observe:
emergencyRepository.observeAllEmergencies(userId)
// Or query Firestore directly from the shared collection:
// firestore.collection("emergency_requests")
```

---

### MODULE 3 — Zone Classification (Green / Yellow / Red)

**Interface:** `ZoneDataProvider`
**File:** `domain/integration/ZoneDataProvider.kt`

**What it provides to Emergency Portal:**
- Real-time zone status (GREEN / YELLOW / RED) for the user's current location
- Triggers the Red Zone Alert Banner on the Emergency Home screen

**Steps to connect:**
1. Create your implementation:
```kotlin
class FirebaseZoneDataProvider @Inject constructor(
    private val firestore: FirebaseFirestore
) : ZoneDataProvider {
    override suspend fun getZoneForLocation(lat: Double, lng: Double): ZoneInfo? {
        // Query your zones collection, find the zone containing this point
        // Return ZoneInfo or null if no zone data
    }
    override fun observeZoneForLocation(lat: Double, lng: Double): Flow<ZoneInfo?> = flow {
        // Stream zone updates using Firestore snapshot listeners
    }
}
```

2. In `di/AppModule.kt`, replace the mock binding:
```kotlin
// BEFORE (mock):
@Provides @Singleton
fun provideZoneDataProvider(mock: MockZoneDataProvider): ZoneDataProvider = mock

// AFTER (real):
@Provides @Singleton
fun provideZoneDataProvider(impl: FirebaseZoneDataProvider): ZoneDataProvider = impl
```

3. The Emergency Home screen will automatically show the RED ZONE ALERT banner
   when your module returns `ZoneStatus.RED`.

**ZoneInfo fields:**
```kotlin
data class ZoneInfo(
    val zoneId: String,
    val zoneStatus: ZoneStatus,       // GREEN / YELLOW / RED
    val riskLevel: Int,               // 0–100
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Double,
    val activeIncidentCount: Int,
    val floodSeverity: String,
    val waterRising: Boolean,
    val lastUpdated: Long             // epoch millis
)
```

---

### MODULE 4 — Municipal Control Room + Dispatch

**Interface:** `ResponderDataProvider`
**File:** `domain/integration/ResponderDataProvider.kt`

**What the Municipal module can do:**
- Acknowledge an emergency → changes status to ACKNOWLEDGED
- Start rescue → changes status to RESCUE_IN_PROGRESS
- Resolve emergency → changes status to RESOLVED
- Read `ResponderSummary` for control room dashboard

**Steps to connect:**
1. Your module injects `EmergencyRepository` (already a Hilt singleton):
```kotlin
@HiltViewModel
class ControlRoomViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {
    // Read all critical active emergencies:
    val criticalEmergencies = emergencyRepository
        .getCriticalUnresolvedEmergencies()

    fun acknowledge(id: String) {
        emergencyRepository.updateStatus(id, EmergencyStatus.ACKNOWLEDGED)
    }
    fun startRescue(id: String) {
        emergencyRepository.updateStatus(id, EmergencyStatus.RESCUE_IN_PROGRESS)
    }
    fun resolve(id: String) {
        emergencyRepository.updateStatus(id, EmergencyStatus.RESOLVED)
    }
}
```

2. The Emergency Detail screen already shows responder information when
   `assignedResponderId` and `responderContact` are populated.
   Set these fields when your module assigns a responder:
```kotlin
// Update the entity directly via DAO or add a new repository method
```

**Responder-ready data fields in EmergencyRequest:**
```kotlin
val assignedResponderId: String?
val responderContact: String?
val responseTime: Date?
val resolutionTime: Date?
val responderNotes: String?
val escalationRequired: Boolean
```

---

### MODULE 6 — Elevation-Aware Evacuation Routing

**Interface:** `RoutingDataProvider`
**File:** `domain/integration/RoutingDataProvider.kt`

**What it provides to Emergency Portal:**
- Safe evacuation route from user's current GPS location
- Displayed on the Evacuate to Safety screen

**Steps to connect:**
1. Create your implementation:
```kotlin
class ElevationRoutingDataProvider @Inject constructor(
    // your routing engine dependencies
) : RoutingDataProvider {
    override suspend fun getEvacuationRoute(
        currentLatitude: Double,
        currentLongitude: Double
    ): EvacuationRoute {
        // Calculate elevation-aware route
        // Return EvacuationRoute with all fields populated
    }
}
```

2. In `di/AppModule.kt`, replace the mock:
```kotlin
// BEFORE (mock):
@Provides @Singleton
fun provideRoutingDataProvider(mock: MockRoutingDataProvider): RoutingDataProvider = mock

// AFTER (real):
@Provides @Singleton
fun provideRoutingDataProvider(impl: ElevationRoutingDataProvider): RoutingDataProvider = impl
```

**EvacuationRoute fields:**
```kotlin
data class EvacuationRoute(
    val currentLatitude: Double,
    val currentLongitude: Double,
    val safeDestination: String,
    val safeRoute: String,            // human-readable directions
    val distanceKm: Double,
    val estimatedTimeMinutes: Int,
    val nearestHighElevation: String,
    val nearestReliefCenter: String,
    val avoidedRedZones: List<String>,
    val routeStatus: RouteStatus
)
```

---

## Shared Firestore Collection Schema

If the team uses a shared Firestore backend, use this collection structure:

```
/emergency_requests/{emergencyRequestId}
    emergencyRequestId: String
    userId: String
    hazardId: String?           ← Module 1 writes this
    clusterId: String?          ← Module 1 writes this
    zoneId: String?             ← Module 3 writes this
    emergencyType: String
    latitude: Double
    longitude: Double
    gpsAccuracy: Float
    timestampMillis: Long
    numberOfPeople: Int
    waterDepth: String?
    waterRising: Boolean?
    peopleTrapped: Boolean
    injured: Boolean
    medicalRequired: Boolean
    medicalEmergencyType: String?
    children: Boolean
    elderly: Boolean
    disabled: Boolean
    description: String
    buildingFloor: String?
    contactNumber: String
    photoUrl: String?
    priority: String            (CRITICAL / HIGH / MEDIUM / LOW)
    status: String              (ACTIVE / ACKNOWLEDGED / RESCUE_IN_PROGRESS / RESOLVED / CANCELLED)
    assignedResponderId: String? ← Module 4 writes this
    responderContact: String?   ← Module 4 writes this
    responseTime: Timestamp?    ← Module 4 writes this
    resolutionTime: Timestamp?  ← Module 4 writes this
    responderNotes: String?     ← Module 4 writes this
    escalationRequired: Boolean
    isDemoData: Boolean         (always false in production)

/volunteers/{volunteerId}
    volunteerId: String
    userId: String
    name: String
    contactNumber: String
    latitude: Double
    longitude: Double
    safeZoneStatus: Boolean
    availableToHelp: Boolean
    helpType: String
    capacity: Int
    notes: String

/help_offers/{helpOfferId}
    helpOfferId: String
    emergencyRequestId: String
    volunteerId: String
    userId: String
    helpType: String
    message: String
    timestampMillis: Long
    status: String
```

---

## Navigation Integration

The Emergency Portal uses Jetpack Compose Navigation.

To add your module's screens to the same app:

1. Add your routes to a new file (do NOT modify `NavRoutes.kt`):
```kotlin
// In your module:
sealed class YourModuleRoutes(val route: String) {
    object LiveMap      : YourModuleRoutes("live_map")
    object ReportHazard : YourModuleRoutes("report_hazard")
    object AdminPanel   : YourModuleRoutes("admin_panel")
}
```

2. Add composables to `AbhayaNavGraph.kt`:
```kotlin
composable(YourModuleRoutes.LiveMap.route) {
    LiveMapScreen(navController = navController)
}
```

3. Add bottom nav items to `BottomNavBar.kt` if needed.

---

## Package Structure for Merged App

When all modules are merged, the recommended structure is:
```
com.pbrlm.abhaya/
├── emergency/      ← This module (Module 5)
├── hazard/         ← Module 1+2+3
├── map/            ← Module 3 UI
├── municipal/      ← Module 4
├── routing/        ← Module 6
└── shared/         ← Common models, theme, components
```

Each module keeps its own `ui/`, `data/`, `domain/` structure internally.

---

## Questions?

Contact the Emergency Portal developer for:
- `EmergencyRequest` model questions
- Repository interface changes
- Navigation integration help
- Hilt DI wiring assistance
