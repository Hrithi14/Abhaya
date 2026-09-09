import React, { useState, useEffect, useRef } from "react";
import {
  View, Text, TextInput, ScrollView, TouchableOpacity,
  ActivityIndicator, Modal, StyleSheet, FlatList, Linking,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import OSMMap from "../../src/components/OSMMap";
import { useLocation } from "../../src/hooks/useLocation";
import { useHazardReports } from "../../src/hooks/useHazardReports";
import { hazardStore, HazardReport, HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI } from "../../src/services/hazardStore";
import { C } from "../../src/theme/colors";
import { formatDistanceToNow } from "date-fns";

// ── Haversine distance ────────────────────────────────────────────────────
function distanceKm(lat1: number, lon1: number, lat2: number, lon2: number) {
  const R = 6371;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLon = ((lon2 - lon1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLon / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

function timeAgo(ts: number): string {
  try {
    return formatDistanceToNow(new Date(ts), { addSuffix: true });
  } catch {
    return "recently";
  }
}

function categoryColor(cat: string): string {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#F97316";
  if (cat === "FIRE" || cat === "MEDICAL_EMERGENCY") return "#DC2626";
  if (cat === "OPEN_WIRE") return "#7C3AED";
  return "#D97706";
}

// ── Report detail modal ───────────────────────────────────────────────────
function ReportModal({
  report, userLat, userLon, onClose, onUpvote,
}: {
  report: HazardReport | null;
  userLat: number; userLon: number;
  onClose: () => void;
  onUpvote: (id: string) => void;
}) {
  if (!report) return null;
  const dist = distanceKm(userLat, userLon, report.latitude, report.longitude);
  return (
    <Modal visible={!!report} transparent animationType="slide" onRequestClose={onClose}>
      <View style={ms.overlay}>
        <View style={ms.card}>
          <View style={ms.header}>
            <Text style={ms.emoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
            <View style={{ flex: 1 }}>
              <Text style={ms.title}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
              <Text style={ms.sub}>{timeAgo(report.createdAt)}</Text>
            </View>
            {report.verified ? (
              <View style={ms.verifiedBadge}><Text style={ms.verifiedText}>✓ VERIFIED</Text></View>
            ) : (
              <View style={ms.unverifiedBadge}><Text style={ms.unverifiedText}>⚠ NOT VERIFIED</Text></View>
            )}
          </View>

          <Text style={ms.desc}>{report.description}</Text>

          <View style={ms.row}>
            <View style={ms.infoChip}>
              <Text style={ms.infoLabel}>📍 GPS</Text>
              <Text style={ms.infoVal}>{report.latitude.toFixed(4)}, {report.longitude.toFixed(4)}</Text>
            </View>
            <View style={ms.infoChip}>
              <Text style={ms.infoLabel}>📏 Distance</Text>
              <Text style={ms.infoVal}>{dist < 1 ? `${(dist * 1000).toFixed(0)}m` : `${dist.toFixed(1)}km`}</Text>
            </View>
          </View>

          {report.waterDepth ? (
            <View style={[ms.infoChip, { marginTop: 0, marginBottom: 10 }]}>
              <Text style={ms.infoLabel}>💧 Water Depth</Text>
              <Text style={ms.infoVal}>{report.waterDepth}</Text>
            </View>
          ) : null}

          <View style={ms.actions}>
            <TouchableOpacity onPress={() => onUpvote(report.id)} style={ms.upvoteBtn}>
              <Text style={ms.upvoteTxt}>👍 {report.upvotes} Upvotes</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => Linking.openURL(`https://www.openstreetmap.org/?mlat=${report.latitude}&mlon=${report.longitude}&zoom=17`)}
              style={ms.osmBtn}
            >
              <Text style={ms.osmBtnTxt}>🗺️ View on OSM</Text>
            </TouchableOpacity>
          </View>

          <TouchableOpacity onPress={onClose} style={ms.closeBtn}>
            <Text style={ms.closeTxt}>Close</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
}

// ── Report Card ───────────────────────────────────────────────────────────
function ReportCard({
  report, userLat, userLon, onPress, onUpvote,
}: {
  report: HazardReport; userLat: number; userLon: number;
  onPress: () => void; onUpvote: () => void;
}) {
  const dist = distanceKm(userLat, userLon, report.latitude, report.longitude);
  const distStr = dist < 1 ? `${(dist * 1000).toFixed(0)} m` : `${dist.toFixed(1)} km`;
  const color = categoryColor(report.category);

  return (
    <TouchableOpacity onPress={onPress} style={[rc.card, { borderLeftColor: color }]} activeOpacity={0.8}>
      <View style={rc.top}>
        <Text style={rc.emoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
        <View style={{ flex: 1 }}>
          <Text style={rc.title}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
          <Text style={rc.desc} numberOfLines={2}>{report.description}</Text>
        </View>
        {report.verified ? (
          <View style={rc.vBadge}><Text style={rc.vText}>✓ V</Text></View>
        ) : (
          <View style={rc.uvBadge}><Text style={rc.uvText}>⚠ !V</Text></View>
        )}
      </View>
      <View style={rc.bottom}>
        <Text style={rc.meta}>📍 {report.latitude.toFixed(3)}, {report.longitude.toFixed(3)}</Text>
        <Text style={rc.meta}>📏 {distStr}</Text>
        <Text style={rc.meta}>🕐 {timeAgo(report.createdAt)}</Text>
        <TouchableOpacity onPress={onUpvote} style={rc.upvote}>
          <Text style={rc.upvoteTxt}>▲ {report.upvotes}</Text>
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );
}

// ── Main LiveMapScreen ────────────────────────────────────────────────────
export default function LiveMapScreen() {
  const { location, isLoading } = useLocation();
  const reports = useHazardReports();
  const [search, setSearch] = useState("");
  const [selectedReport, setSelectedReport] = useState<HazardReport | null>(null);
  // re-key the map whenever reports change so OSM rerenders with new markers
  const [mapKey, setMapKey] = useState(0);

  const userLat = location?.available ? location.latitude  : 12.9141;
  const userLon = location?.available ? location.longitude : 74.856;

  // Re-render OSM map when reports list changes (new report added, upvoted etc.)
  const prevReportsLen = useRef(reports.length);
  useEffect(() => {
    if (reports.length !== prevReportsLen.current) {
      setMapKey((k) => k + 1);
      prevReportsLen.current = reports.length;
    }
  }, [reports]);

  const filtered = reports.filter((r) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return (
      HAZARD_CATEGORY_LABELS[r.category].toLowerCase().includes(q) ||
      r.description.toLowerCase().includes(q)
    );
  });

  const aiSummary = hazardStore.getDynamicAISummary();

  return (
    <SafeAreaView style={s.screen}>
      {/* ── Top Header ── */}
      <View style={s.header}>
        <View style={s.headerLeft}>
          <Text style={s.headerTitle}>🗺️ LIVE MAP</Text>
          <Text style={s.headerSub}>Mangaluru • OpenStreetMap</Text>
        </View>
        {isLoading ? (
          <ActivityIndicator color="#fff" size="small" />
        ) : location?.available ? (
          <View style={s.gpsBadge}><Text style={s.gpsBadgeText}>● GPS LIVE</Text></View>
        ) : (
          <View style={[s.gpsBadge, { backgroundColor: C.emergencyRed }]}>
            <Text style={s.gpsBadgeText}>NO GPS</Text>
          </View>
        )}
      </View>

      {/* ── Search Bar ── */}
      <View style={s.searchRow}>
        <Ionicons name="search" size={16} color={C.textSecondary} style={{ marginRight: 6 }} />
        <TextInput
          style={s.searchInput}
          placeholder="Search hazard reports…"
          placeholderTextColor={C.textDisabled}
          value={search}
          onChangeText={setSearch}
        />
        {search.length > 0 && (
          <TouchableOpacity onPress={() => setSearch("")}>
            <Ionicons name="close-circle" size={16} color={C.textDisabled} />
          </TouchableOpacity>
        )}
      </View>

      {/* ── AI Situation Summary ── */}
      <View style={s.aiCard}>
        <Text style={s.aiLabel}>🤖 AI SITUATION SUMMARY</Text>
        <Text style={s.aiText}>{aiSummary}</Text>
      </View>

      {/* ── GPS loading banner ── */}
      {isLoading && (
        <View style={s.gpsBanner}>
          <ActivityIndicator size="small" color="#92400E" />
          <Text style={s.gpsBannerText}>  Calibrating GPS coordinates…</Text>
        </View>
      )}

      {/* ── OpenStreetMap (Leaflet via WebView) ── */}
      <OSMMap
        key={mapKey}
        userLat={userLat}
        userLon={userLon}
        reports={filtered}
        height={300}
        onMarkerPress={(id) => {
          const r = reports.find((x) => x.id === id);
          if (r) setSelectedReport(r);
        }}
      />

      {/* ── Community Reports Feed ── */}
      <View style={s.feedHeader}>
        <Text style={s.feedTitle}>COMMUNITY REPORTS FEED</Text>
        <View style={s.feedBadge}><Text style={s.feedBadgeText}>{filtered.length} active</Text></View>
        <Text style={s.feedSub}>Live Sync • Mangaluru</Text>
      </View>

      <FlatList
        data={filtered}
        keyExtractor={(r) => r.id}
        contentContainerStyle={{ paddingHorizontal: 12, paddingBottom: 80, paddingTop: 4 }}
        ItemSeparatorComponent={() => <View style={{ height: 8 }} />}
        ListEmptyComponent={
          <View style={{ padding: 32, alignItems: "center" }}>
            <Text style={{ color: C.textSecondary, fontSize: 14 }}>No hazard reports match your search.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <ReportCard
            report={item}
            userLat={userLat}
            userLon={userLon}
            onPress={() => setSelectedReport(item)}
            onUpvote={() => hazardStore.upvoteReport(item.id)}
          />
        )}
      />

      {/* ── Report Detail Modal ── */}
      <ReportModal
        report={selectedReport}
        userLat={userLat}
        userLon={userLon}
        onClose={() => setSelectedReport(null)}
        onUpvote={(id) => {
          hazardStore.upvoteReport(id);
          if (selectedReport?.id === id) {
            setSelectedReport({ ...selectedReport, upvotes: selectedReport.upvotes + 1 });
          }
        }}
      />
    </SafeAreaView>
  );
}

// ── Styles ────────────────────────────────────────────────────────────────
const s = StyleSheet.create({
  screen:       { flex: 1, backgroundColor: C.bg },
  header:       { backgroundColor: "#0F172A", paddingHorizontal: 16, paddingVertical: 12, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  headerLeft:   {},
  headerTitle:  { color: "#fff", fontSize: 15, fontWeight: "900", letterSpacing: 0.3 },
  headerSub:    { color: "rgba(255,255,255,0.55)", fontSize: 11, marginTop: 1 },
  gpsBadge:     { backgroundColor: "#166534", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  gpsBadgeText: { color: "#fff", fontSize: 10, fontWeight: "700" },
  searchRow:    { flexDirection: "row", alignItems: "center", backgroundColor: "#fff", margin: 10, borderRadius: 10, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1, borderColor: C.divider },
  searchInput:  { flex: 1, color: C.textPrimary, fontSize: 14 },
  aiCard:       { backgroundColor: "#1E293B", marginHorizontal: 10, marginBottom: 8, borderRadius: 10, padding: 12 },
  aiLabel:      { color: "#38BDF8", fontSize: 10, fontWeight: "700", letterSpacing: 0.8, marginBottom: 4 },
  aiText:       { color: "#E2E8F0", fontSize: 12, lineHeight: 18 },
  gpsBanner:    { backgroundColor: "#FEF3C7", flexDirection: "row", alignItems: "center", paddingHorizontal: 12, paddingVertical: 6 },
  gpsBannerText:{ color: "#92400E", fontSize: 12, fontWeight: "500" },
  feedHeader:   { flexDirection: "row", alignItems: "center", gap: 6, paddingHorizontal: 14, paddingVertical: 10, backgroundColor: "#fff", borderBottomWidth: 1, borderBottomColor: C.divider },
  feedTitle:    { color: "#0F172A", fontSize: 12, fontWeight: "900", letterSpacing: 0.5 },
  feedBadge:    { backgroundColor: "#1E293B", paddingHorizontal: 7, paddingVertical: 2, borderRadius: 10 },
  feedBadgeText:{ color: "#fff", fontSize: 10, fontWeight: "700" },
  feedSub:      { color: C.textSecondary, fontSize: 11, marginLeft: "auto" },
});

const rc = StyleSheet.create({
  card:     { backgroundColor: "#fff", borderRadius: 12, padding: 12, borderLeftWidth: 4, shadowColor: "#000", shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 4, elevation: 2 },
  top:      { flexDirection: "row", alignItems: "flex-start", gap: 8, marginBottom: 8 },
  emoji:    { fontSize: 20 },
  title:    { color: C.textPrimary, fontWeight: "700", fontSize: 13 },
  desc:     { color: C.textSecondary, fontSize: 12, marginTop: 2, lineHeight: 16 },
  vBadge:   { backgroundColor: "#DCFCE7", borderRadius: 4, paddingHorizontal: 5, paddingVertical: 2 },
  vText:    { color: "#16A34A", fontSize: 9, fontWeight: "700" },
  uvBadge:  { backgroundColor: "#FEF3C7", borderRadius: 4, paddingHorizontal: 5, paddingVertical: 2 },
  uvText:   { color: "#B45309", fontSize: 9, fontWeight: "700" },
  bottom:   { flexDirection: "row", alignItems: "center", gap: 8, flexWrap: "wrap" },
  meta:     { color: C.textSecondary, fontSize: 10 },
  upvote:   { marginLeft: "auto" },
  upvoteTxt:{ color: C.orange, fontWeight: "700", fontSize: 11 },
});

const ms = StyleSheet.create({
  overlay:       { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "flex-end" },
  card:          { backgroundColor: "#fff", borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 20, paddingBottom: 32 },
  header:        { flexDirection: "row", alignItems: "center", gap: 10, marginBottom: 12 },
  emoji:         { fontSize: 28 },
  title:         { color: C.textPrimary, fontWeight: "800", fontSize: 16 },
  sub:           { color: C.textSecondary, fontSize: 12, marginTop: 2 },
  verifiedBadge: { backgroundColor: "#DCFCE7", borderRadius: 6, paddingHorizontal: 8, paddingVertical: 3 },
  verifiedText:  { color: "#16A34A", fontSize: 10, fontWeight: "700" },
  unverifiedBadge:{ backgroundColor: "#FEF3C7", borderRadius: 6, paddingHorizontal: 8, paddingVertical: 3 },
  unverifiedText:{ color: "#B45309", fontSize: 10, fontWeight: "700" },
  desc:          { color: C.textPrimary, fontSize: 14, lineHeight: 20, marginBottom: 12 },
  row:           { flexDirection: "row", gap: 10, marginBottom: 10 },
  infoChip:      { flex: 1, backgroundColor: C.surface, borderRadius: 8, padding: 10 },
  infoLabel:     { color: C.textSecondary, fontSize: 10, fontWeight: "600", marginBottom: 2 },
  infoVal:       { color: C.textPrimary, fontSize: 12, fontWeight: "700" },
  actions:       { flexDirection: "row", gap: 10, marginBottom: 12 },
  upvoteBtn:     { flex: 1, backgroundColor: C.orangeBg, borderRadius: 10, padding: 12, alignItems: "center", borderWidth: 1, borderColor: C.orange },
  upvoteTxt:     { color: C.orange, fontWeight: "700", fontSize: 13 },
  osmBtn:        { flex: 1, backgroundColor: "#EFF6FF", borderRadius: 10, padding: 12, alignItems: "center", borderWidth: 1, borderColor: "#BFDBFE" },
  osmBtnTxt:     { color: "#1D4ED8", fontWeight: "700", fontSize: 13 },
  closeBtn:      { backgroundColor: C.surface, borderRadius: 10, padding: 14, alignItems: "center" },
  closeTxt:      { color: C.textSecondary, fontWeight: "600", fontSize: 14 },
});
