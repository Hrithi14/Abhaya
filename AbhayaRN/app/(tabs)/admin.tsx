import React, { useState } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  Switch, StyleSheet, Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useHazardReports } from "../../src/hooks/useHazardReports";
import { hazardStore, HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI, HazardReport } from "../../src/services/hazardStore";
import { C } from "../../src/theme/colors";
import { formatDistanceToNow } from "date-fns";

function timeAgo(ts: number) {
  try { return formatDistanceToNow(new Date(ts), { addSuffix: true }); }
  catch { return "recently"; }
}

function ReportAdminCard({ report }: { report: HazardReport }) {
  return (
    <View style={s.rCard}>
      <View style={s.rTop}>
        <Text style={s.rEmoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
        <View style={{ flex: 1 }}>
          <Text style={s.rTitle}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
          <Text style={s.rMeta}>
            {report.latitude.toFixed(3)}, {report.longitude.toFixed(3)} · {timeAgo(report.createdAt)}
          </Text>
        </View>
        <TouchableOpacity
          onPress={() => hazardStore.toggleVerification(report.id)}
          style={[s.verifyBtn, report.verified && s.verifyBtnActive]}
        >
          <Text style={[s.verifyBtnText, report.verified && { color: "#16A34A" }]}>
            {report.verified ? "✓ VERIFIED" : "Verify"}
          </Text>
        </TouchableOpacity>
      </View>

      <Text style={s.rDesc} numberOfLines={2}>{report.description}</Text>

      <View style={s.rBottom}>
        <Text style={s.rMeta}>👍 {report.upvotes} upvotes · ID: {report.id}</Text>
        <TouchableOpacity
          onPress={() =>
            Alert.alert("Delete Report?", "This will remove the report from the live map.", [
              { text: "Cancel", style: "cancel" },
              { text: "Delete", style: "destructive", onPress: () => hazardStore.deleteReport(report.id) },
            ])
          }
        >
          <Text style={s.deleteText}>🗑 Delete</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

export default function AdminScreen() {
  const reports = useHazardReports();

  const [offlineCaching, setOfflineCaching] = useState(true);
  const [autoSiren, setAutoSiren]           = useState(true);
  const [highAccGps, setHighAccGps]         = useState(true);

  const totalReports  = reports.length;
  const totalVerified = reports.filter((r) => r.verified).length;
  const totalUpvotes  = reports.reduce((sum, r) => sum + r.upvotes, 0);

  return (
    <SafeAreaView style={s.screen}>
      {/* ── Header ── */}
      <View style={s.header}>
        <View style={s.headerIconWrap}>
          <Ionicons name="shield-checkmark" size={20} color="#0F172A" />
        </View>
        <View>
          <Text style={s.headerTitle}>ADMIN & OPERATIONAL CONSOLE</Text>
          <Text style={s.headerSub}>ABHAYA Disaster Coordination · Mangaluru Node</Text>
        </View>
      </View>

      <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

        {/* ── Profile Card ── */}
        <View style={s.profileCard}>
          <View style={s.profileIcon}>
            <Ionicons name="person" size={24} color="#fff" />
          </View>
          <View style={{ flex: 1, marginLeft: 12 }}>
            <Text style={s.profileName}>Volunteer Field Responder</Text>
            <Text style={s.profileMeta}>Badge ID: KA-19-MNG-402 · Auth: Verified</Text>
          </View>
          <View style={s.activeBadge}><Text style={s.activeBadgeText}>ACTIVE</Text></View>
        </View>

        {/* ── Stats Row ── */}
        <View style={s.statsRow}>
          <View style={[s.statCard, { borderTopColor: C.orange }]}>
            <Text style={[s.statNum, { color: C.orange }]}>{totalReports}</Text>
            <Text style={s.statLabel}>Reports{"\n"}Active</Text>
          </View>
          <View style={[s.statCard, { borderTopColor: "#16A34A" }]}>
            <Text style={[s.statNum, { color: "#16A34A" }]}>{totalVerified}</Text>
            <Text style={s.statLabel}>Verified{"\n"}Reports</Text>
          </View>
          <View style={[s.statCard, { borderTopColor: "#0284C7" }]}>
            <Text style={[s.statNum, { color: "#0284C7" }]}>{totalUpvotes}</Text>
            <Text style={s.statLabel}>Total{"\n"}Upvotes</Text>
          </View>
        </View>

        {/* ── System Settings ── */}
        <Text style={s.sectionTitle}>BASIC SYSTEM SETTINGS</Text>
        <View style={s.settingsCard}>
          <SettingRow
            title="Offline OSM Map Caching"
            sub="Pre-download tiles for low-connectivity zones"
            value={offlineCaching}
            onChange={setOfflineCaching}
          />
          <View style={s.divider} />
          <SettingRow
            title="Severe Flash Flood Alerts"
            sub="Audible alert when within 500 m of rising flood"
            value={autoSiren}
            onChange={setAutoSiren}
          />
          <View style={s.divider} />
          <SettingRow
            title="Continuous Real-Time GPS"
            sub="Update position continuously during movement"
            value={highAccGps}
            onChange={setHighAccGps}
          />
        </View>

        {/* ── Manage Reports ── */}
        <Text style={s.sectionTitle}>MANAGE & VERIFY REPORTS</Text>

        {reports.length === 0 ? (
          <View style={s.emptyBox}>
            <Text style={s.emptyText}>No reports to manage.</Text>
          </View>
        ) : (
          reports.map((r) => <ReportAdminCard key={r.id} report={r} />)
        )}

        <View style={{ height: 80 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

function SettingRow({
  title, sub, value, onChange,
}: {
  title: string; sub: string; value: boolean; onChange: (v: boolean) => void;
}) {
  return (
    <View style={s.settingRow}>
      <View style={{ flex: 1, marginRight: 12 }}>
        <Text style={s.settingTitle}>{title}</Text>
        <Text style={s.settingSub}>{sub}</Text>
      </View>
      <Switch
        value={value}
        onValueChange={onChange}
        trackColor={{ true: `${C.orange}66`, false: "#E2E8F0" }}
        thumbColor={value ? C.orange : "#94A3B8"}
      />
    </View>
  );
}

const s = StyleSheet.create({
  screen:      { flex: 1, backgroundColor: C.bg },
  scroll:      { paddingHorizontal: 16, paddingTop: 12, paddingBottom: 80 },

  // Header
  header:         { backgroundColor: "#0F172A", flexDirection: "row", alignItems: "center", padding: 16, gap: 10 },
  headerIconWrap: { width: 38, height: 38, borderRadius: 19, backgroundColor: "#38BDF8", alignItems: "center", justifyContent: "center" },
  headerTitle:    { color: "#fff", fontWeight: "900", fontSize: 13, letterSpacing: 0.3 },
  headerSub:      { color: "rgba(255,255,255,0.55)", fontSize: 10, marginTop: 1 },

  // Profile
  profileCard:  { backgroundColor: "#fff", borderRadius: 12, padding: 14, flexDirection: "row", alignItems: "center", marginBottom: 12, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  profileIcon:  { width: 46, height: 46, borderRadius: 23, backgroundColor: "#1E293B", alignItems: "center", justifyContent: "center" },
  profileName:  { color: "#0F172A", fontWeight: "900", fontSize: 14 },
  profileMeta:  { color: C.textSecondary, fontSize: 12, marginTop: 1 },
  activeBadge:  { backgroundColor: "#DCFCE7", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  activeBadgeText:{ color: "#16A34A", fontSize: 10, fontWeight: "700" },

  // Stats
  statsRow:    { flexDirection: "row", gap: 10, marginBottom: 16 },
  statCard:    { flex: 1, backgroundColor: "#fff", borderRadius: 10, padding: 12, alignItems: "center", borderTopWidth: 3, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  statNum:     { fontSize: 22, fontWeight: "900" },
  statLabel:   { color: C.textSecondary, fontSize: 10, fontWeight: "600", textAlign: "center", marginTop: 2 },

  // Section title
  sectionTitle:{ color: "#334155", fontSize: 11, fontWeight: "800", letterSpacing: 0.8, marginBottom: 10 },

  // Settings
  settingsCard: { backgroundColor: "#fff", borderRadius: 12, paddingHorizontal: 14, marginBottom: 16, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  settingRow:   { flexDirection: "row", alignItems: "center", paddingVertical: 14 },
  settingTitle: { color: "#0F172A", fontWeight: "700", fontSize: 13 },
  settingSub:   { color: C.textSecondary, fontSize: 11, marginTop: 2 },
  divider:      { height: 1, backgroundColor: C.divider },

  // Report admin card
  rCard:   { backgroundColor: "#fff", borderRadius: 12, padding: 12, marginBottom: 10, borderWidth: 1, borderColor: C.divider, elevation: 1 },
  rTop:    { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 6 },
  rEmoji:  { fontSize: 18 },
  rTitle:  { color: "#0F172A", fontWeight: "700", fontSize: 13 },
  rMeta:   { color: C.textSecondary, fontSize: 11, marginTop: 1 },
  rDesc:   { color: C.textSecondary, fontSize: 12, marginBottom: 8, lineHeight: 17 },
  rBottom: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  verifyBtn:     { backgroundColor: "#FEF3C7", borderRadius: 6, paddingHorizontal: 8, paddingVertical: 4 },
  verifyBtnActive:{ backgroundColor: "#DCFCE7" },
  verifyBtnText: { color: "#B45309", fontSize: 11, fontWeight: "700" },
  deleteText:    { color: C.emergencyRed, fontSize: 11, fontWeight: "700" },

  // Empty
  emptyBox:  { backgroundColor: "#fff", borderRadius: 12, padding: 24, alignItems: "center", borderWidth: 1, borderColor: C.divider },
  emptyText: { color: C.textSecondary, fontSize: 14 },
});
