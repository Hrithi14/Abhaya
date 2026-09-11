import { initializeApp, getApps } from "firebase/app";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";
import { initializeAuth, getReactNativePersistence } from "firebase/auth";
import AsyncStorage from "@react-native-async-storage/async-storage";

const firebaseConfig = {
  apiKey:            "AIzaSyAdqvtRI1InVedbMCA8grOvp29sfuA4yz8",
  authDomain:        "abhaya-b4364.firebaseapp.com",
  projectId:         "abhaya-b4364",
  storageBucket:     "abhaya-b4364.firebasestorage.app",
  messagingSenderId: "427948214907",
  appId:             "1:427948214907:web:f4c1158ad8fb39349d30de",
};

const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApps()[0];

export const db = getFirestore(app);
export const storage = getStorage(app);

// Use AsyncStorage persistence so auth survives app restarts
import { getAuth } from "firebase/auth";

let auth: ReturnType<typeof initializeAuth>;
try {
  auth = initializeAuth(app, {
    persistence: getReactNativePersistence(AsyncStorage),
  });
} catch {
  auth = getAuth(app);
}
export { auth };

export default app;
