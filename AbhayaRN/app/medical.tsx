import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator, Image, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import ToggleChip from "../src/components/ToggleChip";
import SuccessScreen from "../src/components/SuccessScreen";
import ImageValidatorModal, { ValidatorState } from "../src/components/ImageValidatorModal";
import { useLocation } from "../src/hooks/useLocation";
import { saveEmergency } from "../src/services/storage";
import { validateHazardImage } from "../src/services/imageValidator";
import { calculateMedicalPriority } from "../src/services/priorityCalculator";
import { generateEmergencyId } from "../src/services/idGenerator";
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
  const [loading, setLoading]       = useState(false);
  const [submitted, setSubmitted]   = useState<EmergencyRequest | null>(null);

  // ── Image validator state ──────────────────────────────────────────────
  const [validatorState, setValidatorState] = useState<ValidatorState>("idle");
  const [pendingUri, setPendingUri]         = useState<string | undefined>();
  const [validLabel, setValidLabel]         = useState("");
  const [invalidReason, setInvalidReason]   = useState("");

  const runValidation = async (uri: string) => {
    setPendingUri(uri);
    setValidatorState("checking");
    const result = await validateHazardImage(uri);
    if (result.valid) {
      setValidLabel(result.label);
      setValidatorState("valid");
    } else {
      setInvalidReason(result.reason);
      setValidatorState("invalid");
    }
  };

  const takePhoto = async () => {
    const res = await ImagePicker.launchCameraAsync({ quality: 0.7 });
    if (!res.canceled) await runValidation(res.assets[0].uri);
  };
  const pickPhoto = async () => {
    const res = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.Images, quality: 0.7 });
    if (!res.canceled) await runValidation(res.assets[0].uri);
  };

  const handleSubmit = async () => {
    setLoading(true);
    const priority = calculateMedicalPriority({
      medicalType: medType,
      personUnconscious: unconscious, breathingProblem: breathing,
      severeBleeding: bleeding, pregnancyRelated: medType === "PREGNANCY",
      injured: medType === "INJURY", numberOfPeople: 1,
      children: false, elderly: false, disabled: false,
    });
    const req: EmergencyRequest = {
      emergencyRequestId: generateEmergencyId(), userId: USER_ID,
      emergencyType: "MEDICAL",
      latitude: location?.latitude ?? 0, longitude: location?.longitude ?? 0,
      gpsAccuracy: location?.accuracy ?? 0, timestamp: Date.now(),
      numberOfPeople: 1, children: false, elderly: false, disabled: false,
      peopleTrapped: false, injured: medType === "INJURY", medicalRequired: true,
      medicalEmergencyType: medType,
      personUnconscious: unconscious, breathingProblem: breathing,
      severeBleeding: bleeding, pregnancyRelated: medType === "PREGNANCY",
      description: `Medical emergency: ${medType}`, contactNumber: "",
      photoUri, priority, status: "ACTIVE", escalationRequired: false, isDemoData: false,
    };
    await saveEmergency(req);
    setLoading(false);
    setSubmitted(req);
  };

  if (submitted) return (
    <SuccessScreen
      request={submitted}
      onViewRequests={() => router.push("/(tabs)/index")}
      onGoHome={() => router.push("/(tabs)/index")}
    />
  );

  return (
    <SafeAreaView style={s.screen}>
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={24} color={C.textPrimary} />
        </TouchableOpacity>
        <Text style={s.topBarTitle}>🏥 Medical Emergency</Text>
      </View>

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* GPS badge */}
        {location?.available && (
          <View style={s.locationBadge}>
            <Text style={s.locationText}>
              📍 {location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}
            </Text>
          </View>
        )}

        {/* Photo */}
        <Text style={s.label}>📷 Take or Upload Photo</Text>
        {photoUri ? (
          <View style={s.photoContainer}>
            <Image source={{ uri: photoUri }} style={s.photo} resizeMode="cover" />
            <View style={s.aiBadge}>
              <Text style={s.aiBadgeText}>✅ AI VERIFIED · {validLabel}</Text>
            </View>
            <TouchableOpacity onPress={() => { setPhotoUri(undefined); setValidLabel(""); }} style={s.removePhoto}>
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "700" }}>✕ Remove</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View style={s.photoRow}>
            <TouchableOpacity onPress={takePhoto} style={[s.photoBtn, { backgroundColor: C.orange }]}>
              <Text style={s.photoBtnText}>📷 Live Camera</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={pickPhoto} style={[s.photoBtn, { backgroundColor: C.surface, borderWidth: 1.5, borderColor: C.orange }]}>
              <Text style={[s.photoBtnText, { color: C.orange }]}>🖼️ Gallery</Text>
            </TouchableOpacity>
          </View>
        )}

        {/* Medical type grid */}
        <Text style={s.label}>What type of emergency?</Text>
        <View style={s.medGrid}>
          {MED_TYPES.map((t) => (
            <TouchableOpacity
              key={t.key}
              onPress={() => setMedType(t.key)}
              style={[s.medCard, medType === t.key && { borderColor: C.orange, backgroundColor: C.orangeBg }]}
            >
              <Text style={{ fontSize: 24 }}>{t.emoji}</Text>
              <Text style={[s.medCardText, medType === t.key && { color: C.orange }]}>{t.label}</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* Critical conditions */}
        <Text style={s.label}>Critical Conditions</Text>
        <View style={s.row}>
          <ToggleChip label="Unconscious"     selected={unconscious} onToggle={setUnconscious} />
          <ToggleChip label="Not Breathing"   selected={breathing}   onToggle={setBreathing} />
          <ToggleChip label="Severe Bleeding" selected={bleeding}    onToggle={setBleeding} />
        </View>

        <TouchableOpacity onPress={handleSubmit} disabled={loading} style={s.submitBtn}>
          {loading
            ? <ActivityIndicator color="#fff" />
            : <Text style={s.submitText}>SEND MEDICAL ALERT</Text>
          }
        </TouchableOpacity>
      </ScrollView>

      {/* AI Validator Modal */}
      <ImageValidatorModal
        state={validatorState}
        imageUri={pendingUri}
        validLabel={validLabel}
        invalidReason={invalidReason}
        onAccept={() => { setPhotoUri(pendingUri); setValidatorState("idle"); }}
        onRetry={() => { setPendingUri(undefined); setValidatorState("idle"); }}
        onCancel={() => { setPendingUri(undefined); setValidatorState("idle"); }}
      />
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:        { flex: 1, backgroundColor: C.bg },
  topBar:        { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider },
  topBarTitle:   { color: C.emergencyRed, fontSize: 18, fontWeight: "700" },
  scroll:        { padding: 16 },
  locationBadge: { backgroundColor: "#F0FFF4", borderRadius: 8, padding: 10, marginBottom: 16, borderWidth: 1, borderColor: "#A7F3D0" },
  locationText:  { color: C.actionGreen, fontSize: 12, fontWeight: "600" },
  label:         { color: C.textSecondary, fontSize: 12, fontWeight: "700", marginBottom: 8, marginTop: 16, textTransform: "uppercase", letterSpacing: 0.5 },
  photoContainer:{ borderRadius: 12, overflow: "hidden", marginBottom: 4 },
  photo:         { width: "100%", height: 200 },
  aiBadge:       { backgroundColor: "#DCFCE7", padding: 8, alignItems: "center" },
  aiBadgeText:   { color: "#16A34A", fontSize: 11, fontWeight: "700" },
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
