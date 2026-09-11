import React, { useState } from "react";
import {
  View, Text, ScrollView, TouchableOpacity, TextInput,
  ActivityIndicator, Image, StyleSheet, Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import { useLocation } from "../../src/hooks/useLocation";
import { hazardStore, HazardCategory, HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI } from "../../src/services/hazardStore";
import { C } from "../../src/theme/colors";

const CATEGORIES: HazardCategory[] = [
  "FLOOD_WATER", "WATERLOGGING", "FALLEN_TREE", "ROADBLOCK",
  "POTHOLE", "OPEN_WIRE", "LANDSLIDE", "FIRE", "MEDICAL_EMERGENCY",
];

function categoryColor(cat: HazardCategory): string {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#0284C7";
  if (cat === "FIRE" || cat === "MEDICAL_EMERGENCY") return "#DC2626";
  if (cat === "OPEN_WIRE") return "#7C3AED";
  return C.orange;
}

export default function ReportScreen() {
  const { location, isLoading } = useLocation();

  const [selectedCategory, setSelectedCategory] = useState<HazardCategory>("FLOOD_WATER");
  const [description, setDescription] = useState("");
  const [waterDepth, setWaterDepth] = useState("");
  const [photoUri, setPhotoUri] = useState<string | undefined>();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const currentLat = location?.available ? location.latitude  : 12.9141;
  const currentLon = location?.available ? location.longitude : 74.856;

  const takePhoto = async () => {
    const { status } = await ImagePicker.requestCameraPermissionsAsync();
    if (status !== "granted") { Alert.alert("Permission needed", "Camera permission is required to capture a photo."); return; }
    const res = await ImagePicker.launchCameraAsync({ quality: 0.7, allowsEditing: false });
    if (!res.canceled) setPhotoUri(res.assets[0].uri);
  };

  const pickPhoto = async () => {
    const res = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.Images, quality: 0.7 });
    if (!res.canceled) setPhotoUri(res.assets[0].uri);
  };

  const handleSubmit = async () => {
    if (!description.trim()) { setErrorMsg("Please enter a description of the hazard."); return; }
    setErrorMsg("");
    setIsSubmitting(true);

    hazardStore.addReport({
      category: selectedCategory,
      description: description.trim(),
      latitude: currentLat,
      longitude: currentLon,
      waterDepth: waterDepth.trim() || undefined,
      imageUrl: photoUri,
    });

    setIsSubmitting(false);
    setSubmitted(true);
    setDescription("");
    setWaterDepth("");
    setPhotoUri(undefined);
  };

  const handleReset = () => setSubmitted(false);

  // ── Success screen ────────────────────────────────────────────────────
  if (submitted) {
    return (
      <SafeAreaView style={s.screen}>
        <View style={s.successWrap}>
          <Text style={{ fontSize: 60 }}>✅</Text>
          <Text style={s.successTitle}>Report Published!</Text>
          <Text style={s.successSub}>
            Your hazard report is now live on the community map.{"\n"}
            Other citizens and responders can see it instantly.
          </Text>
          <View style={s.successCard}>
            <Row label="Category"   value={`${HAZARD_CATEGORY_EMOJI[selectedCategory]} ${HAZARD_CATEGORY_LABELS[selectedCategory]}`} />
            <Row label="GPS"        value={`${currentLat.toFixed(5)}, ${currentLon.toFixed(5)}`} />
            <Row label="Accuracy"   value={location?.available ? `±${Math.round(location.accuracy)} m` : "GPS unavailable"} />
            <Row label="Timestamp"  value={new Date().toLocaleString("en-IN")} />
          </View>
          <TouchableOpacity onPress={handleReset} style={s.newReportBtn}>
            <Text style={s.newReportTxt}>+ SUBMIT ANOTHER REPORT</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  const needsDepth = selectedCategory === "FLOOD_WATER" || selectedCategory === "WATERLOGGING";

  return (
    <SafeAreaView style={s.screen}>
      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* ── Header ── */}
        <View style={s.headRow}>
          <View style={s.headIcon}>
            <Ionicons name="warning" size={22} color="#fff" />
          </View>
          <View>
            <Text style={s.headTitle}>REPORT A HAZARD</Text>
            <Text style={s.headSub}>Alert responders and citizens instantly</Text>
          </View>
        </View>

        {/* ── GPS Lock Card ── */}
        <View style={[s.gpsCard, { borderLeftColor: location?.available ? "#16A34A" : C.emergencyRed }]}>
          <View style={s.gpsRow}>
            <Ionicons name="location" size={18} color={location?.available ? "#16A34A" : C.emergencyRed} />
            <View style={{ flex: 1, marginLeft: 8 }}>
              <Text style={s.gpsLabel}>AUTOMATIC GPS LOCK</Text>
              {isLoading ? (
                <Text style={{ color: C.textSecondary, fontSize: 12 }}>Acquiring GPS…</Text>
              ) : location?.available ? (
                <>
                  <Text style={s.gpsCoords}>{currentLat.toFixed(5)}, {currentLon.toFixed(5)}</Text>
                  <Text style={s.gpsAccuracy}>Accuracy: ±{Math.round(location.accuracy)} m</Text>
                </>
              ) : (
                <Text style={{ color: C.emergencyRed, fontSize: 12 }}>GPS unavailable — enable location</Text>
              )}
            </View>
            <View style={[s.gpsBadge, { backgroundColor: location?.available ? "#DCFCE7" : "#FEE2E2" }]}>
              <Text style={[s.gpsBadgeText, { color: location?.available ? "#16A34A" : C.emergencyRed }]}>
                {location?.available ? "LOCKED" : "NO GPS"}
              </Text>
            </View>
          </View>
        </View>

        {/* ── Step 1: Photo ── */}
        <Text style={s.stepLabel}>STEP 1 · CAPTURE LIVE PHOTO (OPTIONAL)</Text>
        {photoUri ? (
          <View style={s.photoContainer}>
            <Image source={{ uri: photoUri }} style={s.photoImg} resizeMode="cover" />
            <TouchableOpacity onPress={() => setPhotoUri(undefined)} style={s.removePhoto}>
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "700" }}>✕ Remove Photo</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View style={s.photoRow}>
            <TouchableOpacity onPress={takePhoto} style={s.photoBtnPrimary}>
              <Ionicons name="camera" size={18} color="#fff" />
              <Text style={s.photoBtnPrimaryText}>📷  CAPTURE PHOTO</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={pickPhoto} style={s.photoBtnSecondary}>
              <Ionicons name="images" size={18} color={C.orange} />
              <Text style={s.photoBtnSecondaryText}>Gallery</Text>
            </TouchableOpacity>
          </View>
        )}

        {/* ── Step 2: Hazard Type ── */}
        <Text style={s.stepLabel}>STEP 2 · SELECT HAZARD TYPE</Text>
        <View style={s.catGrid}>
          {CATEGORIES.map((cat) => {
            const selected = cat === selectedCategory;
            const color = categoryColor(cat);
            return (
              <TouchableOpacity
                key={cat}
                onPress={() => setSelectedCategory(cat)}
                style={[
                  s.catChip,
                  selected && { backgroundColor: color, borderColor: color },
                ]}
                activeOpacity={0.8}
              >
                <Text style={{ fontSize: 14 }}>{HAZARD_CATEGORY_EMOJI[cat]}</Text>
                <Text style={[s.catChipText, selected && { color: "#fff" }]}>
                  {HAZARD_CATEGORY_LABELS[cat]}
                </Text>
                {selected && (
                  <View style={s.catDot} />
                )}
              </TouchableOpacity>
            );
          })}
        </View>

        {/* ── Step 3: Water Depth (conditional) ── */}
        {needsDepth && (
          <>
            <Text style={s.stepLabel}>STEP 3 · WATER DEPTH ESTIMATE</Text>
            <View style={s.depthRow}>
              {["Ankle Deep", "Knee Deep", "Waist Deep", "Chest Deep", "Above Chest"].map((d) => (
                <TouchableOpacity
                  key={d}
                  onPress={() => setWaterDepth(waterDepth === d ? "" : d)}
                  style={[s.depthChip, waterDepth === d && { backgroundColor: "#0284C7", borderColor: "#0284C7" }]}
                >
                  <Text style={[s.depthChipText, waterDepth === d && { color: "#fff" }]}>
                    {d.split(" ")[0]}
                  </Text>
                  <Text style={[s.depthChipSub, waterDepth === d && { color: "rgba(255,255,255,0.8)" }]}>
                    {d.split(" ").slice(1).join(" ")}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
            {/* Free text fallback */}
            <TextInput
              style={s.depthInput}
              placeholder="Or type depth (e.g. 2.5 ft / 75 cm)"
              placeholderTextColor={C.textDisabled}
              value={waterDepth}
              onChangeText={setWaterDepth}
            />
          </>
        )}

        {/* ── Description ── */}
        <Text style={s.stepLabel}>{needsDepth ? "STEP 4" : "STEP 3"} · DESCRIPTION</Text>
        <TextInput
          style={s.descInput}
          placeholder="e.g. Waist-deep flooding on service road, power transformer sparking…"
          placeholderTextColor={C.textDisabled}
          value={description}
          onChangeText={(t) => { setDescription(t); setErrorMsg(""); }}
          multiline
          numberOfLines={4}
          textAlignVertical="top"
        />
        {errorMsg ? <Text style={s.errorText}>{errorMsg}</Text> : null}

        {/* ── Auto-attached info ── */}
        <View style={s.autoInfo}>
          <Text style={s.autoInfoText}>
            📡 AUTO-ATTACHED DATA · GPS ({currentLat.toFixed(4)}, {currentLon.toFixed(4)}) · TIMESTAMP · CATEGORY
          </Text>
        </View>

        {/* ── Submit ── */}
        <TouchableOpacity
          onPress={handleSubmit}
          disabled={isSubmitting}
          style={[s.submitBtn, isSubmitting && { opacity: 0.7 }]}
          activeOpacity={0.85}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <>
              <Ionicons name="send" size={18} color="#fff" />
              <Text style={s.submitText}>  SUBMIT HAZARD REPORT</Text>
            </>
          )}
        </TouchableOpacity>

        <View style={{ height: 24 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 8 }}>
      <Text style={{ color: C.textSecondary, fontSize: 13, flex: 0.35 }}>{label}</Text>
      <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "600", flex: 0.65 }}>{value}</Text>
    </View>
  );
}

