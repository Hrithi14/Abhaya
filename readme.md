# ABHAYA - Real-Time Coastal Flood & Disaster Operations App

ABHAYA is a production-grade Android mobile application for real-time flood monitoring, citizen hazard reporting, emergency evacuation guidance, and national emergency response coordination.

Built for **Expo / React Native** and simultaneously running natively on **Android (Kotlin + Jetpack Compose)**.

---

## Key Features

1. **OpenStreetMap with Raster Tiles (No Google Maps API Key Needed)**:
   - Uses `react-native-maps` with `UrlTile` pointing to `https://tile.openstreetmap.org/{z}/{x}/{y}.png`.
   - Never requires a Google Maps API key or billing setup.
   - Danger zone radius circles for severe floodwater alerts.

2. **Real-Time GPS Location & Tracking**:
   - `expo-location` continuous walking tracking (`watchPositionAsync`).
   - Dynamically calculates distances to hazards using the mathematical **Haversine formula**.

3. **Cloud Firestore Real-Time Sync**:
   - Live updates without manual refresh using Firestore `onSnapshot`.
   - Citizen incident reporting with category, coordinates, description, water depth, and photo.
   - Community upvoting and responder verification.

4. **Dynamic AI Situation Summary**:
   - Amber alert card below top header analyzing real-time incidents and recommending safe paths to high-ground shelters.

5. **Direct Emergency SOS 112 Action**:
   - Floating emergency button dialing National Emergency `112` (`Linking.openURL("tel:112")`).
   - Categorized directory for nearest hospitals, police rescue bases, and high-ground shelters with elevation & capacity.

---

## Setup & Installation Instructions

### 1. Install Prerequisites
Make sure you have Node.js (v18+ recommended) and the Expo CLI installed:
```bash
npm install -g expo-cli
```

### 2. Install Project Dependencies
Run the following inside the project root:
```bash
npm install
```
Or with Expo CLI:
```bash
npx expo install react-native-maps expo-location @expo/vector-icons firebase
npm install nativewind
npm install --save-dev tailwindcss@3.3.2
```

### 3. Firebase Configuration
1. Open the [Firebase Console](https://console.firebase.google.com/) and create a new project called `abhaya-app`.
2. Add a **Web / Mobile App** and copy the configuration keys.
3. In `firebase.js`, add your credentials:
```javascript
const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "YOUR_PROJECT.firebaseapp.com",
  projectId: "YOUR_PROJECT_ID",
  storageBucket: "YOUR_PROJECT.appspot.com",
  messagingSenderId: "YOUR_SENDER_ID",
  appId: "YOUR_APP_ID"
};
```
4. In Firestore Database -> Rules, set:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /reports/{reportId} {
      allow read, write: if true;
    }
  }
}
```
*(Note: The app also includes offline fallback sample data for immediate testing even before Firebase setup).*

---

## How to Run on a Real Android Device

### Step 1: Install Expo Go
- Download **Expo Go** from the Google Play Store on your Android smartphone.

### Step 2: Start the Expo Development Server
In your terminal, run:
```bash
npx expo start
```
Or for network tunnel (if on different Wi-Fi):
```bash
npx expo start --tunnel
```

### Step 3: Scan QR Code
1. Open the **Expo Go** app on your Android phone.
2. Tap **Scan QR code** and point your camera at the QR code displayed in your terminal.
3. Grant location permissions when prompted to enable real-time GPS tracking.
