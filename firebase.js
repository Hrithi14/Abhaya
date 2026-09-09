import { initializeApp, getApps, getApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";
import { getAuth } from "firebase/auth";

// Public client-side Firebase configuration
// Replace these with your Firebase project config from Firebase Console:
const firebaseConfig = {
  apiKey: process.env.EXPO_PUBLIC_FIREBASE_API_KEY || "AIzaSyDummyKey_ForAbhayaDev12345",
  authDomain: process.env.EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN || "abhaya-disaster-app.firebaseapp.com",
  projectId: process.env.EXPO_PUBLIC_FIREBASE_PROJECT_ID || "abhaya-disaster-app",
  storageBucket: process.env.EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET || "abhaya-disaster-app.appspot.com",
  messagingSenderId: process.env.EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || "109876543210",
  appId: process.env.EXPO_PUBLIC_FIREBASE_APP_ID || "1:109876543210:android:abc12345678",
};

let app;
let db = null;
let auth = null;

try {
  app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApp();
  db = getFirestore(app);
  auth = getAuth(app);
} catch (err) {
  console.warn("Firebase initialization warning (running in offline fallback mode):", err.message);
}

export { app, db, auth };
