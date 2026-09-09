# Abhaya — PBRLM Emergency Portal (Module 5)

**PBRLM — Crowdsourced Monsoon Hazard & Peer-to-Peer Safety System**

> This is the Emergency Portal / SOS / Community Mutual Aid module.
> Built as a standalone Android module that integrates with teammates' modules later.

---

## Project Purpose

Abhaya is the emergency-response interface for the PBRLM system.  
During monsoon conditions, citizens can:
- Send a one-tap SOS signal with GPS coordinates
- Report being trapped by water, needing medical help, boat rescue, or shelter
- Connect with community volunteers for peer-to-peer aid
- View safe evacuation routes (via routing module)
- Get real-time zone alerts (via zone classification module)

---

## Architecture

```
app/
├── data/
│   ├── demo/           ← Demo data seeder (debug only)
│   ├── integration/
│   │   └── mock/       ← Mock implementations of teammate interfaces
│   ├── local/
│   │   ├── dao/        ← Room DAOs
│   │   └── entity/     ← Room entities + mappers
│   ├── location/       ← Android Fused Location Provider
│   ├── repository/     ← Concrete repository implementations
│   └── user/           ← User provider (mock → replace with FirebaseAuth)
├── di/                 ← Hilt dependency injection modules
├── domain/
│   ├── integration/    ← Interfaces for teammate modules
│   ├── model/          ← Core domain models (no Android dependencies)
│   ├── repository/     ← Repository interfaces
│   └── service/        ← PriorityCalculator, EmergencyIdGenerator
├── navigation/         ← Compose Navigation routes + nav graph
└── ui/
    ├── components/     ← Shared UI components
    ├── screens/        ← One package per screen
    └── theme/          ← Material 3 dark theme
```

Pattern: **MVVM + Repository + Hilt DI + Kotlin Coroutines + StateFlow**

---

## How to Run

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35
- Physical device or emulator running Android 8.0+ (API 26+)

### Steps
1. Clone / open this project in Android Studio
2. Let Gradle sync complete (first sync downloads dependencies)
3. Add your Firebase project's `google-services.json` to `app/`  
   *(the placeholder file works for local Room-only builds)*
4. Run on emulator or physical device:
   - Menu → Run → Run 'app'
   - Or press **Shift+F10**

### Debug Mode
The app auto-seeds 5 demo emergency records on first launch in debug builds.  
Demo records have IDs like `ER-DEMO-001` and are clearly labelled.

---

## Android Permissions

| Permission | Purpose |
|---|---|
| `ACCESS_FINE_LOCATION` | GPS coordinates for emergency requests |
| `ACCESS_COARSE_LOCATION` | Fallback location |
| `INTERNET` | Firebase sync (future) |
| `CAMERA` | Photo attachment for emergencies |
| `READ_MEDIA_IMAGES` | Photo picker (Android 13+) |
| `CALL_PHONE` | Optional direct call (ACTION_DIAL used by default) |
| `POST_NOTIFICATIONS` | Emergency status notifications |

---

## Firebase Configuration

The app uses Room (local) storage by default.  
Firebase Firestore is available for cloud sync.

To enable Firebase:
1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package name `com.pbrlm.abhaya`
3. Download `google-services.json` and place it in `app/`
4. Firestore rules — add these security rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /emergency_requests/{id} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## Emergency Workflow

```
User opens Abhaya
    ↓
GPS acquired automatically
    ↓
User taps "Trapped by Water" / "SOS" / etc.
    ↓
Form filled (people, depth, contact, optional photo)
    ↓
Submit → Priority calculated automatically
    ↓
Emergency ID generated (e.g. ER-2026-000001)
    ↓
Saved to Room DB (+ Firebase when configured)
    ↓
Status = ACTIVE
    ↓
Appears in My Active Emergencies
    ↓
Community Help screen shows it to nearby volunteers
    ↓
Responder acknowledges → Status = ACKNOWLEDGED
    ↓
Rescue starts → Status = RESCUE_IN_PROGRESS
    ↓
Resolved → Status = RESOLVED
```

---

## GPS Workflow

1. App requests `ACCESS_FINE_LOCATION` permission on first launch
2. `LocationService` uses Android **Fused Location Provider**
3. Home screen shows live lat/lng/accuracy/GPS status
4. Every emergency form captures GPS at submission time
5. If GPS is unavailable: user is notified, form can still be submitted

---

## Calling Workflow

**CALL 112:**
1. User taps CALL 112
2. Confirmation dialog shown
3. User confirms → `Intent(ACTION_DIAL, "tel:112")` opens Android dialer
4. User presses Call on their phone to connect

**CALL RESCUE:**
- Same flow, number is configurable via `BuildConfig.RESCUE_NUMBER`
- Default: 112
- Change in `app/build.gradle.kts` → `buildConfigField("String", "RESCUE_NUMBER", "\"YOUR_NUMBER\"")`

**SOS button does NOT call anyone** — it creates a digital record only.

---

## Priority System

| Priority | Trigger Conditions |
|---|---|
| CRITICAL | Trapped, above-chest water, rising water + vulnerable people, unconscious, breathing problem, severe bleeding, SOS |
| HIGH | Chest water, boat rescue, pregnancy, children/elderly/disabled, multiple injured |
| MEDIUM | Shelter, ankle/knee water, non-urgent |
| LOW | Other non-critical |

Escalation: CRITICAL + ACTIVE + unresolved for >30 minutes → `escalationRequired = true`

---

## Running Tests

```bash
# Unit tests (no device needed)
./gradlew test

# Or in Android Studio:
# Right-click PriorityCalculatorTest → Run
```

Tests cover: priority calculation, status transitions, escalation logic, ID generation.

---

## Team Integration

See **INTEGRATION.md** for how to connect your modules.
