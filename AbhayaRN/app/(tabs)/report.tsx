/**
 * Report screen — with:
 *  - Camera photo validated via Gemini Vision (rejects non-disaster photos)
 *  - Gallery photos accepted but marked unverified (no validation)
 *  - Photo uploaded to Firebase Storage
 *  - Report pushed to Firestore via hazardStore → firestoreReports
 */
import React, { useState } from "react";
import {
  View, Text, ScrollView, TouchableOpacity, TextInput,
  ActivityIndicator, Image, StyleSheet, Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import { useLocation } from "../../src/hooks/useLocation";
import {
  hazardStore, HazardCategory,
  HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI,
} from "../../src/services/hazardStore";
import { pushReportToFirestore } from "../../src/services/firestoreReports";
import { uploadPhoto } from "../../src/services/photoUpload";
import { validateDisasterPhoto } from "../../src/services/geminiValidate";
import { C } from "../../src/theme/colors";

const CATEGORIES: HazardCategory[] = [
  "FLOOD_WATER","WATERLOGGING","FALLEN_TREE","ROADBLOCK",
  "POTHOLE","OPEN_WIRE","LANDSLIDE","FIRE","MEDICAL_EMERGENCY",
];

function categoryColor(cat: HazardCategory): string {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#1565C0";
  if (cat === "FIRE" || cat === "MEDICAL_EMERGENCY")   return "#D32F2F";
  if (cat === "OPEN_WIRE")  return "#6A1B9A";
  return C.orange;
}

export default function ReportScreen() {
  const { location, isLoading } = useLocation();

  const [selectedCategory, setSelectedCategory] = useState<HazardCategory>("FLOOD_WATER");
  const [description, setDescription]           = useState("");
  const [waterDepth, setWaterDepth]             = useState("");
  const [photoUri, setPhotoUri]                 = useState<string | undefined>();
  const [photoSource, setPhotoSource]           = useState<"camera" | "gallery" | null>(null);
  const [validating, setValidating]             = useState(false);
  const [isSubmitting, setIsSubmitting]         = useState(false);
  const [submitted, setSubmitted]               = useState(false);
  const [errorMsg, setErrorMsg]                 = useState("");

  const currentLat = location?.available ? location.latitude  : 12.9141;
  const currentLon = location?.available ? location.longitude : 74.856;

  // Camera — validates via Gemini
  const takePhoto = async () => {
    const { status } = await ImagePicker.requestCameraPermissionsAsync();
    if (status !== "granted") {
      Alert.alert("Permission needed", "Camera permission is required.");
      return;
    }
    const res = await ImagePicker.launchCameraAsync({ quality: 0.7, allowsEditing: false });
    if (res.canceled) return;
    const uri = res.assets[0].uri;

    // Gemini validation
    setValidating(true);
    const check = await validateDisasterPhoto(uri);
    setValidating(false);

    if (!check.valid) {
      Alert.alert(
        "Photo not accepted",
        `This photo doesn't appear to show a disaster or hazard.\n\nReason: ${check.reason}\n\nPlease take a photo of the actual hazard.`
      );
      return;
    }
    setPhotoUri(uri);
    setPhotoSource("camera");
  };

  // Gallery — accepted without validation, marked unverified
  const pickPhoto = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== "granted") { Alert.alert("Permission needed", "Gallery access is required."); return; }
    const res = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.7,
    });
    if (!res.canceled) {
      setPhotoUri(res.assets[0].uri);
      setPhotoSource("gallery");
    }
  };

  const handleSubmit = async () => {
    if (!description.trim()) { setErrorMsg("Please enter a description."); return; }
    setErrorMsg("");
    setIsSubmitting(true);

    try {
      // Upload photo to Firebase Storage if present
      let imageUrl: string | undefined;
      if (photoUri) {
        imageUrl = await uploadPhoto(photoUri, "hazard");
      }

      // Add to local store first (instant UI update)
      const report = hazardStore.addReport({
        category: selectedCategory,
        description: description.trim(),
        latitude: currentLat,
        longitude: currentLon,
        waterDepth: waterDepth.trim() || undefined,
        imageUrl,
      });

      // Push to Firestore (real-time sync to all users)
      await pushReportToFirestore(report);

      setIsSubmitting(false);
      setSubmitted(true);
      setDescription("");
      setWaterDepth("");
      setPhotoUri(undefined);
      setPhotoSource(null);
    } catch (e) {
      setIsSubmitting(false);
      Alert.alert("Error", "Failed to submit report. Please try again.");
    }
  };

  if (submitted) {
    return (
      <SafeAreaView style={s.screen}>
        <View style={s.successWrap}>
          <Text style={{ fontSize: 60 }}>✅</Text>
          <Text style={s.successTitle}>Report Published!</Text>
          <Text style={s.successSub}>
            Your hazard report is now live on the community map in real-time.
          </Text>
          <TouchableOpacity onPress={() => setSubmitted(false)} style={s.newReportBtn}>
            <Text style={s.newReportTxt}>+ SUBMIT ANOTHER REPORT</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  const needsDepth = selectedCategory === "FLOOD_WATER" || selectedCategory === "WATERLOGGING";

  return (
    <SafeAreaView style={s.screen}>
      {validating && (
        <View style={s.validatingBanner}>
          <ActivityIndicator size="small" color="#fff" />
          <Text style={s.validatingText}>  Validating photo with AI…</Text>
        </View>
      )}

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* Header */}
        <View style={s.headRow}>
          <View style={s.headIcon}><Ionicons name="warning" size={22} color="#fff" /></View>
          <View>
            <Text style={s.headTitle}>REPORT A HAZARD</Text>
            <Text style={s.headSub}>Alert responders and citizens instantly</Text>
          </View>
        </View>

        {/* GPS */}
        <View style={[s.gpsCard, { borderLeftColor: location?.available ? "#16A34A" : C.emergencyRed }]}>
          <Ionicons name="location" size={16} color={location?.available ? "#16A34A" : C.emergencyRed} />
          <View style={{ flex: 1, marginLeft: 8 }}>
            <Text style={s.gpsLabel}>AUTOMATIC GPS LOCK</Text>
            {isLoading
              ? <Text style={{ color: C.textSecondary, fontSize: 12 }}>Acquiring GPS…</Text>
              : location?.available
                ? <Text style={s.gpsCoords}>{currentLat.toFixed(5)}, {currentLon.toFixed(5)}</Text>
                : <Text style={{ color: C.emergencyRed, fontSize: 12 }}>GPS unavailable</Text>}
          </View>
        </View>

        {/* Step 1: Photo */}
        <Text style={s.stepLabel}>STEP 1 · CAPTURE PHOTO (OPTIONAL)</Text>
        <Text style={s.photoNote}>
          📷 Camera photos are AI-validated (must show actual hazard){"\n"}
          🖼️ Gallery photos are accepted but marked as unverified
        </Text>

        {photoUri ? (
          <View style={s.photoContainer}>
            <Image source={{ uri: photoUri }} style={s.photoImg} resizeMode="cover" />
            {photoSource === "gallery" && (
              <View style={s.unverifiedBanner}>
                <Text style={s.unverifiedText}>⚠ Gallery photo — UNVERIFIED</Text>
              </View>
            )}
            <TouchableOpacity onPress={() => { setPhotoUri(undefined); setPhotoSource(null); }} style={s.removePhoto}>
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "700" }}>✕ Remove Photo</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <View style={s.photoRow}>
            <TouchableOpacity onPress={takePhoto} style={s.photoBtnPrimary}>
              <Ionicons name="camera" size={18} color="#fff" />
              <Text style={s.photoBtnPrimaryText}>📷  CAMERA</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={pickPhoto} style={s.photoBtnSecondary}>
              <Ionicons name="images" size={18} color={C.orange} />
              <Text style={s.photoBtnSecondaryText}>Gallery</Text>
            </TouchableOpacity>
          </View>
        )}

        {/* Step 2: Hazard type */}
        <Text style={s.stepLabel}>STEP 2 · SELECT HAZARD TYPE</Text>
        <View style={s.catGrid}>
          {CATEGORIES.map((cat) => {
            const selected = cat === selectedCategory;
            const color    = categoryColor(cat);
            return (
              <TouchableOpacity
                key={cat}
                onPress={() => setSelectedCategory(cat)}
                style={[s.catChip, selected && { backgroundColor: color, borderColor: color }]}
                activeOpacity={0.8}
              >
                <Text style={{ fontSize: 14 }}>{HAZARD_CATEGORY_EMOJI[cat]}</Text>
                <Text style={[s.catChipText, selected && { color: "#fff" }]}>
                  {HAZARD_CATEGORY_LABELS[cat]}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>

        {/* Step 3: Water depth (conditional) */}
        {needsDepth && (
          <>
            <Text style={s.stepLabel}>STEP 3 · WATER DEPTH</Text>
            <View style={s.depthRow}>
              {["Ankle Deep","Knee Deep","Waist Deep","Chest Deep","Above Chest"].map((d) => (
                <TouchableOpacity
                  key={d}
                  onPress={() => setWaterDepth(waterDepth === d ? "" : d)}
                  style={[s.depthChip, waterDepth === d && { backgroundColor: "#1565C0", borderColor: "#1565C0" }]}
                >
                  <Text style={[s.depthChipText, waterDepth === d && { color: "#fff" }]}>{d.split(" ")[0]}</Text>
                  <Text style={[s.depthChipSub, waterDepth === d && { color: "rgba(255,255,255,0.8)" }]}>{d.split(" ").slice(1).join(" ")}</Text>
                </TouchableOpacity>
              ))}
            </View>
            <TextInput
              style={s.depthInput}
              placeholder="Or type depth (e.g. 2.5 ft / 75 cm)"
              placeholderTextColor={C.textDisabled}
              value={waterDepth}
              onChangeText={setWaterDepth}
            />
          </>
        )}

        {/* Description */}
        <Text style={s.stepLabel}>{needsDepth ? "STEP 4" : "STEP 3"} · DESCRIPTION</Text>
        <TextInput
          style={s.descInput}
          placeholder="Describe the hazard situation…"
          placeholderTextColor={C.textDisabled}
          value={description}
          onChangeText={(t) => { setDescription(t); setErrorMsg(""); }}
          multiline
          numberOfLines={4}
          textAlignVertical="top"
        />
        {errorMsg ? <Text style={s.errorText}>{errorMsg}</Text> : null}

        {/* Submit */}
        <TouchableOpacity
          onPress={handleSubmit}
          disabled={isSubmitting || validating}
          style={[s.submitBtn, (isSubmitting || validating) && { opacity: 0.7 }]}
        >
          {isSubmitting
            ? <ActivityIndicator color="#fff" />
            : <><Ionicons name="send" size={18} color="#fff" /><Text style={s.submitText}>  SUBMIT HAZARD REPORT</Text></>}
        </TouchableOpacity>

        <View style={{ height: 24 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:       { flex: 1, backgroundColor: C.bg },
  scroll:       { padding: 16, paddingBottom: 40 },
  validatingBanner: { backgroundColor: "#1565C0", flexDirection: "row", alignItems: "center", padding: 10, justifyContent: "center" },
  validatingText:   { color: "#fff", fontSize: 13, fontWeight: "600" },
  headRow:      { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 16 },
  headIcon:     { width: 40, height: 40, borderRadius: 10, backgroundColor: C.orange, alignItems: "center", justifyContent: "center" },
  headTitle:    { color: C.textPrimary, fontSize: 18, fontWeight: "900" },
  headSub:      { color: C.textSecondary, fontSize: 12, marginTop: 1 },
  gpsCard:      { backgroundColor: "#fff", borderRadius: 10, padding: 12, marginBottom: 16, borderLeftWidth: 4, borderWidth: 1, borderColor: C.divider, flexDirection: "row", alignItems: "center" },
  gpsLabel:     { color: "#0369A1", fontSize: 10, fontWeight: "800", letterSpacing: 0.5 },
  gpsCoords:    { color: C.textPrimary, fontSize: 13, fontWeight: "700" },
  stepLabel:    { color: "#1E293B", fontSize: 11, fontWeight: "800", letterSpacing: 0.8, marginBottom: 8, marginTop: 16 },
  photoNote:    { color: C.textSecondary, fontSize: 11, lineHeight: 17, marginBottom: 10, backgroundColor: C.surface, borderRadius: 8, padding: 10 },
  photoContainer: { borderRadius: 12, overflow: "hidden", marginBottom: 4 },
  photoImg:     { width: "100%", height: 200 },
  unverifiedBanner: { backgroundColor: "#F59E0B", padding: 6, alignItems: "center" },
  unverifiedText:   { color: "#fff", fontSize: 11, fontWeight: "700" },
  removePhoto:  { backgroundColor: C.emergencyRed, padding: 10, alignItems: "center" },
  photoRow:     { flexDirection: "row", gap: 10, marginBottom: 4 },
  photoBtnPrimary:    { flex: 2, backgroundColor: "#16A34A", borderRadius: 12, padding: 14, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6 },
  photoBtnPrimaryText:{ color: "#fff", fontWeight: "700", fontSize: 14 },
  photoBtnSecondary:  { flex: 1, backgroundColor: C.surface, borderRadius: 12, padding: 14, alignItems: "center", justifyContent: "center", borderWidth: 1.5, borderColor: C.orange },
  photoBtnSecondaryText:{ color: C.orange, fontWeight: "700", fontSize: 13 },
  catGrid:      { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  catChip:      { flexDirection: "row", alignItems: "center", gap: 5, backgroundColor: "#fff", borderRadius: 8, paddingHorizontal: 10, paddingVertical: 8, borderWidth: 1.5, borderColor: C.divider },
  catChipText:  { color: C.textPrimary, fontSize: 11, fontWeight: "600" },
  depthRow:     { flexDirection: "row", flexWrap: "wrap", gap: 8, marginBottom: 8 },
  depthChip:    { backgroundColor: "#fff", borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1.5, borderColor: "#1565C0", alignItems: "center" },
  depthChipText:{ color: "#1565C0", fontSize: 12, fontWeight: "700" },
  depthChipSub: { color: "#1565C0", fontSize: 10 },
  depthInput:   { backgroundColor: "#fff", borderRadius: 10, borderWidth: 1.5, borderColor: C.divider, paddingHorizontal: 14, paddingVertical: 10, color: C.textPrimary, fontSize: 13, marginBottom: 4 },
  descInput:    { backgroundColor: "#fff", borderRadius: 10, borderWidth: 1.5, borderColor: C.divider, paddingHorizontal: 14, paddingVertical: 12, color: C.textPrimary, fontSize: 13, minHeight: 100 },
  errorText:    { color: C.emergencyRed, fontSize: 12, fontWeight: "700", marginTop: 4 },
  submitBtn:    { backgroundColor: "#16A34A", borderRadius: 12, padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "center", marginTop: 16 },
  submitText:   { color: "#fff", fontWeight: "900", fontSize: 15 },
  successWrap:  { flex: 1, padding: 24, alignItems: "center", justifyContent: "center" },
  successTitle: { color: "#16A34A", fontSize: 24, fontWeight: "900", marginTop: 12 },
  successSub:   { color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 8, lineHeight: 20, marginBottom: 20 },
  newReportBtn: { backgroundColor: C.orange, borderRadius: 12, padding: 16, width: "100%", alignItems: "center" },
  newReportTxt: { color: "#fff", fontWeight: "700", fontSize: 14 },
});
