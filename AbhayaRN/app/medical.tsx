import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator, Image, StyleSheet, Alert } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import ToggleChip from "../src/components/ToggleChip";
import SuccessScreen from "../src/components/SuccessScreen";
import { useLocation } from "../src/hooks/useLocation";
import { saveEmergency } from "../src/services/storage";
import { calculateMedicalPriority } from "../src/services/priorityCalculator";
import { generateEmergencyId } from "../src/services/idGenerator";
import { uploadPhoto } from "../src/services/photoUpload";
import { validateDisasterPhoto } from "../src/services/geminiValidate";
import type { EmergencyRequest, MedicalEmergencyType } from "../src/types/emergency";
import { C } from "../src/theme/colors";

const USER_ID = "user-demo-001";
type MedType = { key: MedicalEmergencyType; label: string; emoji: string };
const MED_TYPES: MedType[] = [
  { key: "INJURY",            label: "Injury",      emoji: "🩹" },
  { key: "UNCONSCIOUS",       label: "Unconscious", emoji: "😶" },
  { key: "BREATHING_PROBLEM", label: "Breathing",   emoji: "😮‍💨" },
  { key: "SEVERE_BLEEDING",   label: "Bleeding",    emoji: "🩸" },
  { key: "PREGNANCY",         label: "Pregnancy",   emoji: "🤰" },
  { key: "OTHER",             label: "Other",       emoji: "⚕️" },
];

