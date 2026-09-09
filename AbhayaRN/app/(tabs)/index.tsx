import React, { useState, useEffect } from "react";
import {
  View, Text, TouchableOpacity, ScrollView,
  Modal, Linking, StyleSheet, ActivityIndicator,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { useLocation } from "../../src/hooks/useLocation";
import ConfirmModal from "../../src/components/ConfirmModal";
import { saveEmergency, seedDemoData } from "../../src/services/storage";
import { calculateSosPriority } from "../../src/services/priorityCalculator";
import { generateEmergencyId } from "../../src/services/idGenerator";
import type { EmergencyRequest } from "../../src/types/emergency";
import { C } from "../../src/theme/colors";

const USER_ID = "user-demo-001";

export default function EmergencyScreen() {
  const router = useRouter();
  const { location, isLoading } = useLocation();
  const [showCallDialog, setShowCallDialog] = useState(false);
  const [sosResult, setSosResult] = useState<EmergencyRequest | null>(null);

  useEffect(() => { seedDemoData(USER_ID); }, []);

  const handleSOS = async () => {
    const id = generateEmergencyId();
    const req: EmergencyRequest = {
      emergencyRequestId: id, userId: USER_ID,
      emergencyType: "SOS",
      latitude: location?.latitude ?? 0, longitude: location?.longitude ?? 0,
      gpsAccuracy: location?.accuracy ?? 0, timestamp: Date.now(),
      numberOfPeople: 1, children: false, elderly: false, disabled: false,
      peopleTrapped: false, injured: false, medicalRequired: false,
      personUnconscious: false, breathingProblem: false, severeBleeding: false, pregnancyRelated: false,
      description: "", contactNumber: "",
      priority: calculateSosPriority(), status: "ACTIVE",
      escalationRequired: false, isDemoData: false,
    };
    await saveEmergency(req);
    setSosResult(req);
  };

  return (
    <SafeAreaView style={s.screen}>
      {/* Header */}
      <View style={s.header}>
        <View style={s.headerLeft}>
          <View style={s.redDot}><Text style={{ color: "#fff", fontSize: 14, fontWeight: "900" }}>✱</Text></View>
          <View>
            <Text style={s.headerTitle}>EMERGENCY & DISASTER RELIEF</Text>
            <Text style={s.headerSub}>24/7 Rapid Help • PBRLM Safety System</Text>
          </View>
        </View>
        {/* GPS indicator */}
        {isLoading ? (
          <ActivityIndicator color="#fff" size="small" />
        ) : location?.available ? (
          <View style={s.gpsTag}><Text style={s.gpsTagText}>● GPS</Text></View>
        ) : (
          <View style={[s.gpsTag, { backgroundColor: C.emergencyRed }]}><Text style={s.gpsTagText}>NO GPS</Text></View>
        )}
      </View>

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* GPS Location Card */}
        {location?.available && (
          <View style={s.gpsCard}>
            <Text style={s.gpsLabel}>📍 Your Location Detected</Text>
            <Text style={s.gpsCoords}>{location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}</Text>
            <Text style={s.gpsAccuracy}>Accuracy: {Math.round(location.accuracy)} m</Text>
          </View>
        )}

        {/* CALL 112 Card */}
        <View style={s.callCard}>
          <Text style={s.callCardTitle}>NATIONAL EMERGENCY NUMBER</Text>
          <Text style={s.callCardSub}>Unified Police, Ambulance, Fire & Flood Rescue</Text>
          <TouchableOpacity
            onPress={() => setShowCallDialog(true)}
            style={s.call112Btn}
            activeOpacity={0.85}
          >
            <Text style={s.call112Text}>📞  CALL 112 NOW</Text>
          </TouchableOpacity>
        </View>

        {/* 4 Emergency Type Cards */}
        <Text style={s.sectionLabel}>SELECT EMERGENCY TYPE</Text>
        <View style={s.grid}>
          {[
            { emoji: "🌊", title: "Trapped by\nWater",    route: "/water-rescue" },
            { emoji: "🏥", title: "Medical\nEmergency",   route: "/medical" },
            { emoji: "🚤", title: "Boat\nRescue",         route: "/boat-rescue" },
            { emoji: "🏠", title: "Shelter\nRequest",     route: "/shelter" },
          ].map((item) => (
            <TouchableOpacity
              key={item.route}
              onPress={() => router.push(item.route as any)}
              style={s.emergencyCard}
              activeOpacity={0.85}
            >
              <Text style={{ fontSize: 32 }}>{item.emoji}</Text>
              <Text style={s.emergencyCardText}>{item.title}</Text>
              <Text style={s.tapHint}>Tap to report →</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* SOS + Evacuate */}
        <Text style={s.sectionLabel}>QUICK ACTIONS</Text>
        <TouchableOpacity onPress={handleSOS} style={s.sosBtn} activeOpacity={0.85}>
          <Text style={s.sosBtnText}>🚨  SEND SOS SIGNAL</Text>
          <Text style={s.sosBtnSub}>Sends your GPS location to emergency services</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => router.push("/evacuate")} style={s.evacuateBtn} activeOpacity={0.85}>
          <Text style={s.evacuateBtnText}>🏃  EVACUATE TO SAFETY</Text>
          <Text style={s.evacuateBtnSub}>Find nearest safe shelter route</Text>
        </TouchableOpacity>

        {/* My Requests shortcut */}
        <TouchableOpacity onPress={() => router.push("/(tabs)/my-requests")} style={s.myRequestsBtn}>
          <Text style={s.myRequestsBtnText}>📋  View My Active Emergencies</Text>
        </TouchableOpacity>

        <View style={{ height: 24 }} />
      </ScrollView>

      {/* Call 112 Confirmation */}
      <ConfirmModal
        visible={showCallDialog}
        title="Call Emergency Number 112?"
        message="This will open your phone dialer with 112. Tap Call to connect to emergency services."
        confirmText="CALL 112"
        confirmColor={C.emergencyRed}
        onConfirm={() => { setShowCallDialog(false); Linking.openURL("tel:112"); }}
        onCancel={() => setShowCallDialog(false)}
      />

      {/* SOS Success Modal */}
      <Modal visible={!!sosResult} transparent animationType="fade">
        <View style={s.modalOverlay}>
          <View style={s.modalCard}>
            <Text style={{ fontSize: 44, textAlign: "center" }}>✅</Text>
            <Text style={[s.modalTitle, { color: C.actionGreen }]}>SOS SENT</Text>
            <Text style={{ color: C.textSecondary, fontSize: 13, textAlign: "center", marginTop: 6 }}>
              Emergency ID: {sosResult?.emergencyRequestId}
            </Text>
            <Text style={{ color: C.textSecondary, fontSize: 12, textAlign: "center", marginTop: 8, lineHeight: 18 }}>
              Your location has been recorded. Use CALL 112 to speak to emergency services directly.
            </Text>
            <TouchableOpacity
              onPress={() => setSosResult(null)}
              style={{ backgroundColor: C.actionGreen, borderRadius: 10, padding: 14, marginTop: 16, alignItems: "center" }}
            >
              <Text style={{ color: "#fff", fontWeight: "700" }}>OK</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:  { flex: 1, backgroundColor: C.bg },
  scroll:  { padding: 16 },

  // Header
  header:     { backgroundColor: "#1A1A2E", padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  headerLeft: { flexDirection: "row", alignItems: "center", gap: 12, flex: 1 },
  redDot:     { width: 36, height: 36, borderRadius: 18, backgroundColor: C.emergencyRed, alignItems: "center", justifyContent: "center" },
  headerTitle:{ color: "#fff", fontSize: 14, fontWeight: "900", letterSpacing: 0.3 },
  headerSub:  { color: "rgba(255,255,255,0.6)", fontSize: 11, marginTop: 1 },
  gpsTag:     { backgroundColor: "#2E7D32", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  gpsTagText: { color: "#fff", fontSize: 10, fontWeight: "700" },

  // GPS Card
  gpsCard:    { backgroundColor: C.surface, borderRadius: 10, padding: 12, marginBottom: 14, borderLeftWidth: 4, borderLeftColor: C.actionGreen },
  gpsLabel:   { color: C.textSecondary, fontSize: 11, fontWeight: "600", marginBottom: 2 },
  gpsCoords:  { color: C.textPrimary, fontSize: 13, fontWeight: "700" },
  gpsAccuracy:{ color: C.textSecondary, fontSize: 11, marginTop: 2 },

  // Call 112 Card
  callCard:      { backgroundColor: C.white, borderRadius: 14, padding: 18, marginBottom: 16, borderWidth: 1, borderColor: C.divider, shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.08, shadowRadius: 8, elevation: 3 },
  callCardTitle: { color: C.emergencyRed, fontSize: 14, fontWeight: "900", textAlign: "center", letterSpacing: 0.5 },
  callCardSub:   { color: C.textSecondary, fontSize: 13, textAlign: "center", marginTop: 4, marginBottom: 14 },
  call112Btn:    { backgroundColor: C.emergencyRed, borderRadius: 12, padding: 18, alignItems: "center", shadowColor: C.emergencyRed, shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.35, shadowRadius: 8, elevation: 6 },
  call112Text:   { color: "#fff", fontSize: 18, fontWeight: "900", letterSpacing: 1 },

  // Section
  sectionLabel: { color: C.textSecondary, fontSize: 11, fontWeight: "700", letterSpacing: 1, marginBottom: 10, textTransform: "uppercase" },

  // Emergency Cards Grid
  grid:          { flexDirection: "row", flexWrap: "wrap", gap: 10, marginBottom: 20 },
  emergencyCard: { width: "47.5%", backgroundColor: C.white, borderRadius: 14, padding: 16, alignItems: "center", borderWidth: 1.5, borderColor: C.orange, shadowColor: C.orange, shadowOffset: { width: 0, height: 3 }, shadowOpacity: 0.15, shadowRadius: 6, elevation: 3 },
  emergencyCardText: { color: C.textPrimary, fontSize: 14, fontWeight: "700", textAlign: "center", marginTop: 8, lineHeight: 20 },
  tapHint:       { color: C.orange, fontSize: 11, marginTop: 6, fontWeight: "600" },

  // SOS
  sosBtn:    { backgroundColor: C.emergencyRed, borderRadius: 14, padding: 18, alignItems: "center", marginBottom: 12, shadowColor: C.emergencyRed, shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.3, shadowRadius: 8, elevation: 5 },
  sosBtnText:{ color: "#fff", fontSize: 17, fontWeight: "900", letterSpacing: 0.5 },
  sosBtnSub: { color: "rgba(255,255,255,0.8)", fontSize: 11, marginTop: 4 },

  // Evacuate
  evacuateBtn:    { backgroundColor: C.orange, borderRadius: 14, padding: 18, alignItems: "center", marginBottom: 12, shadowColor: C.orange, shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.3, shadowRadius: 8, elevation: 5 },
  evacuateBtnText:{ color: "#fff", fontSize: 17, fontWeight: "900", letterSpacing: 0.5 },
  evacuateBtnSub: { color: "rgba(255,255,255,0.85)", fontSize: 11, marginTop: 4 },

  // My Requests
  myRequestsBtn:    { backgroundColor: C.surface, borderRadius: 12, padding: 16, alignItems: "center", borderWidth: 1.5, borderColor: C.orange, marginBottom: 8 },
  myRequestsBtnText:{ color: C.orange, fontSize: 14, fontWeight: "700" },

  // Modal
  modalOverlay: { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "center", alignItems: "center", padding: 24 },
  modalCard:    { backgroundColor: C.white, borderRadius: 16, padding: 24, width: "100%", shadowColor: "#000", shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.15, shadowRadius: 20, elevation: 10 },
  modalTitle:   { fontSize: 20, fontWeight: "900", textAlign: "center", marginTop: 8 },
});
