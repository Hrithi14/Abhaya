import React, { useState, useEffect, useRef } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  ActivityIndicator, Modal, StyleSheet, FlatList, Linking, Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import OSMMap, { RouteInfo } from "../../src/components/OSMMap";
import { useLocation } from "../../src/hooks/useLocation";
import { useHazardReports } from "../../src/hooks/useHazardReports";
import {
  hazardStore, HazardReport,
  HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI,
} from "../../src/services/hazardStore";
import { C } from "../../src/theme/colors";
import { formatDistanceToNow } from "date-fns";

// ── Helpers ───────────────────────────────────────────────────────────────
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

function timeAgo(ts: number) {
  try { return formatDistanceToNow(new Date(ts), { addSuffix: true }); }
  catch { return "recently"; }
}

function categoryColor(cat: string) {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#F97316";
  if (cat === "FIRE" || cat === "MEDICAL_EMERGENCY") return "#DC2626";
  if (cat === "OPEN_WIRE") return "#7C3AED";
  return "#D97706";
}

// ── Geocode a place name using Nominatim (OSM) ────────────────────────────
async function geocodePlace(query: string): Promise<{ lat: number; lon: number; display: string } | null> {
  try {
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=1&countrycodes=in`;
    const res = await fetch(url, {
      headers: { "User-Agent": "AbhayaDisasterApp/1.0" },
    });
    const data = await res.json();
    if (data && data.length > 0) {
      return {
        lat: parseFloat(data[0].lat),
        lon: parseFloat(data[0].lon),
        display: data[0].display_name,
      };
    }
    return null;
  } catch {
    return null;
  }
}

// ── Route Result Banner ───────────────────────────────────────────────────
function RouteBanner({
  routeInfo, destName, onClear,
}: {
  routeInfo: RouteInfo; destName: string; onClear: () => void;
}) {
  return (
    <View style={[
      rb.wrap,
      { borderColor: routeInfo.hasHazards ? "#DC2626" : "#16A34A" },
    ]}>
      <View style={rb.row}>
        <Text style={rb.icon}>{routeInfo.hasHazards ? "⚠️" : "✅"}</Text>
        <View style={{ flex: 1 }}>
          <Text style={[rb.status, { color: routeInfo.hasHazards ? "#DC2626" : "#16A34A" }]}>
            {routeInfo.hasHazards ? "HAZARDOUS ROUTE DETECTED" : "SAFE ROUTE FOUND"}
          </Text>
          <Text style={rb.dest} numberOfLines={1}>→ {destName}</Text>
        </View>
        <TouchableOpacity onPress={onClear} style={rb.clearBtn}>
          <Ionicons name="close" size={16} color={C.textSecondary} />
        </TouchableOpacity>
      </View>
      <View style={rb.stats}>
        <Chip icon="📏" label={`${routeInfo.distanceKm} km`} />
        <Chip icon="⏱️" label={`${routeInfo.durationMin} min`} />
        {routeInfo.hasHazards && (
          <Chip icon="🚧" label={`${routeInfo.hazardCount} hazard(s)`} color="#DC2626" />
        )}
      </View>
      {routeInfo.hasHazards && (
        <Text style={rb.hazardNames}>
          Hazards: {routeInfo.hazardNames.join(", ")}
          {"\n"}🔵 Blue dashed line = suggested alternate route
        </Text>
      )}
    </View>
  );
}

function Chip({ icon, label, color }: { icon: string; label: string; color?: string }) {
  return (
    <View style={[rb.chip, color ? { borderColor: color, backgroundColor: color + "11" } : {}]}>
      <Text style={{ fontSize: 11 }}>{icon}</Text>
      <Text style={[rb.chipText, color ? { color } : {}]}>{label}</Text>
    </View>
  );
}

// ── Route Input Modal ─────────────────────────────────────────────────────
function RouteModal({
  visible, userLat, userLon, onRoute, onClose,
}: {
  visible: boolean;
  userLat: number; userLon: number;
  onRoute: (lat: number, lon: number, name: string) => void;
  onClose: () => void;
}) {
  const [destText, setDestText] = useState("");
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState("");

  // Quick presets — real Mangaluru locations
  const PRESETS = [
    { name: "Wenlock Hospital",        lat: 12.8646, lon: 74.8427 },
    { name: "Town Hall Mangaluru",     lat: 12.8698, lon: 74.8431 },
    { name: "Kadri Hills Shelter",     lat: 12.8826, lon: 74.8423 },
    { name: "Mangaluru Central Rly",   lat: 12.8654, lon: 74.8400 },
    { name: "St. Aloysius College",    lat: 12.8680, lon: 74.8418 },
    { name: "Bajpe Airport",           lat: 12.9616, lon: 74.8900 },
  ];

  const handleSearch = async () => {
    if (!destText.trim()) { setError("Please enter a destination."); return; }
    setError(""); setLoading(true);
    const result = await geocodePlace(destText.trim() + " Mangaluru Karnataka");
    setLoading(false);
    if (!result) {
      setError("Location not found. Try a different name or use a preset below.");
      return;
    }
    onRoute(result.lat, result.lon, destText.trim());
    setDestText("");
  };

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View style={rm.overlay}>
        <View style={rm.card}>
          <View style={rm.headerRow}>
            <Text style={rm.title}>🧭 Plan Safe Route</Text>
            <TouchableOpacity onPress={onClose}>
              <Ionicons name="close" size={22} color={C.textSecondary} />
            </TouchableOpacity>
          </View>

          <Text style={rm.fromText}>
            📍 From: Your live GPS ({userLat.toFixed(4)}, {userLon.toFixed(4)})
          </Text>

          {/* Destination input */}
          <Text style={rm.label}>Enter Destination</Text>
          <View style={rm.inputRow}>
            <TextInput
              style={rm.input}
              placeholder="e.g. Wenlock Hospital, Kadri Hills…"
              placeholderTextColor={C.textDisabled}
              value={destText}
              onChangeText={(t) => { setDestText(t); setError(""); }}
              onSubmitEditing={handleSearch}
            />
            <TouchableOpacity
              onPress={handleSearch}
              disabled={loading}
              style={rm.searchBtn}
            >
              {loading
                ? <ActivityIndicator color="#fff" size="small" />
                : <Ionicons name="search" size={18} color="#fff" />
              }
            </TouchableOpacity>
          </View>
          {error ? <Text style={rm.error}>{error}</Text> : null}

          {/* Presets */}
          <Text style={rm.label}>Quick Destinations</Text>
          <View style={rm.presets}>
            {PRESETS.map((p) => (
              <TouchableOpacity
                key={p.name}
                onPress={() => { onRoute(p.lat, p.lon, p.name); }}
                style={rm.presetChip}
              >
                <Text style={rm.presetText}>{p.name}</Text>
              </TouchableOpacity>
            ))}
          </View>

          {/* How it works note */}
          <View style={rm.noteBox}>
            <Text style={rm.noteText}>
              🟢 Green route = safe path{"\n"}
              🔴 Red route = hazards detected on path{"\n"}
              🔵 Blue dashed = alternate safer route{"\n"}
              Route avoids reported flood zones automatically
            </Text>
          </View>
        </View>
      </View>
    </Modal>
  );
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
            {report.verified
              ? <View style={ms.verifiedBadge}><Text style={ms.verifiedText}>✓ VERIFIED</Text></View>
              : <View style={ms.unverifiedBadge}><Text style={ms.unverifiedText}>⚠ NOT VERIFIED</Text></View>
            }
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
          {report.waterDepth && (
            <View style={[ms.infoChip, { marginBottom: 10 }]}>
              <Text style={ms.infoLabel}>💧 Water Depth</Text>
              <Text style={ms.infoVal}>{report.waterDepth}</Text>
            </View>
          )}
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

// ── Report Feed Card ──────────────────────────────────────────────────────
function ReportCard({
  report, userLat, userLon, onPress, onUpvote,
}: {
  report: HazardReport; userLat: number; userLon: number;
  onPress: () => void; onUpvote: () => void;
}) {
  const dist = distanceKm(userLat, userLon, report.latitude, report.longitude);
  const distStr = dist < 1 ? `${(dist * 1000).toFixed(0)} m` : `${dist.toFixed(1)} km`;
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[rc.card, { borderLeftColor: categoryColor(report.category) }]}
      activeOpacity={0.8}
    >
      <View style={rc.top}>
        <Text style={rc.emoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
        <View style={{ flex: 1 }}>
          <Text style={rc.title}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
          <Text style={rc.desc} numberOfLines={2}>{report.description}</Text>
        </View>
        {report.verified
          ? <View style={rc.vBadge}><Text style={rc.vText}>✓ V</Text></View>
          : <View style={rc.uvBadge}><Text style={rc.uvText}>⚠ !V</Text></View>
        }
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
  const reports  = useHazardReports();

  const [search, setSearch]               = useState("");
  const [selectedReport, setSelectedReport] = useState<HazardReport | null>(null);
  const [mapKey, setMapKey]               = useState(0);

  // Routing state
  const [showRouteModal, setShowRouteModal] = useState(false);
  const [destLat, setDestLat]             = useState<number | undefined>();
  const [destLon, setDestLon]             = useState<number | undefined>();
  const [destName, setDestName]           = useState("");
  const [showRoute, setShowRoute]         = useState(false);
  const [routeInfo, setRouteInfo]         = useState<RouteInfo | null>(null);

  const userLat = location?.available ? location.latitude  : 12.9141;
  const userLon = location?.available ? location.longitude : 74.856;

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

  const handleRouteSelected = (lat: number, lon: number, name: string) => {
    setDestLat(lat);
    setDestLon(lon);
    setDestName(name);
    setShowRoute(true);
    setRouteInfo(null);
    setShowRouteModal(false);
    setMapKey((k) => k + 1); // re-render map with route
  };

  const handleClearRoute = () => {
    setShowRoute(false);
    setDestLat(undefined);
    setDestLon(undefined);
    setDestName("");
    setRouteInfo(null);
    setMapKey((k) => k + 1);
  };

  return (
    <SafeAreaView style={s.screen}>

      {/* Header */}
      <View style={s.header}>
        <View>
          <Text style={s.headerTitle}>🗺️ LIVE MAP</Text>
          <Text style={s.headerSub}>Mangaluru • OpenStreetMap</Text>
        </View>
        <View style={s.headerRight}>
          {isLoading ? (
            <ActivityIndicator color="#fff" size="small" />
          ) : location?.available ? (
            <View style={s.gpsBadge}><Text style={s.gpsBadgeText}>● GPS</Text></View>
          ) : (
            <View style={[s.gpsBadge, { backgroundColor: C.emergencyRed }]}>
              <Text style={s.gpsBadgeText}>NO GPS</Text>
            </View>
          )}
          {/* Route button */}
          <TouchableOpacity
            onPress={() => setShowRouteModal(true)}
            style={s.routeBtn}
          >
            <Ionicons name="navigate" size={14} color="#fff" />
            <Text style={s.routeBtnText}>ROUTE</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Search */}
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

      {/* AI Summary */}
      <View style={s.aiCard}>
        <Text style={s.aiLabel}>🤖 AI SITUATION SUMMARY</Text>
        <Text style={s.aiText}>{hazardStore.getDynamicAISummary()}</Text>
      </View>

      {/* GPS loading banner */}
      {isLoading && (
        <View style={s.gpsBanner}>
          <ActivityIndicator size="small" color="#92400E" />
          <Text style={s.gpsBannerText}>  Calibrating GPS…</Text>
        </View>
      )}

      {/* Route result banner */}
      {routeInfo && (
        <RouteBanner
          routeInfo={routeInfo}
          destName={destName}
          onClear={handleClearRoute}
        />
      )}

      {/* Calculating route indicator */}
      {showRoute && !routeInfo && (
        <View style={s.calcBanner}>
          <ActivityIndicator size="small" color="#0284C7" />
          <Text style={s.calcText}>  Calculating safe route via OSRM…</Text>
        </View>
      )}

      {/* OSM Map */}
      <OSMMap
        key={mapKey}
        userLat={userLat}
        userLon={userLon}
        reports={filtered}
        height={showRoute ? 380 : 300}
        destLat={destLat}
        destLon={destLon}
        destName={destName}
        showRoute={showRoute}
        onMarkerPress={(id) => {
          const r = reports.find((x) => x.id === id);
          if (r) setSelectedReport(r);
        }}
        onRouteCalculated={(info) => setRouteInfo(info)}
      />

      {/* Community feed */}
      {!showRoute && (
        <>
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
                <Text style={{ color: C.textSecondary, fontSize: 14 }}>No reports match your search.</Text>
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
        </>
      )}

      {/* Clear route button when routing */}
      {showRoute && (
        <TouchableOpacity onPress={handleClearRoute} style={s.clearRouteBtn}>
          <Ionicons name="close-circle" size={16} color={C.emergencyRed} />
          <Text style={s.clearRouteTxt}>Clear Route</Text>
        </TouchableOpacity>
      )}

      {/* Modals */}
      <RouteModal
        visible={showRouteModal}
        userLat={userLat}
        userLon={userLon}
        onRoute={handleRouteSelected}
        onClose={() => setShowRouteModal(false)}
      />
      <ReportModal
        report={selectedReport}
        userLat={userLat}
        userLon={userLon}
        onClose={() => setSelectedReport(null)}
        onUpvote={(id) => {
          hazardStore.upvoteReport(id);
          if (selectedReport?.id === id)
            setSelectedReport({ ...selectedReport, upvotes: selectedReport.upvotes + 1 });
        }}
      />
    </SafeAreaView>
  );
}

// ── Styles ────────────────────────────────────────────────────────────────
const s = StyleSheet.create({
  screen:       { flex: 1, backgroundColor: C.bg },
  header:       { backgroundColor: "#0F172A", paddingHorizontal: 16, paddingVertical: 12, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  headerTitle:  { color: "#fff", fontSize: 15, fontWeight: "900", letterSpacing: 0.3 },
  headerSub:    { color: "rgba(255,255,255,0.55)", fontSize: 11, marginTop: 1 },
  headerRight:  { flexDirection: "row", alignItems: "center", gap: 8 },
  gpsBadge:     { backgroundColor: "#166534", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  gpsBadgeText: { color: "#fff", fontSize: 10, fontWeight: "700" },
  routeBtn:     { flexDirection: "row", alignItems: "center", gap: 4, backgroundColor: "#0284C7", paddingHorizontal: 10, paddingVertical: 6, borderRadius: 8 },
  routeBtnText: { color: "#fff", fontSize: 11, fontWeight: "700" },
  searchRow:    { flexDirection: "row", alignItems: "center", backgroundColor: "#fff", margin: 10, borderRadius: 10, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1, borderColor: C.divider },
  searchInput:  { flex: 1, color: C.textPrimary, fontSize: 14 },
  aiCard:       { backgroundColor: "#1E293B", marginHorizontal: 10, marginBottom: 8, borderRadius: 10, padding: 12 },
  aiLabel:      { color: "#38BDF8", fontSize: 10, fontWeight: "700", letterSpacing: 0.8, marginBottom: 4 },
  aiText:       { color: "#E2E8F0", fontSize: 12, lineHeight: 18 },
  gpsBanner:    { backgroundColor: "#FEF3C7", flexDirection: "row", alignItems: "center", paddingHorizontal: 12, paddingVertical: 6 },
  gpsBannerText:{ color: "#92400E", fontSize: 12, fontWeight: "500" },
  calcBanner:   { backgroundColor: "#EFF6FF", flexDirection: "row", alignItems: "center", paddingHorizontal: 12, paddingVertical: 6 },
  calcText:     { color: "#1D4ED8", fontSize: 12, fontWeight: "500" },
  feedHeader:   { flexDirection: "row", alignItems: "center", gap: 6, paddingHorizontal: 14, paddingVertical: 10, backgroundColor: "#fff", borderBottomWidth: 1, borderBottomColor: C.divider },
  feedTitle:    { color: "#0F172A", fontSize: 12, fontWeight: "900", letterSpacing: 0.5 },
  feedBadge:    { backgroundColor: "#1E293B", paddingHorizontal: 7, paddingVertical: 2, borderRadius: 10 },
  feedBadgeText:{ color: "#fff", fontSize: 10, fontWeight: "700" },
  feedSub:      { color: C.textSecondary, fontSize: 11, marginLeft: "auto" },
  clearRouteBtn:{ flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6, padding: 12, backgroundColor: "#FEF2F2", borderTopWidth: 1, borderTopColor: "#FCA5A5" },
  clearRouteTxt:{ color: C.emergencyRed, fontWeight: "700", fontSize: 13 },
});

const rb = StyleSheet.create({
  wrap:     { marginHorizontal: 10, marginBottom: 6, backgroundColor: "#fff", borderRadius: 10, padding: 12, borderWidth: 1.5 },
  row:      { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 8 },
  icon:     { fontSize: 20 },
  status:   { fontWeight: "800", fontSize: 12, letterSpacing: 0.3 },
  dest:     { color: C.textSecondary, fontSize: 11, marginTop: 1 },
  clearBtn: { padding: 4 },
  stats:    { flexDirection: "row", gap: 8 },
  chip:     { flexDirection: "row", alignItems: "center", gap: 4, backgroundColor: C.surface, borderRadius: 6, paddingHorizontal: 8, paddingVertical: 4, borderWidth: 1, borderColor: C.divider },
  chipText: { color: C.textPrimary, fontSize: 11, fontWeight: "600" },
  hazardNames:{ color: "#DC2626", fontSize: 11, marginTop: 8, lineHeight: 18 },
});

const rm = StyleSheet.create({
  overlay:    { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "flex-end" },
  card:       { backgroundColor: "#fff", borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 20, paddingBottom: 32 },
  headerRow:  { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 12 },
  title:      { color: "#0F172A", fontSize: 18, fontWeight: "800" },
  fromText:   { color: "#16A34A", fontSize: 12, fontWeight: "600", marginBottom: 14, backgroundColor: "#F0FDF4", padding: 8, borderRadius: 8 },
  label:      { color: C.textSecondary, fontSize: 11, fontWeight: "700", letterSpacing: 0.5, marginBottom: 8 },
  inputRow:   { flexDirection: "row", gap: 8, marginBottom: 6 },
  input:      { flex: 1, backgroundColor: C.surface, borderRadius: 10, paddingHorizontal: 14, paddingVertical: 12, color: C.textPrimary, fontSize: 14, borderWidth: 1, borderColor: C.divider },
  searchBtn:  { backgroundColor: "#0284C7", borderRadius: 10, width: 46, alignItems: "center", justifyContent: "center" },
  error:      { color: C.emergencyRed, fontSize: 12, marginBottom: 8 },
  presets:    { flexDirection: "row", flexWrap: "wrap", gap: 8, marginBottom: 14 },
  presetChip: { backgroundColor: "#EFF6FF", borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1, borderColor: "#BFDBFE" },
  presetText: { color: "#1D4ED8", fontSize: 12, fontWeight: "600" },
  noteBox:    { backgroundColor: "#F8FAFC", borderRadius: 10, padding: 12, borderWidth: 1, borderColor: C.divider },
  noteText:   { color: C.textSecondary, fontSize: 12, lineHeight: 20 },
});

const rc = StyleSheet.create({
  card:     { backgroundColor: "#fff", borderRadius: 12, padding: 12, borderLeftWidth: 4, elevation: 2 },
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
