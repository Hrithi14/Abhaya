/**
 * Emergency Portal — main emergency screen.
 * Features: CALL 112, 4 emergency type cards, SOS signal, Evacuate to Safety.
 */
import React, { useState, useEffect } from "react";
import {
  View, Text, TouchableOpacity, ScrollView,
  Modal, Linking, StyleSheet, ActivityIndicator,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { useLocation } from "../../src/hooks/useLocation";
import ConfirmModal from "../../src/components/ConfirmModal";
import { saveEmergency, seedDemoData } from "../../src/services/storage";
import { calculateSosPriority } from "../../src/services/priorityCalculator";
import { generateEmergencyId } from "../../src/services/idGenerator";
import type { EmergencyRequest } from "../../src/types/emergency";
import { C } from "../../src/theme/colors";

const USER_ID = "user-demo-001";

const EMERGENCY_TYPES = [
  { emoji: "🌊", title: "Trapped by\nWater",  sub: "Flood rescue",   route: "/water-rescue", color: "#1565C0" },
  { emoji: "🏥", title: "Medical\nEmergency", sub: "First aid",      route: "/medical",      color: "#D32F2F" },
  { emoji: "🚤", title: "Boat\nRescue",       sub: "Need a boat",    route: "/boat-rescue",  color: "#0284C7" },
  { emoji: "🏠", title: "Shelter\nRequest",   sub: "Find shelter",   route: "/shelter",      color: "#B45309" },
];

export default function EmergencyScreen() {
  const router = useRouter();
  const { location, isLoading } = useLocation();
  const [showCallDialog, setShowCallDialog] = useState(false);
  const [sosResult, setSosResult]           = useState<EmergencyRequest | null>(null);
  const [sosSending, setSosSending]         = useState(false);

  useEffect(() => { seedDemoData(USER_ID); }, []);

  const handleSOS = async () => {
    setSosSending(true);
    const id  = generateEmergencyId();
    const req: EmergencyRequest = {
      emergencyRequestId: id,
      userId: USER_ID,
      emergencyType: "SOS",
      latitude:   location?.latitude  ?? 0,
      longitude:  location?.longitude ?? 0,
      gpsAccuracy: location?.accuracy ?? 0,
      timestamp:  Date.now(),
      numberOfPeople: 1,
      children: false, elderly: false, disabled: false,
      peopleTrapped: false, injured: false, medicalRequired: false,
      personUnconscious: false, breathingProblem: false,
      severeBleeding: false, pregnancyRelated: false,
      description: "SOS signal sent from emergency portal",
      contactNumber: "",
      priority: calculateSosPriority(),
      status: "ACTIVE",
      escalationRequired: false,
      isDemoData: false,
    };
    await saveEmergency(req);
    setSosSending(false);
    setSosResult(req);
  };

  return (
    <SafeAreaView style={s.screen}>

      {/* Header */}
      <View style={s.header}>
        <View style={s.headerLeft}>
          <View style={s.redDot}>
            <Text style={{ color: "#fff", fontSize: 16, fontWeight: "900" }}>✱</Text>
          </View>
          <View>
            <Text style={s.headerTitle}>EMERGENCY PORTAL</Text>
            <Text style={s.headerSub}>PBRLM Disaster Relief · Mangaluru</Text>
          </View>
        </View>
        {isLoading ? (
          <ActivityIndicator color="#fff" size="small" />
        ) : location?.available ? (
          <View style={s.gpsBadge}><Text style={s.gpsBadgeText}>● GPS LIVE</Text></View>
        ) : (
          <View style={[s.gpsBadge, { backgroundColor: "#7F1D1D" }]}>
            <Text style={s.gpsBadgeText}>NO GPS</Text>
          </View>
        )}
      </View>

      <ScrollView
        contentContainerStyle={s.scroll}
        showsVerticalScrollIndicator={false}
      >

        {/* Live GPS card */}
        {location?.available && (
          <View style={s.gpsCard}>
            <Ionicons name="location" size={14} color={C.actionGreen} />
            <View style={{ marginLeft: 8 }}>
              <Text style={s.gpsCoords}>
                {location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}
              </Text>
              <Text style={s.gpsAcc}>Accuracy ±{Math.round(location.accuracy)} m</Text>
            </View>
            <View style={s.liveTag}><Text style={s.liveTagText}>LIVE</Text></View>
          </View>
        )}

        {/* CALL 112 */}
        <TouchableOpacity
          onPress={() => setShowCallDialog(true)}
          style={s.call112}
          activeOpacity={0.85}
        >
          <Ionicons name="call" size={22} color="#fff" />
          <View style={{ marginLeft: 12 }}>
            <Text style={s.call112Title}>CALL 112</Text>
            <Text style={s.call112Sub}>Police · Ambulance · Fire · Flood Rescue</Text>
          </View>
          <Ionicons name="chevron-forward" size={20} color="rgba(255,255,255,0.7)" style={{ marginLeft: "auto" }} />
        </TouchableOpacity>

        {/* Emergency type grid */}
        <Text style={s.sectionLabel}>SELECT EMERGENCY TYPE</Text>
        <View style={s.grid}>
          {EMERGENCY_TYPES.map((item) => (
            <TouchableOpacity
              key={item.route}
              onPress={() => router.push(item.route as any)}
              style={[s.typeCard, { borderTopColor: item.color }]}
              activeOpacity={0.8}
            >
              <Text style={s.typeEmoji}>{item.emoji}</Text>
              <Text style={s.typeTitle}>{item.title}</Text>
              <Text style={s.typeSub}>{item.sub}</Text>
              <View style={[s.typeArrow, { backgroundColor: item.color }]}>
                <Ionicons name="arrow-forward" size={12} color="#fff" />
              </View>
            </TouchableOpacity>
          ))}
        </View>

        {/* SOS button */}
        <Text style={s.sectionLabel}>QUICK ACTIONS</Text>
        <TouchableOpacity
          onPress={handleSOS}
          disabled={sosSending}
          style={[s.sosBtn, sosSending && { opacity: 0.7 }]}
          activeOpacity={0.85}
        >
          {sosSending ? (
            <ActivityIndicator color="#fff" size="small" />
          ) : (
            <Ionicons name="radio" size={22} color="#fff" />
          )}
          <View style={{ marginLeft: 12 }}>
            <Text style={s.sosBtnTitle}>🚨  SEND SOS SIGNAL</Text>
            <Text style={s.sosBtnSub}>
              {location?.available
                ? `Broadcasting GPS: ${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}`
                : "Enable GPS for accurate rescue location"}
            </Text>
          </View>
        </TouchableOpacity>

        {/* Evacuate button */}
        <TouchableOpacity
          onPress={() => router.push("/evacuate")}
          style={s.evacuateBtn}
          activeOpacity={0.85}
        >
          <Ionicons name="walk" size={22} color="#fff" />
          <View style={{ marginLeft: 12 }}>
            <Text style={s.evacuateBtnTitle}>🏃  EVACUATE TO SAFETY</Text>
            <Text style={s.evacuateBtnSub}>
              Shortest safe route to nearest shelter — avoids flood zones
            </Text>
          </View>
          <Ionicons name="chevron-forward" size={20} color="rgba(255,255,255,0.7)" style={{ marginLeft: "auto" }} />
        </TouchableOpacity>

        <View style={{ height: 32 }} />
      </ScrollView>

      {/* Call 112 confirm modal */}
      <ConfirmModal
        visible={showCallDialog}
        title="Call Emergency Number 112?"
        message="This will open your phone dialer with 112. Tap Call to connect to emergency services immediately."
        confirmText="CALL 112 NOW"
        confirmColor={C.emergencyRed}
        onConfirm={() => { setShowCallDialog(false); Linking.openURL("tel:112"); }}
        onCancel={() => setShowCallDialog(false)}
      />

      {/* SOS sent modal */}
      <Modal visible={!!sosResult} transparent animationType="fade">
        <View style={s.modalOverlay}>
          <View style={s.modalCard}>
            <Text style={{ fontSize: 48, textAlign: "center" }}>🚨</Text>
            <Text style={s.modalTitle}>SOS SIGNAL SENT</Text>
            <View style={s.modalRow}>
              <Text style={s.modalLabel}>Emergency ID</Text>
              <Text style={s.modalVal}>{sosResult?.emergencyRequestId}</Text>
            </View>
            <View style={s.modalRow}>
              <Text style={s.modalLabel}>Location</Text>
              <Text style={s.modalVal}>
                {sosResult?.latitude !== 0
                  ? `${sosResult?.latitude.toFixed(5)}, ${sosResult?.longitude.toFixed(5)}`
                  : "GPS unavailable"}
              </Text>
            </View>
            <View style={s.modalRow}>
              <Text style={s.modalLabel}>Priority</Text>
              <Text style={[s.modalVal, { color: C.emergencyRed, fontWeight: "900" }]}>CRITICAL</Text>
            </View>
            <Text style={s.modalNote}>
              Call 112 directly to speak with emergency services.
            </Text>
            <View style={{ flexDirection: "row", gap: 10, marginTop: 16 }}>
              <TouchableOpacity
                onPress={() => { setSosResult(null); Linking.openURL("tel:112"); }}
                style={[s.modalBtn, { backgroundColor: C.emergencyRed }]}
              >
                <Text style={s.modalBtnText}>📞 CALL 112</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setSosResult(null)}
                style={[s.modalBtn, { backgroundColor: C.actionGreen }]}
              >
                <Text style={s.modalBtnText}>✓ OK</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:  { flex: 1, backgroundColor: C.bg },
  scroll:  { padding: 16, paddingBottom: 32 },

  // Header
  header:       { backgroundColor: "#1A1A2E", paddingHorizontal: 16, paddingVertical: 14, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  headerLeft:   { flexDirection: "row", alignItems: "center", gap: 12, flex: 1 },
  redDot:       { width: 40, height: 40, borderRadius: 20, backgroundColor: C.emergencyRed, alignItems: "center", justifyContent: "center" },
  headerTitle:  { color: "#fff", fontSize: 15, fontWeight: "900", letterSpacing: 0.5 },
  headerSub:    { color: "rgba(255,255,255,0.55)", fontSize: 11, marginTop: 1 },
  gpsBadge:     { backgroundColor: "#14532D", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  gpsBadgeText: { color: "#86EFAC", fontSize: 10, fontWeight: "700" },

  // GPS card
  gpsCard:    { backgroundColor: "#F0FDF4", borderRadius: 10, padding: 12, marginBottom: 14, borderWidth: 1, borderColor: "#A7F3D0", flexDirection: "row", alignItems: "center" },
  gpsCoords:  { color: "#166534", fontSize: 13, fontWeight: "700" },
  gpsAcc:     { color: "#4ADE80", fontSize: 11, marginTop: 1 },
  liveTag:    { backgroundColor: "#16A34A", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 5, marginLeft: "auto" },
  liveTagText:{ color: "#fff", fontSize: 9, fontWeight: "700" },

  // CALL 112
  call112:      { backgroundColor: C.emergencyRed, borderRadius: 14, padding: 16, flexDirection: "row", alignItems: "center", marginBottom: 20, elevation: 6, shadowColor: C.emergencyRed, shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.35, shadowRadius: 8 },
  call112Title: { color: "#fff", fontSize: 18, fontWeight: "900", letterSpacing: 1 },
  call112Sub:   { color: "rgba(255,255,255,0.8)", fontSize: 11, marginTop: 2 },

  // Section label
  sectionLabel: { color: "#64748B", fontSize: 11, fontWeight: "800", letterSpacing: 1, marginBottom: 12, textTransform: "uppercase" },

  // Emergency type grid
  grid:       { flexDirection: "row", flexWrap: "wrap", gap: 12, marginBottom: 24 },
  typeCard:   { width: "47%", backgroundColor: "#fff", borderRadius: 14, padding: 16, borderTopWidth: 4, borderWidth: 1, borderColor: C.divider, elevation: 2, shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 6 },
  typeEmoji:  { fontSize: 34, marginBottom: 8 },
  typeTitle:  { color: "#0F172A", fontSize: 14, fontWeight: "800", lineHeight: 20, marginBottom: 2 },
  typeSub:    { color: C.textSecondary, fontSize: 11, marginBottom: 10 },
  typeArrow:  { width: 24, height: 24, borderRadius: 12, alignItems: "center", justifyContent: "center", alignSelf: "flex-end" },

  // SOS
  sosBtn:      { backgroundColor: "#7F1D1D", borderRadius: 14, padding: 16, flexDirection: "row", alignItems: "center", marginBottom: 12, borderWidth: 1.5, borderColor: C.emergencyRed, elevation: 4 },
  sosBtnTitle: { color: "#fff", fontSize: 16, fontWeight: "900" },
  sosBtnSub:   { color: "rgba(255,255,255,0.7)", fontSize: 11, marginTop: 2, maxWidth: 260 },

  // Evacuate
  evacuateBtn:      { backgroundColor: "#1C3A1C", borderRadius: 14, padding: 16, flexDirection: "row", alignItems: "center", marginBottom: 12, borderWidth: 1.5, borderColor: "#16A34A", elevation: 4 },
  evacuateBtnTitle: { color: "#fff", fontSize: 16, fontWeight: "900" },
  evacuateBtnSub:   { color: "rgba(255,255,255,0.7)", fontSize: 11, marginTop: 2, maxWidth: 240 },

  // Modal
  modalOverlay: { flex: 1, backgroundColor: "rgba(0,0,0,0.65)", justifyContent: "center", alignItems: "center", padding: 20 },
  modalCard:    { backgroundColor: "#fff", borderRadius: 18, padding: 24, width: "100%", elevation: 10 },
  modalTitle:   { fontSize: 20, fontWeight: "900", textAlign: "center", color: C.emergencyRed, marginTop: 8, marginBottom: 16 },
  modalRow:     { flexDirection: "row", justifyContent: "space-between", marginBottom: 10 },
  modalLabel:   { color: C.textSecondary, fontSize: 13 },
  modalVal:     { color: C.textPrimary, fontSize: 13, fontWeight: "600", flex: 1, textAlign: "right" },
  modalNote:    { color: C.textSecondary, fontSize: 12, textAlign: "center", marginTop: 8, lineHeight: 18 },
  modalBtn:     { flex: 1, borderRadius: 10, padding: 14, alignItems: "center" },
  modalBtnText: { color: "#fff", fontWeight: "700", fontSize: 14 },
});
