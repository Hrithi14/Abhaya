import {
  collection,
  addDoc,
  onSnapshot,
  query,
  orderBy,
  doc,
  updateDoc,
  increment,
  serverTimestamp,
} from "firebase/firestore";
import { db } from "../../firebase";

const REPORTS_COLLECTION = "reports";

// Realistic fallback initial reports in Mangaluru for offline / initial state
export const DEFAULT_REPORTS = [
  {
    id: "rep_init_1",
    category: "FLOOD WATER",
    description: "Knee-deep water near MG Road junction. Left lane completely submerged.",
    latitude: 12.9148,
    longitude: 74.8552,
    createdAt: new Date(Date.now() - 2 * 60 * 1000),
    verified: true,
    upvotes: 14,
    waterDepth: "2.4 ft",
    userId: "user_field_01",
  },
  {
    id: "rep_init_2",
    category: "WATERLOGGING",
    description: "Heavy waterlogging under Pumpwell Flyover. Two wheelers stranded.",
    latitude: 12.8715,
    longitude: 74.8698,
    createdAt: new Date(Date.now() - 7 * 60 * 1000),
    verified: true,
    upvotes: 28,
    waterDepth: "1.8 ft",
    userId: "user_field_02",
  },
  {
    id: "rep_init_3",
    category: "FALLEN TREE",
    description: "Blocking left lane entirely near Ladyhill Circle.",
    latitude: 12.8942,
    longitude: 74.8385,
    createdAt: new Date(Date.now() - 10 * 60 * 1000),
    verified: false,
    upvotes: 5,
    waterDepth: null,
    userId: "user_field_03",
  },
  {
    id: "rep_init_4",
    category: "OPEN WIRE",
    description: "Snapping live electric wire in puddle near Kadri Park gate.",
    latitude: 12.8830,
    longitude: 74.8580,
    createdAt: new Date(Date.now() - 25 * 60 * 1000),
    verified: true,
    upvotes: 42,
    waterDepth: null,
    userId: "user_field_04",
  },
];

let localReports = [...DEFAULT_REPORTS];

/**
 * Subscribe to real-time reports via onSnapshot()
 */
export function subscribeToReports(onUpdate, onError) {
  if (!db) {
    // Graceful fallback to local in-memory state
    onUpdate(localReports);
    return () => {};
  }

  try {
    const q = query(collection(db, REPORTS_COLLECTION), orderBy("createdAt", "desc"));
    const unsubscribe = onSnapshot(
      q,
      (snapshot) => {
        if (snapshot.empty) {
          onUpdate(localReports);
          return;
        }
        const reports = [];
        snapshot.forEach((d) => {
          reports.push({
            id: d.id,
            ...d.data(),
          });
        });
        onUpdate(reports);
      },
      (err) => {
        console.warn("Firestore onSnapshot error, falling back gracefully:", err.message);
        if (onError) onError(err);
        onUpdate(localReports);
      }
    );
    return unsubscribe;
  } catch (err) {
    console.warn("Error setting up Firestore listener:", err.message);
    onUpdate(localReports);
    return () => {};
  }
}

/**
 * Submit a new hazard report to Firestore
 */
export async function submitReport(reportData) {
  const newReport = {
    category: reportData.category,
    description: reportData.description,
    latitude: Number(reportData.latitude),
    longitude: Number(reportData.longitude),
    waterDepth: reportData.waterDepth || null,
    imageUrl: reportData.imageUrl || null,
    verified: false,
    upvotes: 1,
    userId: reportData.userId || "anonymous_citizen",
    createdAt: serverTimestamp(),
  };

  if (!db) {
    const localId = "rep_loc_" + Date.now();
    const createdLocal = {
      id: localId,
      ...newReport,
      createdAt: new Date(),
    };
    localReports = [createdLocal, ...localReports];
    return createdLocal;
  }

  try {
    const docRef = await addDoc(collection(db, REPORTS_COLLECTION), newReport);
    return { id: docRef.id, ...newReport };
  } catch (error) {
    console.warn("Firestore write error, saving locally:", error);
    const localId = "rep_loc_" + Date.now();
    const createdLocal = {
      id: localId,
      ...newReport,
      createdAt: new Date(),
    };
    localReports = [createdLocal, ...localReports];
    return createdLocal;
  }
}

/**
 * Increment upvotes for a report
 */
export async function upvoteReport(reportId) {
  // Update local
  localReports = localReports.map((r) =>
    r.id === reportId ? { ...r, upvotes: (r.upvotes || 0) + 1 } : r
  );

  if (!db) return;
  try {
    const docRef = doc(db, REPORTS_COLLECTION, reportId);
    await updateDoc(docRef, {
      upvotes: increment(1),
    });
  } catch (e) {
    console.warn("Could not update Firestore upvote:", e.message);
  }
}

/**
 * Toggle verification (Admin action)
 */
export async function toggleReportVerification(reportId, currentStatus) {
  localReports = localReports.map((r) =>
    r.id === reportId ? { ...r, verified: !currentStatus } : r
  );

  if (!db) return;
  try {
    const docRef = doc(db, REPORTS_COLLECTION, reportId);
    await updateDoc(docRef, {
      verified: !currentStatus,
    });
  } catch (e) {
    console.warn("Could not update Firestore verification:", e.message);
  }
}
