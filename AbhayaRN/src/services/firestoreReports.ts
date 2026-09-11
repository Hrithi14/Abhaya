/**
 * Firestore real-time sync for HazardReports.
 */
import {
  collection, addDoc, onSnapshot, doc,
  updateDoc, deleteDoc, orderBy, query, serverTimestamp,
} from "firebase/firestore";
import { db } from "./firebase";
import { hazardStore, HazardReport, HazardCategory } from "./hazardStore";

const COL = "hazardReports";

// ── Type used by AdminScreen ───────────────────────────────────────────────
export interface FirestoreHazardReport {
  id: string;
  category: HazardCategory;
  description: string;
  latitude: number;
  longitude: number;
  createdAt: number;
  verified: boolean;
  upvotes: number;
  waterDepth?: string;
  imageUrl?: string;
  radiusMeters: number;
}

// ── Push a new report ──────────────────────────────────────────────────────
export async function pushReportToFirestore(report: HazardReport): Promise<string> {
  try {
    const ref = await addDoc(collection(db, COL), {
      ...report,
      createdAtServer: serverTimestamp(),
    });
    return ref.id;
  } catch {
    return report.id; // offline fallback
  }
}

// ── Subscribe — updates hazardStore AND calls optional callback ────────────
export function subscribeToFirestoreReports(
  callback?: (reports: FirestoreHazardReport[]) => void
): () => void {
  try {
    const q = query(collection(db, COL), orderBy("createdAt", "desc"));
    const unsub = onSnapshot(
      q,
      (snap) => {
        const remote: FirestoreHazardReport[] = snap.docs.map((d) => {
          const data = d.data();
          return {
            id:           d.id,
            category:     data.category as HazardCategory,
            description:  data.description ?? "",
            latitude:     data.latitude ?? 0,
            longitude:    data.longitude ?? 0,
            createdAt:    data.createdAt ?? Date.now(),
            verified:     data.verified ?? false,
            upvotes:      data.upvotes ?? 0,
            waterDepth:   data.waterDepth,
            imageUrl:     data.imageUrl,
            radiusMeters: data.radiusMeters ?? 80,
          };
        });
        // Sync into local hazardStore
        hazardStore.mergeFromFirestore(remote);
        // Also call admin callback if provided
        callback?.(remote);
      },
      () => {
        // Firestore error (offline/rules) — silently ignore
      }
    );
    return unsub;
  } catch {
    return () => {};
  }
}

// ── Toggle verified ────────────────────────────────────────────────────────
export async function toggleVerificationInFirestore(id: string, currentVerified: boolean) {
  try {
    await updateDoc(doc(db, COL, id), { verified: !currentVerified });
  } catch {}
}

// ── Upvote ────────────────────────────────────────────────────────────────
export async function upvoteInFirestore(id: string, newCount: number) {
  try {
    await updateDoc(doc(db, COL, id), { upvotes: newCount });
  } catch {}
}

// ── Delete ────────────────────────────────────────────────────────────────
export async function deleteReportFromFirestore(id: string) {
  try {
    await deleteDoc(doc(db, COL, id));
  } catch {}
}

// Legacy aliases so nothing breaks
export const toggleVerifyFirestore    = toggleVerificationInFirestore;
export const upvoteFirestore          = upvoteInFirestore;
export const deleteFirestore          = deleteReportFromFirestore;