export default function MedicalScreen() {
  const router = useRouter();
  const { location } = useLocation();
  const [medType, setMedType]       = useState<MedicalEmergencyType>("OTHER");
  const [unconscious, setUnconscious] = useState(false);
  const [breathing, setBreathing]   = useState(false);
  const [bleeding, setBleeding]     = useState(false);
  const [photoUri, setPhotoUri]     = useState<string | undefined>();
  const [photoSource, setPhotoSource] = useState<"camera"|"gallery"|null>(null);
  const [validating, setValidating] = useState(false);
  const [loading, setLoading]       = useState(false);
  const [submitted, setSubmitted]   = useState<EmergencyRequest | null>(null);

  const takePhoto = async () => {
    const { status } = await ImagePicker.requestCameraPermissionsAsync();
    if (status !== "granted") { Alert.alert("Permission needed", "Camera access is required."); return; }
    const res = await ImagePicker.launchCameraAsync({ quality: 0.7 });
    if (res.canceled) return;
    const uri = res.assets[0].uri;
    setValidating(true);
    const check = await validateDisasterPhoto(uri);
    setValidating(false);
    if (!check.valid) {
      Alert.alert("Photo not accepted", `This photo doesn't appear to show an emergency.\n\nReason: ${check.reason}`);
      return;
    }
    setPhotoUri(uri);
    setPhotoSource("camera");
  };

  const pickPhoto = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== "granted") { Alert.alert("Permission needed", "Gallery access is required."); return; }
    const res = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.Images, quality: 0.7 });
    if (!res.canceled) { setPhotoUri(res.assets[0].uri); setPhotoSource("gallery"); }
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      let photoUrl: string | undefined;
      if (photoUri) photoUrl = await uploadPhoto(photoUri, "emergency");

      const priority = calculateMedicalPriority({ medicalType: medType, personUnconscious: unconscious, breathingProblem: breathing, severeBleeding: bleeding, pregnancyRelated: medType === "PREGNANCY", injured: medType === "INJURY", numberOfPeople: 1, children: false, elderly: false, disabled: false });
      const req: EmergencyRequest = {
        emergencyRequestId: generateEmergencyId(), userId: USER_ID,
        emergencyType: "MEDICAL",
        latitude: location?.latitude ?? 0, longitude: location?.longitude ?? 0,
        gpsAccuracy: location?.accuracy ?? 0, timestamp: Date.now(),
        numberOfPeople: 1, children: false, elderly: false, disabled: false,
        peopleTrapped: false, injured: medType === "INJURY", medicalRequired: true,
        medicalEmergencyType: medType, personUnconscious: unconscious,
        breathingProblem: breathing, severeBleeding: bleeding, pregnancyRelated: medType === "PREGNANCY",
        description: `Medical: ${medType}`, contactNumber: "",
        photoUri: photoUrl, priority, status: "ACTIVE", escalationRequired: false, isDemoData: false,
      };
      await saveEmergency(req);
      setLoading(false);
      setSubmitted(req);
    } catch {
      setLoading(false);
      Alert.alert("Error", "Failed to submit. Please try again.");
    }
  };

  if (submitted) return <SuccessScreen request={submitted} onViewRequests={() => router.replace("/")} onGoHome={() => router.replace("/")} />;

  return (
    <SafeAreaView style={s.screen}>
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()}><Ionicons name="arrow-back" size={24} color={C.textPrimary} /></TouchableOpacity>
        <Text style={s.topBarTitle}>🏥 Medical Emergency</Text>
      </View>

      {validating && (
        <View style={s.validatingBar}>
          <ActivityIndicator size="small" color="#fff" />
          <Text style={s.validatingText}>  Validating photo with AI…</Text>
        </View>
      )}

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>
        {location?.available && (
          <View style={s.locationBadge}><Text style={s.locationText}>📍 {location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}</Text></View>
        )}

        <Text style={s.label}>📷 Take or Upload Photo</Text>
        {photoUri ? (
          <View style={s.photoContainer}>
            <Image source={{ uri: photoUri }} style={s.photo} resizeMode="cover" />
            {photoSource === "gallery" && (
              <View style={{ backgroundColor: "#F59E0B", padding: 5, alignItems: "center" }}>
                <Text style={{ color: "#fff", fontSize: 11, fontWeight: "700" }}>⚠ Gallery photo — UNVERIFIED</Text>
              </View>
            )}
            <TouchableOpacity onPress={() => { setPhotoUri(undefined); setPhotoSource(null); }} style={s.removePhoto}>
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "700" }}>✕ Remove</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View style={s.photoRow}>
            <TouchableOpacity onPress={takePhoto} style={[s.photoBtn, { backgroundColor: C.orange }]}>
              <Text style={s.photoBtnText}>📷 Camera</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={pickPhoto} style={[s.photoBtn, { backgroundColor: C.surface, borderWidth: 1.5, borderColor: C.orange }]}>
              <Text style={[s.photoBtnText, { color: C.orange }]}>🖼️ Gallery</Text>
            </TouchableOpacity>
          </View>
        )}

        <Text style={s.label}>What type of emergency?</Text>
        <View style={s.medGrid}>
          {MED_TYPES.map((t) => (
            <TouchableOpacity key={t.key} onPress={() => setMedType(t.key)} style={[s.medCard, medType === t.key && { borderColor: C.orange, backgroundColor: C.orangeBg }]}>
              <Text style={{ fontSize: 24 }}>{t.emoji}</Text>
              <Text style={[s.medCardText, medType === t.key && { color: C.orange }]}>{t.label}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <Text style={s.label}>Critical Conditions</Text>
        <View style={s.row}>
          <ToggleChip label="Unconscious"      selected={unconscious} onToggle={setUnconscious} />
          <ToggleChip label="Not Breathing"    selected={breathing}   onToggle={setBreathing} />
          <ToggleChip label="Severe Bleeding"  selected={bleeding}    onToggle={setBleeding} />
        </View>

        <TouchableOpacity onPress={handleSubmit} disabled={loading || validating} style={[s.submitBtn, (loading || validating) && { opacity: 0.6 }]}>
          {loading ? <ActivityIndicator color="#fff" /> : <Text style={s.submitText}>SEND MEDICAL ALERT</Text>}
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:        { flex: 1, backgroundColor: C.bg },
  topBar:        { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider },
  topBarTitle:   { color: C.emergencyRed, fontSize: 18, fontWeight: "700" },
  validatingBar: { backgroundColor: "#1565C0", flexDirection: "row", alignItems: "center", padding: 10, justifyContent: "center" },
  validatingText:{ color: "#fff", fontSize: 13, fontWeight: "600" },
  scroll:        { padding: 16 },
  locationBadge: { backgroundColor: "#F0FFF4", borderRadius: 8, padding: 10, marginBottom: 16, borderWidth: 1, borderColor: "#A7F3D0" },
  locationText:  { color: C.actionGreen, fontSize: 12, fontWeight: "600" },
  label:         { color: C.textSecondary, fontSize: 12, fontWeight: "700", marginBottom: 8, marginTop: 16, textTransform: "uppercase", letterSpacing: 0.5 },
  photoContainer:{ borderRadius: 12, overflow: "hidden", marginBottom: 4 },
  photo:         { width: "100%", height: 200 },
  removePhoto:   { backgroundColor: C.emergencyRed, padding: 8, alignItems: "center" },
  photoRow:      { flexDirection: "row", gap: 10 },
  photoBtn:      { flex: 1, padding: 16, borderRadius: 12, alignItems: "center" },
  photoBtnText:  { color: "#fff", fontWeight: "700", fontSize: 14 },
  medGrid:       { flexDirection: "row", flexWrap: "wrap", gap: 10 },
  medCard:       { width: "30%", backgroundColor: C.surface, borderRadius: 12, padding: 12, alignItems: "center", borderWidth: 1.5, borderColor: C.divider },
  medCardText:   { color: C.textPrimary, fontSize: 12, fontWeight: "600", marginTop: 4, textAlign: "center" },
  row:           { flexDirection: "row", gap: 8, flexWrap: "wrap" },
  submitBtn:     { backgroundColor: C.orange, borderRadius: 14, padding: 18, alignItems: "center", marginTop: 24, marginBottom: 24 },
  submitText:    { color: "#fff", fontWeight: "900", fontSize: 15, letterSpacing: 0.5 },
});
