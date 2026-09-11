/**
 * Live Map — real-time hazard map with:
 *  - Firestore live reports (via hazardStore mergeFromFirestore)
 *  - OSRM rerouting (source → destination avoiding hazards)
 *  - Gemini AI situation summary
 *  - Pull-to-refresh feed
 */
import React, { useState, useEffect, useRef, useCallback } from "react";
import {
  View, Text, TextInput, TouchableOpacity, ActivityIndicator,
  Modal, StyleSheet, FlatList, Linking, Alert, RefreshControl,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import OSMMap, { OSMMapRef } from "../../src/components/OSMMap";
import { useLocation } from "../../src/hooks/useLocation";
import { useHazardReports } from "../../src/hooks/useHazardReports";
import {
  hazardStore, HazardReport,
  HAZARD_CATEGORY_LABELS, HAZARD_CATEGORY_EMOJI,
} from "../../src/services/hazardStore";
import { getGeminiSituationSummary } from "../../src/services/geminiValidate";
import { C } from "../../src/theme/colors";
import { formatDistanceToNow } from "date-fns";

function timeAgo(ts: number): string {
  try { return formatDistanceToNow(new Date(ts), { addSuffix: true }); }
  catch { return "recently"; }
}

function distanceKm(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLon = ((lon2 - lon1) * Math.PI) / 180;
  const a = Math.sin(dLat/2)**2 +
    Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLon/2)**2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}

function markerBorderColor(cat: string): string {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#1565C0";
  if (cat === "FIRE" || cat === "MEDICAL_EMERGENCY")   return "#D32F2F";
  if (cat === "OPEN_WIRE")  return "#6A1B9A";
  if (cat === "FALLEN_TREE") return "#2E7D32";
  return "#E65100";
}