const s = StyleSheet.create({
  screen:       { flex: 1, backgroundColor: C.bg },
  scroll:       { padding: 16, paddingBottom: 40 },

  // Header
  headRow:      { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 16 },
  headIcon:     { width: 40, height: 40, borderRadius: 10, backgroundColor: C.orange, alignItems: "center", justifyContent: "center" },
  headTitle:    { color: C.textPrimary, fontSize: 18, fontWeight: "900" },
  headSub:      { color: C.textSecondary, fontSize: 12, marginTop: 1 },

  // GPS
  gpsCard:      { backgroundColor: "#fff", borderRadius: 12, padding: 12, marginBottom: 16, borderLeftWidth: 4, borderWidth: 1, borderColor: C.divider },
  gpsRow:       { flexDirection: "row", alignItems: "center" },
  gpsLabel:     { color: "#0369A1", fontSize: 10, fontWeight: "800", letterSpacing: 0.5 },
  gpsCoords:    { color: C.textPrimary, fontSize: 13, fontWeight: "700" },
  gpsAccuracy:  { color: C.textSecondary, fontSize: 11, marginTop: 1 },
  gpsBadge:     { borderRadius: 4, paddingHorizontal: 6, paddingVertical: 2 },
  gpsBadgeText: { fontSize: 9, fontWeight: "700" },

  // Steps
  stepLabel:    { color: "#1E293B", fontSize: 11, fontWeight: "800", letterSpacing: 0.8, marginBottom: 10, marginTop: 16 },

  // Photo
  photoContainer:{ borderRadius: 12, overflow: "hidden", marginBottom: 4 },
  photoImg:     { width: "100%", height: 200 },
  removePhoto:  { backgroundColor: C.emergencyRed, padding: 10, alignItems: "center" },
  photoRow:     { flexDirection: "row", gap: 10, marginBottom: 4 },
  photoBtnPrimary:   { flex: 2, backgroundColor: "#16A34A", borderRadius: 12, padding: 14, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6 },
  photoBtnPrimaryText:{ color: "#fff", fontWeight: "700", fontSize: 14 },
  photoBtnSecondary: { flex: 1, backgroundColor: C.surface, borderRadius: 12, padding: 14, alignItems: "center", justifyContent: "center", borderWidth: 1.5, borderColor: C.orange },
  photoBtnSecondaryText:{ color: C.orange, fontWeight: "700", fontSize: 13, marginTop: 2 },

  // Category grid
  catGrid:      { flexDirection: "row", flexWrap: "wrap", gap: 8, marginBottom: 4 },
  catChip:      { flexDirection: "row", alignItems: "center", gap: 5, backgroundColor: "#fff", borderRadius: 8, paddingHorizontal: 10, paddingVertical: 8, borderWidth: 1.5, borderColor: C.divider },
  catChipText:  { color: C.textPrimary, fontSize: 11, fontWeight: "600" },
  catDot:       { width: 6, height: 6, borderRadius: 3, backgroundColor: "#fff" },

  // Water depth
  depthRow:     { flexDirection: "row", flexWrap: "wrap", gap: 8, marginBottom: 8 },
  depthChip:    { backgroundColor: "#fff", borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1.5, borderColor: "#0284C7", alignItems: "center" },
  depthChipText:{ color: "#0284C7", fontSize: 12, fontWeight: "700" },
  depthChipSub: { color: "#0284C7", fontSize: 10 },
  depthInput:   { backgroundColor: "#fff", borderRadius: 10, borderWidth: 1.5, borderColor: C.divider, paddingHorizontal: 14, paddingVertical: 10, color: C.textPrimary, fontSize: 13, marginBottom: 4 },

  // Description
  descInput:    { backgroundColor: "#fff", borderRadius: 10, borderWidth: 1.5, borderColor: C.divider, paddingHorizontal: 14, paddingVertical: 12, color: C.textPrimary, fontSize: 13, minHeight: 100 },
  errorText:    { color: C.emergencyRed, fontSize: 12, fontWeight: "700", marginTop: 4 },

  // Auto info
  autoInfo:     { backgroundColor: "#F0F9FF", borderRadius: 8, padding: 10, marginTop: 12, borderWidth: 1, borderColor: "#BAE6FD" },
  autoInfoText: { color: "#0369A1", fontSize: 10, fontWeight: "600" },

  // Submit
  submitBtn:    { backgroundColor: "#16A34A", borderRadius: 12, padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "center", marginTop: 16 },
  submitText:   { color: "#fff", fontWeight: "900", fontSize: 15, letterSpacing: 0.5 },

  // Success
  successWrap:  { flex: 1, padding: 24, alignItems: "center", justifyContent: "center" },
  successTitle: { color: "#16A34A", fontSize: 24, fontWeight: "900", marginTop: 12 },
  successSub:   { color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 8, lineHeight: 20, marginBottom: 20 },
  successCard:  { backgroundColor: "#fff", borderRadius: 14, padding: 20, width: "100%", borderWidth: 1.5, borderColor: C.divider, marginBottom: 20 },
  newReportBtn: { backgroundColor: C.orange, borderRadius: 12, padding: 16, width: "100%", alignItems: "center" },
  newReportTxt: { color: "#fff", fontWeight: "700", fontSize: 14 },
});
