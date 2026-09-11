/**
 * Profile screen — shows user info, activity stats, my reports, sign out.
 * Replaces the old Admin screen.
 */
import React, { useState, useEffect } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  StyleSheet, Alert, Switch,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import {
  subscribeToFirestoreReports,
  toggleVerificationInFirestore,
  deleteReportFromFirestore,
  FirestoreHazardReport,
} from "../../src/services/firestoreReports";
import { HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI } from "../../src/services/hazardStore";
import { useAuth } from "../../src/context/AuthContext";
import { C } from "../../src/theme/colors";
import { formatDistanceToNow } from "date-fns";

function timeAgo(ts: number) {
  try { return formatDistanceToNow(new Date(ts), { addSuffix: true }); }
  catch { return "recently"; }
}

export default function ProfileScreen() {
  const router                    = useRouter();
  const { user, isGuest, logout } = useAuth();
  const [reports, setReports]     = useState<FirestoreHazardReport[]>([]);
  const [alertsOn, setAlertsOn]   = useState(true);
  const [gpsOn, setGpsOn]         = useState(true);

  useEffect(() => {
    const unsub = subscribeToFirestoreReports(setReports);
    return unsub;
  }, []);

  const myReports     = reports.length;
  const verifiedCount = reports.filter((r) => r.verified).length;
  const upvotesTotal  = reports.reduce((s, r) => s + r.upvotes, 0);

  const displayName = isGuest
    ? "Guest User"
    : typeof user === "object"
      ? (user.displayName ?? user.email?.split("@")[0] ?? "User")
      : "User";

  const displayEmail = isGuest
    ? "Not signed in"
    : typeof user === "object"
      ? (user.email ?? "")
      : "";

  const initials = displayName
    .split(" ")
    .map((w) => w[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

  const handleLogout = () => {
    Alert.alert("Sign Out", "Are you sure you want to sign out?", [
      { text: "Cancel", style: "cancel" },
      {
        text: "Sign Out", style: "destructive",
        onPress: async () => {
          await logout();
          router.replace("/login");
        },
      },
    ]);
  };

  return (
    <SafeAreaView style={s.screen}>

      {/* Header */}
      <View style={s.header}>
        <Text style={s.headerTitle}>MY PROFILE</Text>
        <Text style={s.headerSub}>ABHAYA Emergency Portal</Text>
      </View>

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* Avatar + name card */}
        <View style={s.profileCard}>
          <View style={s.avatar}>
            <Text style={s.avatarText}>{initials || "👤"}</Text>
          </View>
          <View style={s.profileInfo}>
            <Text style={s.profileName}>{displayName}</Text>
            <Text style={s.profileEmail}>{displayEmail}</Text>
            <View style={isGuest ? s.guestBadge : s.verifiedBadge}>
              <Text style={isGuest ? s.guestBadgeText : s.verifiedBadgeText}>
                {isGuest ? "GUEST" : "✓ VERIFIED USER"}
              </Text>
            </View>
          </View>
        </View>

        {/* Stats */}
        <Text style={s.section}>ACTIVITY</Text>
        <View style={s.statsRow}>
          {[
            { num: myReports,     label: "Reports\nSubmitted", color: C.orange },
            { num: verifiedCount, label: "Reports\nVerified",  color: "#16A34A" },
            { num: upvotesTotal,  label: "Total\nUpvotes",     color: "#0284C7" },
          ].map((item) => (
            <View key={item.label} style={[s.statCard, { borderTopColor: item.color }]}>
              <Text style={[s.statNum, { color: item.color }]}>{item.num}</Text>
              <Text style={s.statLabel}>{item.label}</Text>
            </View>
          ))}
        </View>

        {/* Quick links */}
        <Text style={s.section}>QUICK ACCESS</Text>
        <View style={s.quickCard}>
          <QuickRow
            icon="list-outline"
            label="My Emergency Requests"
            onPress={() => router.push("/my-requests")}
          />
          <View style={s.rowDivider} />
          <QuickRow
            icon="map-outline"
            label="Live Hazard Map"
            onPress={() => router.push("/(tabs)/live-map" as any)}
          />
          <View style={s.rowDivider} />
          <QuickRow
            icon="walk-outline"
            label="Evacuate to Safety"
            onPress={() => router.push("/evacuate")}
          />
        </View>

        {/* Settings */}
        <Text style={s.section}>SETTINGS</Text>
        <View style={s.settingsCard}>
          <View style={s.settingRow}>
            <Ionicons name="notifications-outline" size={20} color={C.orange} style={{ marginRight: 10 }} />
            <View style={{ flex: 1 }}>
              <Text style={s.settingLabel}>Flood Alerts</Text>
              <Text style={s.settingSub}>Get notified when hazards are near you</Text>
            </View>
            <Switch
              value={alertsOn}
              onValueChange={setAlertsOn}
              trackColor={{ true: `${C.orange}66`, false: "#E2E8F0" }}
              thumbColor={alertsOn ? C.orange : "#94A3B8"}
            />
          </View>
          <View style={s.rowDivider} />
          <View style={s.settingRow}>
            <Ionicons name="location-outline" size={20} color={C.orange} style={{ marginRight: 10 }} />
            <View style={{ flex: 1 }}>
              <Text style={s.settingLabel}>Continuous GPS</Text>
              <Text style={s.settingSub}>Keep location updating in real time</Text>
            </View>
            <Switch
              value={gpsOn}
              onValueChange={setGpsOn}
              trackColor={{ true: `${C.orange}66`, false: "#E2E8F0" }}
              thumbColor={gpsOn ? C.orange : "#94A3B8"}
            />
          </View>
        </View>

        {/* My submitted reports */}
        {reports.length > 0 && (
          <>
            <Text style={s.section}>MY HAZARD REPORTS</Text>
            {reports.slice(0, 5).map((r) => (
              <View key={r.id} style={s.reportCard}>
                <Text style={s.reportEmoji}>{HAZARD_CATEGORY_EMOJI[r.category]}</Text>
                <View style={{ flex: 1 }}>
                  <Text style={s.reportTitle}>{HAZARD_CATEGORY_LABELS[r.category]}</Text>
                  <Text style={s.reportMeta} numberOfLines={1}>{r.description}</Text>
                  <Text style={s.reportTime}>{timeAgo(r.createdAt)}</Text>
                </View>
                {r.verified
                  ? <View style={s.vBadge}><Text style={s.vText}>✓</Text></View>
                  : <View style={s.uvBadge}><Text style={s.uvText}>⚠</Text></View>}
              </View>
            ))}
          </>
        )}

        {/* Sign out / Sign in */}
        <View style={{ marginTop: 24 }}>
          {isGuest ? (
            <TouchableOpacity
              onPress={() => router.replace("/login")}
              style={s.signInBtn}
            >
              <Ionicons name="log-in-outline" size={18} color="#fff" />
              <Text style={s.signInText}>  Sign In / Register</Text>
            </TouchableOpacity>
          ) : (
            <TouchableOpacity onPress={handleLogout} style={s.signOutBtn}>
              <Ionicons name="log-out-outline" size={18} color={C.emergencyRed} />
              <Text style={s.signOutText}>  Sign Out</Text>
            </TouchableOpacity>
          )}
        </View>

        <View style={{ height: 40 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

function QuickRow({
  icon, label, onPress,
}: { icon: any; label: string; onPress: () => void }) {
  return (
    <TouchableOpacity style={s.quickRow} onPress={onPress} activeOpacity={0.7}>
      <Ionicons name={icon} size={20} color={C.orange} style={{ marginRight: 12 }} />
      <Text style={s.quickLabel}>{label}</Text>
      <Ionicons name="chevron-forward" size={16} color={C.textSecondary} />
    </TouchableOpacity>
  );
}

const s = StyleSheet.create({
  screen:     { flex: 1, backgroundColor: C.bg },
  scroll:     { paddingHorizontal: 16, paddingTop: 12, paddingBottom: 40 },

  header:     { backgroundColor: "#0F172A", padding: 16 },
  headerTitle:{ color: "#fff", fontSize: 16, fontWeight: "900", letterSpacing: 0.5 },
  headerSub:  { color: "rgba(255,255,255,0.5)", fontSize: 11, marginTop: 2 },

  // Profile card
  profileCard:  { backgroundColor: "#fff", borderRadius: 14, padding: 18, flexDirection: "row", alignItems: "center", marginBottom: 16, borderWidth: 1, borderColor: C.divider, elevation: 2, gap: 14 },
  avatar:       { width: 60, height: 60, borderRadius: 30, backgroundColor: C.orange, alignItems: "center", justifyContent: "center" },
  avatarText:   { color: "#fff", fontSize: 22, fontWeight: "900" },
  profileInfo:  { flex: 1 },
  profileName:  { color: "#0F172A", fontWeight: "900", fontSize: 16 },
  profileEmail: { color: C.textSecondary, fontSize: 12, marginTop: 2 },
  verifiedBadge:{ backgroundColor: "#DCFCE7", paddingHorizontal: 8, paddingVertical: 2, borderRadius: 5, alignSelf: "flex-start", marginTop: 6 },
  verifiedBadgeText:{ color: "#16A34A", fontSize: 10, fontWeight: "700" },
  guestBadge:   { backgroundColor: "#F1F5F9", paddingHorizontal: 8, paddingVertical: 2, borderRadius: 5, alignSelf: "flex-start", marginTop: 6 },
  guestBadgeText:{ color: "#64748B", fontSize: 10, fontWeight: "700" },

  section:    { color: "#334155", fontSize: 11, fontWeight: "800", letterSpacing: 0.8, marginBottom: 10, marginTop: 4 },

  // Stats
  statsRow:   { flexDirection: "row", gap: 10, marginBottom: 16 },
  statCard:   { flex: 1, backgroundColor: "#fff", borderRadius: 10, padding: 12, alignItems: "center", borderTopWidth: 3, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  statNum:    { fontSize: 22, fontWeight: "900" },
  statLabel:  { color: C.textSecondary, fontSize: 10, fontWeight: "600", textAlign: "center", marginTop: 2 },

  // Quick access
  quickCard:  { backgroundColor: "#fff", borderRadius: 12, borderWidth: 1, borderColor: C.divider, marginBottom: 16, elevation: 1 },
  quickRow:   { flexDirection: "row", alignItems: "center", padding: 14 },
  quickLabel: { flex: 1, color: "#0F172A", fontSize: 14, fontWeight: "600" },
  rowDivider: { height: 1, backgroundColor: C.divider, marginHorizontal: 14 },

  // Settings
  settingsCard:{ backgroundColor: "#fff", borderRadius: 12, paddingHorizontal: 14, marginBottom: 16, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  settingRow: { flexDirection: "row", alignItems: "center", paddingVertical: 14 },
  settingLabel:{ color: "#0F172A", fontWeight: "700", fontSize: 13 },
  settingSub: { color: C.textSecondary, fontSize: 11, marginTop: 1 },

  // Report cards
  reportCard: { backgroundColor: "#fff", borderRadius: 10, padding: 12, marginBottom: 8, flexDirection: "row", alignItems: "center", gap: 10, borderWidth: 1, borderColor: C.divider },
  reportEmoji:{ fontSize: 22 },
  reportTitle:{ color: "#0F172A", fontWeight: "700", fontSize: 13 },
  reportMeta: { color: C.textSecondary, fontSize: 11, marginTop: 2 },
  reportTime: { color: C.textDisabled, fontSize: 10, marginTop: 2 },
  vBadge:     { backgroundColor: "#DCFCE7", borderRadius: 4, paddingHorizontal: 6, paddingVertical: 3 },
  vText:      { color: "#16A34A", fontSize: 11, fontWeight: "700" },
  uvBadge:    { backgroundColor: "#FEF3C7", borderRadius: 4, paddingHorizontal: 6, paddingVertical: 3 },
  uvText:     { color: "#B45309", fontSize: 11, fontWeight: "700" },

  // Buttons
  signInBtn:  { backgroundColor: C.orange, borderRadius: 12, padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "center" },
  signInText: { color: "#fff", fontWeight: "700", fontSize: 15 },
  signOutBtn: { backgroundColor: "#FEE2E2", borderRadius: 12, padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "center", borderWidth: 1, borderColor: "#FECACA" },
  signOutText:{ color: C.emergencyRed, fontWeight: "700", fontSize: 15 },
});