// ── Report detail modal ───────────────────────────────────────────────────
function ReportModal({
  report, userLat, userLon, onClose, onUpvote,
}: {
  report: HazardReport | null; userLat: number; userLon: number;
  onClose: () => void; onUpvote: (id: string) => void;
}) {
  if (!report) return null;
  const dist = distanceKm(userLat, userLon, report.latitude, report.longitude);
  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <View style={ms.overlay}>
        <View style={ms.card}>
          <View style={ms.header}>
            <Text style={ms.emoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
            <View style={{ flex: 1 }}>
              <Text style={ms.title}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
              <Text style={ms.sub}>{timeAgo(report.createdAt)}</Text>
            </View>
            {report.verified
              ? <View style={ms.vBadge}><Text style={ms.vText}>✓ VERIFIED</Text></View>
              : <View style={ms.uvBadge}><Text style={ms.uvText}>⚠ UNVERIFIED</Text></View>}
          </View>
          <Text style={ms.desc}>{report.description}</Text>
          <View style={ms.row}>
            <View style={ms.chip}><Text style={ms.chipLabel}>📍 GPS</Text><Text style={ms.chipVal}>{report.latitude.toFixed(4)}, {report.longitude.toFixed(4)}</Text></View>
            <View style={ms.chip}><Text style={ms.chipLabel}>📏 Distance</Text><Text style={ms.chipVal}>{dist < 1 ? `${(dist*1000).toFixed(0)}m` : `${dist.toFixed(1)}km`}</Text></View>
          </View>
          {report.waterDepth ? (
            <View style={[ms.chip, { marginBottom: 10 }]}>
              <Text style={ms.chipLabel}>💧 Water Depth</Text>
              <Text style={ms.chipVal}>{report.waterDepth}</Text>
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
              <Text style={ms.osmTxt}>🗺️ View on OSM</Text>
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

// ── Report card in feed ───────────────────────────────────────────────────
function ReportCard({
  report, userLat, userLon, onPress, onUpvote,
}: {
  report: HazardReport; userLat: number; userLon: number;
  onPress: () => void; onUpvote: () => void;
}) {
  const dist    = distanceKm(userLat, userLon, report.latitude, report.longitude);
  const distStr = dist < 1 ? `${(dist*1000).toFixed(0)} m` : `${dist.toFixed(1)} km`;
  const color   = markerBorderColor(report.category);

  return (
    <TouchableOpacity onPress={onPress} style={[rc.card, { borderLeftColor: color }]} activeOpacity={0.8}>
      <View style={rc.top}>
        <Text style={rc.emoji}>{HAZARD_CATEGORY_EMOJI[report.category]}</Text>
        <View style={{ flex: 1 }}>
          <Text style={rc.title}>{HAZARD_CATEGORY_LABELS[report.category]}</Text>
          <Text style={rc.desc} numberOfLines={2}>{report.description}</Text>
        </View>
        {report.verified
          ? <View style={rc.vBadge}><Text style={rc.vText}>✓ V</Text></View>
          : <View style={rc.uvBadge}><Text style={rc.uvText}>⚠</Text></View>}
      </View>
      <View style={rc.bottom}>
        <Text style={rc.meta}>📏 {distStr}</Text>
        <Text style={rc.meta}>🕐 {timeAgo(report.createdAt)}</Text>
        <TouchableOpacity onPress={onUpvote} style={rc.upvote}>
          <Text style={rc.upvoteTxt}>▲ {report.upvotes}</Text>
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );
}

// ── Main screen ───────────────────────────────────────────────────────────
export default function LiveMapScreen() {
  const { location, isLoading } = useLocation();
  const reports   = useHazardReports();
  const mapRef    = useRef<OSMMapRef>(null);

  const [search, setSearch]             = useState("");
  const [selectedReport, setSelectedReport] = useState<HazardReport | null>(null);
  const [mapKey, setMapKey]             = useState(0);

  // Routing state
  const [routeMode, setRouteMode]       = useState(false);
  const [destInput, setDestInput]       = useState("");
  const [routeInfo, setRouteInfo]       = useState<{ distKm: string; durationMins: number } | null>(null);
  const [routeActive, setRouteActive]   = useState(false);

  // Gemini summary
  const [aiSummary, setAiSummary]       = useState(hazardStore.getDynamicAISummary());
  const [aiLoading, setAiLoading]       = useState(false);

  const userLat = location?.available ? location.latitude  : 12.9141;
  const userLon = location?.available ? location.longitude : 74.856;

  // Re-key map on report count change
  const prevLen = useRef(reports.length);
  useEffect(() => {
    if (reports.length !== prevLen.current) {
      setMapKey((k) => k + 1);
      prevLen.current = reports.length;
    }
  }, [reports]);

  // Gemini summary — refresh when reports change, passes full report data
  const refreshSummary = useCallback(async () => {
    setAiLoading(true);
    const floodCount = reports.filter(
      (r) => r.category === "FLOOD_WATER" || r.category === "WATERLOGGING"
    ).length;
    const topCat = reports[0] ? HAZARD_CATEGORY_LABELS[reports[0].category] : "N/A";
    // Pass full report objects so Gemini gets actual descriptions
    const summary = await getGeminiSituationSummary(
      floodCount,
      reports.length,
      topCat,
      reports.map((r) => ({
        category:    r.category,
        description: r.description,
        verified:    r.verified,
        upvotes:     r.upvotes,
        latitude:    r.latitude,
        longitude:   r.longitude,
        waterDepth:  r.waterDepth,
      }))
    );
    setAiSummary(summary);
    setAiLoading(false);
  }, [reports]);

  useEffect(() => { refreshSummary(); }, [reports.length]);

  const filtered = reports.filter((r) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return (
      HAZARD_CATEGORY_LABELS[r.category].toLowerCase().includes(q) ||
      r.description.toLowerCase().includes(q)
    );
  });

  // ── Routing: geocode place name → lat/lon via Nominatim ─────────────────
  const [geocoding, setGeocoding]           = useState(false);
  const [suggestions, setSuggestions]       = useState<{ name: string; lat: number; lon: number }[]>([]);

  const searchPlace = async (query: string) => {
    setDestInput(query);
    if (query.trim().length < 3) { setSuggestions([]); return; }
    try {
      const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=5&addressdetails=1`;
      const res  = await fetch(url, { headers: { "User-Agent": "AbhayaApp/1.0" } });
      const json = await res.json();
      const results = json.map((item: any) => ({
        name: item.display_name,
        lat:  parseFloat(item.lat),
        lon:  parseFloat(item.lon),
      }));
      setSuggestions(results);
    } catch {
      setSuggestions([]);
    }
  };

  const selectPlace = (place: { name: string; lat: number; lon: number }) => {
    setDestInput(place.name.split(",")[0]); // show short name
    setSuggestions([]);
    mapRef.current?.drawRoute(userLat, userLon, place.lat, place.lon);
    setRouteActive(true);
    setRouteMode(false);
  };

  const handleStartRoute = async () => {
    if (!destInput.trim()) {
      Alert.alert("Enter a destination", "Type a place name to get directions.");
      return;
    }
    // If user typed something but didn't pick a suggestion — geocode directly
    setGeocoding(true);
    try {
      const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(destInput.trim())}&format=json&limit=1`;
      const res  = await fetch(url, { headers: { "User-Agent": "AbhayaApp/1.0" } });
      const json = await res.json();
      if (!json.length) {
        Alert.alert("Place not found", `Could not find "${destInput}". Try a more specific name.`);
        setGeocoding(false);
        return;
      }
      const toLat = parseFloat(json[0].lat);
      const toLon = parseFloat(json[0].lon);
      mapRef.current?.drawRoute(userLat, userLon, toLat, toLon);
      setRouteActive(true);
      setRouteMode(false);
    } catch {
      Alert.alert("Network error", "Could not search for the place. Check your internet.");
    }
    setGeocoding(false);
  };

  const handleClearRoute = () => {
    mapRef.current?.clearRoute();
    setRouteActive(false);
    setRouteInfo(null);
    setDestInput("");
  };

  return (
    <SafeAreaView style={s.screen}>
      {/* Header */}
      <View style={s.header}>
        <View>
          <Text style={s.headerTitle}>🗺️ LIVE MAP</Text>
          <Text style={s.headerSub}>Mangaluru · OpenStreetMap · Real-time</Text>
        </View>
        <View style={s.headerRight}>
          {isLoading
            ? <ActivityIndicator color="#fff" size="small" />
            : location?.available
              ? <View style={s.gpsBadge}><Text style={s.gpsBadgeText}>● GPS LIVE</Text></View>
              : <View style={[s.gpsBadge, { backgroundColor: C.emergencyRed }]}><Text style={s.gpsBadgeText}>NO GPS</Text></View>}
          <TouchableOpacity onPress={() => setRouteMode(true)} style={s.routeBtn}>
            <Ionicons name="navigate" size={16} color="#fff" />
            <Text style={s.routeBtnText}> ROUTE</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Search */}
      <View style={s.searchRow}>
        <Ionicons name="search" size={16} color={C.textSecondary} />
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

      {/* Route info banner */}
      {routeActive && routeInfo && (
        <View style={s.routeInfoBar}>
          <Text style={s.routeInfoText}>
            🗺️ Route: {routeInfo.distKm} km · ~{routeInfo.durationMins} min
          </Text>
          <TouchableOpacity onPress={handleClearRoute}>
            <Text style={s.routeClearText}>✕ Clear</Text>
          </TouchableOpacity>
        </View>
      )}
      {routeActive && !routeInfo && (
        <View style={s.routeInfoBar}>
          <ActivityIndicator size="small" color={C.orange} />
          <Text style={s.routeInfoText}>  Calculating route…</Text>
          <TouchableOpacity onPress={handleClearRoute}>
            <Text style={s.routeClearText}>✕ Clear</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* AI Summary */}
      <View style={s.aiCard}>
        <View style={s.aiLabelRow}>
          <Text style={s.aiLabel}>🤖 AI SITUATION SUMMARY</Text>
          <TouchableOpacity onPress={refreshSummary} disabled={aiLoading}>
            {aiLoading
              ? <ActivityIndicator size="small" color="#38BDF8" />
              : <Ionicons name="refresh" size={14} color="#38BDF8" />}
          </TouchableOpacity>
        </View>
        <Text style={s.aiText}>{aiSummary}</Text>
      </View>

      {/* Map */}
      <OSMMap
        key={mapKey}
        ref={mapRef}
        userLat={userLat}
        userLon={userLon}
        reports={filtered}
        height={280}
        onMarkerPress={(id) => {
          const r = reports.find((x) => x.id === id);
          if (r) setSelectedReport(r);
        }}
      />

      {/* Feed header */}
      <View style={s.feedHeader}>
        <Text style={s.feedTitle}>COMMUNITY REPORTS FEED</Text>
        <View style={s.feedBadge}><Text style={s.feedBadgeText}>{filtered.length} active</Text></View>
        <Text style={s.feedSub}>Live · Mangaluru</Text>
      </View>

      {/* Reports list */}
      <FlatList
        data={filtered}
        keyExtractor={(r) => r.id}
        contentContainerStyle={{ paddingHorizontal: 12, paddingBottom: 80, paddingTop: 4 }}
        ItemSeparatorComponent={() => <View style={{ height: 8 }} />}
        refreshControl={
          <RefreshControl refreshing={aiLoading} onRefresh={refreshSummary} tintColor={C.orange} />
        }
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

      {/* Detail modal */}
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

      {/* Route input modal */}
      <Modal visible={routeMode} transparent animationType="fade" onRequestClose={() => setRouteMode(false)}>
        <View style={rm.overlay}>
          <View style={rm.card}>
            <Text style={rm.title}>🗺️ Get Directions</Text>
            <Text style={rm.sub}>Type a place name — hospital, shelter, area, landmark</Text>

            {/* From */}
            <View style={rm.fromRow}>
              <View style={rm.fromDot} />
              <Text style={rm.fromText}>
                From: Your current location ({userLat.toFixed(4)}, {userLon.toFixed(4)})
              </Text>
            </View>

            {/* To — place name search */}
            <Text style={rm.inputLabel}>To:</Text>
            <View style={rm.inputRow}>
              <Ionicons name="search" size={16} color={C.textSecondary} style={{ marginRight: 8 }} />
              <TextInput
                style={rm.input}
                placeholder="e.g. Wenlock Hospital, Mangaluru"
                placeholderTextColor={C.textDisabled}
                value={destInput}
                onChangeText={searchPlace}
                autoCorrect={false}
                returnKeyType="search"
                onSubmitEditing={handleStartRoute}
              />
              {destInput.length > 0 && (
                <TouchableOpacity onPress={() => { setDestInput(""); setSuggestions([]); }}>
                  <Ionicons name="close-circle" size={18} color={C.textDisabled} />
                </TouchableOpacity>
              )}
            </View>

            {/* Suggestions list */}
            {suggestions.length > 0 && (
              <View style={rm.suggestBox}>
                {suggestions.map((s, i) => (
                  <TouchableOpacity
                    key={i}
                    style={[rm.suggestRow, i < suggestions.length - 1 && rm.suggestBorder]}
                    onPress={() => selectPlace(s)}
                    activeOpacity={0.7}
                  >
                    <Ionicons name="location-outline" size={14} color={C.orange} style={{ marginRight: 8 }} />
                    <Text style={rm.suggestText} numberOfLines={2}>{s.name}</Text>
                  </TouchableOpacity>
                ))}
              </View>
            )}

            <Text style={rm.hint}>
              💡 Try: "Wenlock Hospital", "Kadri Hills", "MG Road Mangaluru"
            </Text>

            <View style={rm.btnRow}>
              <TouchableOpacity
                onPress={() => { setRouteMode(false); setSuggestions([]); }}
                style={rm.cancelBtn}
              >
                <Text style={rm.cancelTxt}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={handleStartRoute}
                disabled={geocoding}
                style={[rm.goBtn, geocoding && { opacity: 0.6 }]}
              >
                {geocoding
                  ? <ActivityIndicator color="#fff" size="small" />
                  : <Text style={rm.goTxt}>Get Route</Text>}
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

// ── Styles ─────────────────────────────────────────────────────────────────
const s = StyleSheet.create({
  screen:        { flex: 1, backgroundColor: C.bg },
  header:        { backgroundColor: "#0F172A", paddingHorizontal: 16, paddingVertical: 12, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  headerTitle:   { color: "#fff", fontSize: 15, fontWeight: "900" },
  headerSub:     { color: "rgba(255,255,255,0.5)", fontSize: 10, marginTop: 1 },
  headerRight:   { flexDirection: "row", alignItems: "center", gap: 8 },
  gpsBadge:      { backgroundColor: "#166534", paddingHorizontal: 7, paddingVertical: 3, borderRadius: 5 },
  gpsBadgeText:  { color: "#fff", fontSize: 9, fontWeight: "700" },
  routeBtn:      { backgroundColor: "#1D4ED8", borderRadius: 7, paddingHorizontal: 10, paddingVertical: 6, flexDirection: "row", alignItems: "center" },
  routeBtnText:  { color: "#fff", fontSize: 11, fontWeight: "700" },
  searchRow:     { flexDirection: "row", alignItems: "center", backgroundColor: "#fff", margin: 10, borderRadius: 10, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1, borderColor: C.divider, gap: 6 },
  searchInput:   { flex: 1, color: C.textPrimary, fontSize: 14 },
  routeInfoBar:  { backgroundColor: "#EFF6FF", flexDirection: "row", alignItems: "center", paddingHorizontal: 14, paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: "#BFDBFE" },
  routeInfoText: { color: "#1D4ED8", fontSize: 12, fontWeight: "600", flex: 1 },
  routeClearText:{ color: C.emergencyRed, fontSize: 12, fontWeight: "700" },
  aiCard:        { backgroundColor: "#1E293B", marginHorizontal: 10, marginBottom: 6, borderRadius: 10, padding: 10 },
  aiLabelRow:    { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 4 },
  aiLabel:       { color: "#38BDF8", fontSize: 10, fontWeight: "700", letterSpacing: 0.8 },
  aiText:        { color: "#E2E8F0", fontSize: 12, lineHeight: 17 },
  feedHeader:    { flexDirection: "row", alignItems: "center", gap: 6, paddingHorizontal: 14, paddingVertical: 8, backgroundColor: "#fff", borderBottomWidth: 1, borderBottomColor: C.divider },
  feedTitle:     { color: "#0F172A", fontSize: 11, fontWeight: "900", letterSpacing: 0.5 },
  feedBadge:     { backgroundColor: "#1E293B", paddingHorizontal: 7, paddingVertical: 2, borderRadius: 10 },
  feedBadgeText: { color: "#fff", fontSize: 10, fontWeight: "700" },
  feedSub:       { color: C.textSecondary, fontSize: 11, marginLeft: "auto" },
});

const rc = StyleSheet.create({
  card:    { backgroundColor: "#fff", borderRadius: 10, padding: 11, borderLeftWidth: 4, elevation: 1 },
  top:     { flexDirection: "row", alignItems: "flex-start", gap: 8, marginBottom: 6 },
  emoji:   { fontSize: 18 },
  title:   { color: C.textPrimary, fontWeight: "700", fontSize: 13 },
  desc:    { color: C.textSecondary, fontSize: 12, marginTop: 2, lineHeight: 16 },
  vBadge:  { backgroundColor: "#DCFCE7", borderRadius: 4, paddingHorizontal: 5, paddingVertical: 2 },
  vText:   { color: "#16A34A", fontSize: 9, fontWeight: "700" },
  uvBadge: { backgroundColor: "#FEF3C7", borderRadius: 4, paddingHorizontal: 5, paddingVertical: 2 },
  uvText:  { color: "#B45309", fontSize: 9, fontWeight: "700" },
  bottom:  { flexDirection: "row", alignItems: "center", gap: 8 },
  meta:    { color: C.textSecondary, fontSize: 10 },
  upvote:  { marginLeft: "auto" },
  upvoteTxt: { color: C.orange, fontWeight: "700", fontSize: 11 },
});

const ms = StyleSheet.create({
  overlay:  { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "flex-end" },
  card:     { backgroundColor: "#fff", borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 20, paddingBottom: 32 },
  header:   { flexDirection: "row", alignItems: "center", gap: 10, marginBottom: 10 },
  emoji:    { fontSize: 26 },
  title:    { color: C.textPrimary, fontWeight: "800", fontSize: 16 },
  sub:      { color: C.textSecondary, fontSize: 12 },
  vBadge:   { backgroundColor: "#DCFCE7", borderRadius: 5, paddingHorizontal: 7, paddingVertical: 2 },
  vText:    { color: "#16A34A", fontSize: 10, fontWeight: "700" },
  uvBadge:  { backgroundColor: "#FEF3C7", borderRadius: 5, paddingHorizontal: 7, paddingVertical: 2 },
  uvText:   { color: "#B45309", fontSize: 10, fontWeight: "700" },
  desc:     { color: C.textPrimary, fontSize: 14, lineHeight: 20, marginBottom: 12 },
  row:      { flexDirection: "row", gap: 10, marginBottom: 10 },
  chip:     { flex: 1, backgroundColor: C.surface, borderRadius: 8, padding: 10 },
  chipLabel:{ color: C.textSecondary, fontSize: 10, fontWeight: "600", marginBottom: 2 },
  chipVal:  { color: C.textPrimary, fontSize: 12, fontWeight: "700" },
  actions:  { flexDirection: "row", gap: 10, marginBottom: 12 },
  upvoteBtn:{ flex: 1, backgroundColor: C.orangeBg, borderRadius: 10, padding: 12, alignItems: "center", borderWidth: 1, borderColor: C.orange },
  upvoteTxt:{ color: C.orange, fontWeight: "700", fontSize: 13 },
  osmBtn:   { flex: 1, backgroundColor: "#EFF6FF", borderRadius: 10, padding: 12, alignItems: "center", borderWidth: 1, borderColor: "#BFDBFE" },
  osmTxt:   { color: "#1D4ED8", fontWeight: "700", fontSize: 13 },
  closeBtn: { backgroundColor: C.surface, borderRadius: 10, padding: 14, alignItems: "center" },
  closeTxt: { color: C.textSecondary, fontWeight: "600", fontSize: 14 },
});

const rm = StyleSheet.create({
  overlay:      { flex: 1, backgroundColor: "rgba(0,0,0,0.55)", justifyContent: "center", padding: 20 },
  card:         { backgroundColor: "#fff", borderRadius: 16, padding: 20 },
  title:        { color: C.textPrimary, fontSize: 18, fontWeight: "800", marginBottom: 4 },
  sub:          { color: C.textSecondary, fontSize: 13, lineHeight: 18, marginBottom: 14 },

  // From row
  fromRow:      { flexDirection: "row", alignItems: "center", backgroundColor: "#F0FFF4", borderRadius: 8, padding: 10, marginBottom: 12 },
  fromDot:      { width: 10, height: 10, borderRadius: 5, backgroundColor: "#16A34A", marginRight: 8 },
  fromText:     { color: "#166534", fontSize: 12, fontWeight: "600", flex: 1 },

  // Input
  inputLabel:   { color: C.textSecondary, fontSize: 12, fontWeight: "600", marginBottom: 6 },
  inputRow:     { flexDirection: "row", alignItems: "center", backgroundColor: C.surface, borderRadius: 10, borderWidth: 1.5, borderColor: C.divider, paddingHorizontal: 12, paddingVertical: 10, marginBottom: 4 },
  input:        { flex: 1, color: C.textPrimary, fontSize: 14 },

  // Suggestions
  suggestBox:   { backgroundColor: "#fff", borderRadius: 10, borderWidth: 1, borderColor: C.divider, marginBottom: 8, elevation: 4, shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4 },
  suggestRow:   { flexDirection: "row", alignItems: "center", padding: 12 },
  suggestBorder:{ borderBottomWidth: 1, borderBottomColor: C.divider },
  suggestText:  { flex: 1, color: C.textPrimary, fontSize: 13, lineHeight: 18 },

  hint:         { color: C.textDisabled, fontSize: 11, lineHeight: 16, marginBottom: 16 },

  btnRow:       { flexDirection: "row", gap: 10 },
  cancelBtn:    { flex: 1, backgroundColor: C.surface, borderRadius: 10, padding: 14, alignItems: "center", borderWidth: 1, borderColor: C.divider },
  cancelTxt:    { color: C.textSecondary, fontWeight: "600" },
  goBtn:        { flex: 1, backgroundColor: "#1D4ED8", borderRadius: 10, padding: 14, alignItems: "center" },
  goTxt:        { color: "#fff", fontWeight: "700", fontSize: 14 },
});
