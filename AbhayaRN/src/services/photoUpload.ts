/**
 * Upload a photo URI to Firebase Storage.
 * Returns the public download URL.
 */
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { storage } from "./firebase";

export async function uploadPhoto(
  localUri: string,
  folder: "hazard" | "emergency"
): Promise<string> {
  // Fetch the file as a blob
  const response = await fetch(localUri);
  const blob     = await response.blob();

  const filename  = `${folder}/${Date.now()}_${Math.random().toString(36).slice(2)}.jpg`;
  const storageRef = ref(storage, filename);

  await uploadBytes(storageRef, blob);
  const url = await getDownloadURL(storageRef);
  return url;
}
