import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import ToggleChip from "../src/components/ToggleChip";
import SuccessScreen from "../src/components/SuccessScreen";
import { useLocation } from "../src/hooks/useLocation";
import { saveEmergency } from "../src/services/storage";
import { calculateShelterPriority } from "../src/services/priorityCalculator";
import { generateEmergencyId } from "../src/services/idGenerator";
import type { EmergencyRequest } from "../src/types/emergency";
import { C } from "../src/theme/colors";

const USER_ID = "user-demo-001";

export default function ShelterScreen() {
  const router = useRouter();
  const { location } = useLocation();
  const [people, setPeople] = useState(1);
  const [children, setChildren] = useState(false);
  const [elderly, setElderly] = useState(false);
  const [disabled, setDisabled] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState<EmergencyRequest | null>(null);

  const handleSubmit = async () => {
    setLoading(true);
    const priority = calculateShelterPriority({ numberOfPeople: people, children, elderly, disabled });
    const req: EmergencyRequest = {
      emergencyRequestId: generateEmergencyId(), userId: USER_ID,
      emergencyType: "SHELTER",
      latitude: location?.latitude ?? 0, longitude: location?.longitude ?? 0,
      gpsAccuracy: location?.accuracy ?? 0, timestamp: Date.now(),
      numberOfPeople: people, children, elderly, disabled,
      peopleTrapped: false, injured: false, medicalRequired: false,
      personUnconscious: false, breathingProblem: false, severeBleeding: false, pregnancyRelated: false,
      description: `Shelter needed for ${people} people. Children: ${children}. Elderly: ${elderly}.`,
      contactNumber: "", priority, status: "ACTIVE", escalationRequired: false, isDemoData: false,
    };
    await saveEmergency(req);
    setLoading(false);
    setSubmitted(req);
  };

  if (submitted) return <SuccessScreen request={submitted} onViewRequests={() => router.replace("/")} onGoHome={() => router.replace("/")} />;

  return (
    <SafeAreaView style={s.screen}>
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()}><Ionicons name="arrow-back" size={24} color={C.textPrimary} /></TouchableOpacity>
        <Text style={s.topBarTitle}>🏠 Request Shelter</Text>
      </View>
      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>
        {location?.available && (
          <View style={s.locationBadge}><Text style={s.locationText}>📍 {location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}</Text></View>
        )}

        {!location?.available && (
          <View style={[s.locationBadge, { backgroundColor: "#FFF5F5", borderColor: C.emergencyRed }]}>
            <Text style={{ color: C.emergencyRed, fontSize: 12, fontWeight: "600" }}>⚠️ GPS not available — your location may not be accurate</Text>
          </View>
        )}

        <Text style={s.label}>👥 Number of People Needing Shelter</Text>
        <View style={s.stepper}>
          <TouchableOpacity onPress={() => people > 1 && setPeople(people - 1)} style={s.stepBtn}><Text style={s.stepBtnText}>−</Text></TouchableOpacity>
          <Text style={s.stepValue}>{people}</Text>
          <TouchableOpacity onPress={() => setPeople(people + 1)} style={s.stepBtn}><Text style={s.stepBtnText}>+</Text></TouchableOpacity>
        </View>

        <Text style={s.label}>Vulnerable People Present</Text>
        <View style={s.row}>
          <ToggleChip label="🧒 Children" selected={children} onToggle={setChildren} />
          <ToggleChip label="👴 Elderly" selected={elderly} onToggle={setElderly} />
          <ToggleChip label="♿ Disabled" selected={disabled} onToggle={setDisabled} />
        </View>

        {/* Info note */}
        <View style={s.infoBox}>
          <Text style={s.infoText}>ℹ️ Your GPS location will be shared with nearby relief centers to find you the nearest available shelter.</Text>
        </View>

        <TouchableOpacity onPress={handleSubmit} disabled={loading} style={s.submitBtn}>
          {loading ? <ActivityIndicator color="#fff" /> : <Text style={s.submitText}>REQUEST SHELTER NOW</Text>}
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: C.bg },
  topBar: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider },
  topBarTitle: { color: C.emergencyRed, fontSize: 18, fontWeight: "700" },
  scroll: { padding: 16 },
  locationBadge: { backgroundColor: "#F0FFF4", borderRadius: 8, padding: 10, marginBottom: 16, borderWidth: 1, borderColor: "#A7F3D0" },
  locationText: { color: C.actionGreen, fontSize: 12, fontWeight: "600" },
  label: { color: C.textSecondary, fontSize: 12, fontWeight: "700", marginBottom: 8, marginTop: 16, textTransform: "uppercase", letterSpacing: 0.5 },
  stepper: { flexDirection: "row", alignItems: "center", gap: 20 },
  stepBtn: { width: 44, height: 44, backgroundColor: C.surface, borderRadius: 8, borderWidth: 1.5, borderColor: C.orange, alignItems: "center", justifyContent: "center" },
  stepBtnText: { color: C.orange, fontSize: 22, fontWeight: "700" },
  stepValue: { color: C.textPrimary, fontSize: 26, fontWeight: "700", minWidth: 40, textAlign: "center" },
  row: { flexDirection: "row", gap: 8, flexWrap: "wrap" },
  infoBox: { backgroundColor: C.orangeBg, borderRadius: 10, padding: 12, marginTop: 20, borderWidth: 1, borderColor: C.orange },
  infoText: { color: C.orangeDark, fontSize: 13, lineHeight: 18 },
  submitBtn: { backgroundColor: C.orange, borderRadius: 14, padding: 18, alignItems: "center", marginTop: 24, marginBottom: 24 },
  submitText: { color: "#fff", fontWeight: "900", fontSize: 15, letterSpacing: 0.5 },
});
