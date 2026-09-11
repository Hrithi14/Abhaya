/**
 * Evacuate screen
 * - Shows OSM map immediately
 * - Auto-draws OSRM route to nearest hazard-free shelter on load
 * - Lists all shelters sorted by distance, skipping ones near hazards
 * - Tap any shelter card to reroute to it
 */
import React, { useState, useEffect, useRef } from "react";
import {
  View, Text, ScrollView, TouchableOpacity,
  ActivityIndicator, StyleSheet, Linking,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { useLocation } from "../src/hooks/useLocation";
import { useHazardReports } from "../src/hooks/useHazardReports";
import OSMMap, { OSMMapRef } from "../src/components/OSMMap";
import { C } from "../src/theme/colors";

// ── Safe shelters — real locations ────────────────────────────────────────
const SHELTERS = [
  {
    name: "Town Hall Mangaluru",
    address: "Nehru Maidan Rd, Hampankatta, Mangaluru",
    elevation: "42m", occupancy: "210 / 850",
    amenities: ["Water", "Food", "First Aid", "Generators"],
    lat: 12.8698, lng: 74.8425,
  },
  {
    name: "Kadri Hills Relief Pavilion",
    address: "Near Kadri Temple Grounds, Mangaluru",
    elevation: "65m", occupancy: "130 / 600",
    amenities: ["Water", "Sanitary Kits", "Ambulance"],
    lat: 12.8830, lng: 74.8580,
  },
  {
    name: "Alvas College High Ground",
    address: "Vidyagiri, Moodbidri - 574227",
    elevation: "62m", occupancy: "500 / 1500",
    amenities: ["Medical Bay", "Food", "Water", "Dormitories"],
    lat: 13.0650, lng: 74.9980,
  },
  {
    name: "Moodbidri Panchayat Office",
    address: "Main Road, Moodbidri, Dakshina Kannada",
    elevation: "55m", occupancy: "150 / 400",
    amenities: ["Water", "Food", "First Aid"],
    lat: 13.0676, lng: 74.9931,
  },
  {
    name: "Government High School Moodbidri",
    address: "School Road, Moodbidri - 574227",
    elevation: "48m", occupancy: "200 / 600",
    amenities: ["Water", "Food", "Shelter", "Generators"],
    lat: 13.0710, lng: 74.9960,
  },
];

function haversineKm(lat1: number, lng1: number, lat2: number, lng2: number) {
  const R   = 6371;
  const dLt = (lat2 - lat1) * Math.PI / 180;
  const dLn = (lng2 - lng1) * Math.PI / 180;
  const a   = Math.sin(dLt / 2) ** 2 +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLn / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

function cardinalDir(lat1: number, lng1: number, lat2: number, lng2: number): string {
  const a = Math.atan2(lng2 - lng1, lat2 - lat1) * 180 / Math.PI;
  if (a >= -22.5  && a < 22.5)  return "north";
  if (a >= 22.5   && a < 67.5)  return "north-east";
  if (a >= 67.5   && a < 112.5) return "east";
  if (a >= 112.5  && a < 157.5) return "south-east";
  if (a >= 157.5  || a < -157.5) return "south";
  if (a >= -157.5 && a < -112.5) return "south-west";
  if (a >= -112.5 && a < -67.5) return "west";
  return "north-west";
}

export default function EvacuateScreen() {
  const router  = useRouter();
  const { location, isLoading, refresh } = useLocation();
  const reports = useHazardReports();
  const mapRef  = useRef<OSMMapRef>(null);

  const [ready, setReady]         = useState(false);
  const [activeIdx, setActiveIdx] = useState<number | null>(null);
  const autoDrawn                 = useRef(false);

  // Wait briefly for GPS to settle
  useEffect(() => {
    if (!isLoading) {
      const t = setTimeout(() => setReady(true), 500);
      return () => clearTimeout(t);
    }
  }, [isLoading]);

  const userLat = location?.available ? location.latitude  : 12.9141;
  const userLon = location?.available ? location.longitude : 74.856;

  // Build shelter list with distance + hazard check
  const shelters = SHELTERS
    .map((sh) => {
      const km      = haversineKm(userLat, userLon, sh.lat, sh.lng);
      const dir     = cardinalDir(userLat, userLon, sh.lat, sh.lng);
      const blocked = reports.some((r) => haversineKm(sh.lat, sh.lng, r.latitude, r.longitude) < 0.3);
      return { ...sh, km, dir, blocked };
    })
    .sort((a, b) => a.km - b.km);

  const safe = shelters.filter((s) => !s.blocked);

  // Auto-draw route to nearest safe shelter once map is ready
  useEffect(() => {
    if (!ready || autoDrawn.current) return;
    const best = safe[0] ?? shelters[0];
    if (!best) return;
    autoDrawn.current = true;
    const idx = shelters.indexOf(best);
    setActiveIdx(idx);
    setTimeout(() => {
      mapRef.current?.drawRoute(userLat, userLon, best.lat, best.lng);
    }, 1200);
  }, [ready]);

  const routeTo = (i: number) => {
    const sh = shelters[i];
    setActiveIdx(i);
    mapRef.current?.drawRoute(userLat, userLon, sh.lat, sh.lng);
    mapRef.current?.recenter(
      (userLat + sh.lat) / 2,
      (userLon + sh.lng) / 2,
      12
    );
  };

  const openMaps = (sh: typeof shelters[0]) => {
    const url = location?.available
      ? `https://maps.google.com/maps?saddr=${location.latitude},${location.longitude}&daddr=${sh.lat},${sh.lng}&travelmode=walking`
      : `https://maps.google.com/maps?q=${sh.lat},${sh.lng}`;
    Linking.openURL(url);
  };

  const activeShelter = activeIdx !== null ? shelters[activeIdx] : null;

  return (
    <SafeAreaView style={s.screen}>

      {/* Top bar */}
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()} style={s.backBtn}>
          <Ionicons name="arrow-back" size={22} color="#fff" />
        </TouchableOpacity>
        <Text style={s.topBarTitle}>🏃 Evacuate to Safety</Text>
        <TouchableOpacity onPress={() => { autoDrawn.current = false; setReady(false); refresh(); }} style={s.refreshBtn}>
          <Ionicons name="refresh" size={20} color={C.orange} />
        </TouchableOpacity>
      </View>

      {!ready ? (
        <View style={s.center}>
          <ActivityIndicator color={C.orange} size="large" />
          <Text style={s.loadingTitle}>Finding safest route…</Text>
          <Text style={s.loadingSub}>Checking {reports.length} hazard zones near you</Text>
        </View>
      ) : (
        <>
          {/* Active route banner */}
          {activeShelter && (
            <View style={s.routeBanner}>
              <Ionicons name="navigate" size={16} color="#fff" />
              <View style={{ flex: 1, marginLeft: 10 }}>
                <Text style={s.routeBannerTitle} numberOfLines={1}>
                  ROUTE → {activeShelter.name}
                </Text>
                <Text style={s.routeBannerSub}>
                  {activeShelter.km.toFixed(1)} km {activeShelter.dir} · {activeShelter.elevation}
                  {activeShelter.blocked ? " · ⚠ Hazard nearby" : " · ✓ Hazard-free"}
                </Text>
              </View>
              <TouchableOpacity onPress={() => openMaps(activeShelter)} style={s.navBtn}>
                <Ionicons name="navigate-circle" size={28} color={C.orange} />
              </TouchableOpacity>
            </View>
          )}

          {/* Map — always visible, route pre-drawn */}
          <View style={s.mapWrap}>
            <OSMMap
              ref={mapRef}
              userLat={userLat}
              userLon={userLon}
              reports={reports}
              height={280}
            />
          </View>

          <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

            {/* GPS status */}
            <View style={[s.gpsCard, { borderLeftColor: location?.available ? C.actionGreen : C.emergencyRed }]}>
              {location?.available ? (
                <View style={s.gpsRow}>
                  <Ionicons name="location" size={14} color={C.actionGreen} />
                  <Text style={s.gpsCoords}>
                    {location.latitude.toFixed(5)}, {location.longitude.toFixed(5)}
                  </Text>
                  <View style={s.livePill}><Text style={s.livePillText}>LIVE GPS</Text></View>
                </View>
              ) : (
                <Text style={{ color: C.emergencyRed, fontSize: 13, fontWeight: "600" }}>
                  ⚠️ GPS unavailable — showing default Mangaluru location
                </Text>
              )}
            </View>

            {/* Summary card */}
            <View style={s.summaryCard}>
              <Text style={s.summaryText}>
                ✅ {safe.length} hazard-free shelter{safe.length !== 1 ? "s" : ""} found
                {reports.length > 0 ? ` · ${reports.length} hazard zone${reports.length !== 1 ? "s" : ""} avoided` : ""}
              </Text>
              <Text style={s.summarySub}>Route auto-drawn to nearest · Tap a shelter to reroute</Text>
            </View>

            {/* Shelter list */}
            <Text style={s.sectionLabel}>🏠 SAFE SHELTERS</Text>

            {shelters.map((sh, i) => (
              <TouchableOpacity
                key={i}
                onPress={() => routeTo(i)}
                activeOpacity={0.85}
                style={[
                  s.shelterCard,
                  activeIdx === i && s.shelterCardActive,
                  sh.blocked && s.shelterCardBlocked,
                ]}
              >
                {/* Badge row */}
                <View style={s.badgeRow}>
                  {!sh.blocked && i === 0 && (
                    <View style={s.nearestBadge}><Text style={s.nearestText}>⭐ NEAREST SAFE</Text></View>
                  )}
                  {activeIdx === i && (
                    <View style={s.activeBadge}><Text style={s.activeText}>▶ ACTIVE ROUTE</Text></View>
                  )}
                  {sh.blocked && (
                    <View style={s.blockedBadge}><Text style={s.blockedText}>⚠ HAZARD NEARBY</Text></View>
                  )}
                </View>

                <View style={s.shelterHeader}>
                  <View style={{ flex: 1 }}>
                    <Text style={s.shelterName}>{sh.name}</Text>
                    <Text style={s.shelterAddr}>{sh.address}</Text>
                  </View>
                  <View style={s.elevBadge}>
                    <Text style={s.elevText}>{sh.elevation}</Text>
                  </View>
                </View>

                <View style={s.metaRow}>
                  <Text style={s.metaDist}>
                    📏 {sh.km.toFixed(1)} km {sh.dir}
                  </Text>
                  <Text style={s.metaCap}>👥 {sh.occupancy}</Text>
                </View>

                {/* Amenities */}
                <View style={s.amenitiesRow}>
                  {sh.amenities.map((a) => (
                    <View key={a} style={s.amenityChip}>
                      <Text style={s.amenityText}>{a}</Text>
                    </View>
                  ))}
                </View>

                {/* Actions */}
                <View style={s.btnRow}>
                  <TouchableOpacity
                    onPress={() => routeTo(i)}
                    style={[s.routeBtn, activeIdx === i && { backgroundColor: "#1D4ED8" }]}
                  >
                    <Ionicons name="map" size={13} color="#fff" />
                    <Text style={s.routeBtnText}>
                      {activeIdx === i ? "✓ Routing" : "Show Route"}
                    </Text>
                  </TouchableOpacity>
                  <TouchableOpacity onPress={() => openMaps(sh)} style={s.mapsBtn}>
                    <Ionicons name="navigate" size={13} color="#fff" />
                    <Text style={s.mapsBtnText}>Open Maps</Text>
                  </TouchableOpacity>
                </View>
              </TouchableOpacity>
            ))}

            <View style={{ height: 40 }} />
          </ScrollView>
        </>
      )}
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:      { flex: 1, backgroundColor: C.bg },

  // Top bar
  topBar:      { backgroundColor: "#0F172A", flexDirection: "row", alignItems: "center", paddingHorizontal: 14, paddingVertical: 12 },
  backBtn:     { marginRight: 10 },
  topBarTitle: { flex: 1, color: "#fff", fontSize: 16, fontWeight: "800" },
  refreshBtn:  { padding: 4 },

  // Loading
  center:       { flex: 1, justifyContent: "center", alignItems: "center", padding: 24, gap: 10 },
  loadingTitle: { color: C.textPrimary, fontSize: 16, fontWeight: "700" },
  loadingSub:   { color: C.textSecondary, fontSize: 13, textAlign: "center" },

  // Route banner
  routeBanner:      { backgroundColor: "#1D4ED8", flexDirection: "row", alignItems: "center", paddingHorizontal: 14, paddingVertical: 10 },
  routeBannerTitle: { color: "#fff", fontWeight: "800", fontSize: 13, letterSpacing: 0.3 },
  routeBannerSub:   { color: "rgba(255,255,255,0.75)", fontSize: 11, marginTop: 2 },
  navBtn:           { padding: 4 },

  // Map
  mapWrap:     { borderBottomWidth: 1, borderBottomColor: C.divider },

  scroll:      { padding: 14 },

  // GPS
  gpsCard:     { backgroundColor: "#F0FDF4", borderRadius: 10, padding: 10, marginBottom: 10, borderLeftWidth: 4, borderWidth: 1, borderColor: "#A7F3D0" },
  gpsRow:      { flexDirection: "row", alignItems: "center", gap: 6 },
  gpsCoords:   { flex: 1, color: "#166534", fontSize: 12, fontWeight: "600" },
  livePill:    { backgroundColor: "#16A34A", paddingHorizontal: 7, paddingVertical: 2, borderRadius: 5 },
  livePillText:{ color: "#fff", fontSize: 9, fontWeight: "700" },

  // Summary
  summaryCard: { backgroundColor: "#FFF7ED", borderRadius: 10, padding: 12, marginBottom: 12, borderWidth: 1, borderColor: C.orange },
  summaryText: { color: C.orange, fontWeight: "800", fontSize: 13 },
  summarySub:  { color: C.textSecondary, fontSize: 11, marginTop: 3 },

  sectionLabel:{ color: "#334155", fontSize: 11, fontWeight: "800", letterSpacing: 0.8, marginBottom: 10 },

  // Shelter cards
  shelterCard:        { backgroundColor: "#fff", borderRadius: 14, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider, elevation: 2 },
  shelterCardActive:  { borderColor: "#1D4ED8", borderWidth: 2, backgroundColor: "#EFF6FF" },
  shelterCardBlocked: { borderColor: "#F59E0B", backgroundColor: "#FFFBEB" },

  badgeRow:     { flexDirection: "row", gap: 6, marginBottom: 8, flexWrap: "wrap" },
  nearestBadge: { backgroundColor: "#FFF7ED", borderRadius: 5, paddingHorizontal: 8, paddingVertical: 3 },
  nearestText:  { color: C.orange, fontSize: 10, fontWeight: "700" },
  activeBadge:  { backgroundColor: "#DBEAFE", borderRadius: 5, paddingHorizontal: 8, paddingVertical: 3 },
  activeText:   { color: "#1D4ED8", fontSize: 10, fontWeight: "700" },
  blockedBadge: { backgroundColor: "#FEF3C7", borderRadius: 5, paddingHorizontal: 8, paddingVertical: 3 },
  blockedText:  { color: "#92400E", fontSize: 10, fontWeight: "700" },

  shelterHeader:{ flexDirection: "row", alignItems: "flex-start", gap: 8, marginBottom: 6 },
  shelterName:  { color: "#0F172A", fontSize: 14, fontWeight: "800", lineHeight: 20 },
  shelterAddr:  { color: C.textSecondary, fontSize: 11, marginTop: 2, lineHeight: 16 },
  elevBadge:    { backgroundColor: "#EFF6FF", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  elevText:     { color: "#1D4ED8", fontSize: 11, fontWeight: "700" },

  metaRow:      { flexDirection: "row", justifyContent: "space-between", marginBottom: 8 },
  metaDist:     { color: C.orange, fontSize: 13, fontWeight: "700" },
  metaCap:      { color: C.textSecondary, fontSize: 12 },

  amenitiesRow: { flexDirection: "row", flexWrap: "wrap", gap: 5, marginBottom: 10 },
  amenityChip:  { backgroundColor: C.surface, borderRadius: 5, paddingHorizontal: 8, paddingVertical: 4, borderWidth: 1, borderColor: C.divider },
  amenityText:  { color: C.textSecondary, fontSize: 10, fontWeight: "500" },

  btnRow:       { flexDirection: "row", gap: 8 },
  routeBtn:     { flex: 1, backgroundColor: "#1E293B", borderRadius: 8, paddingVertical: 10, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 5 },
  routeBtnText: { color: "#fff", fontSize: 12, fontWeight: "700" },
  mapsBtn:      { flex: 1, backgroundColor: C.orange, borderRadius: 8, paddingVertical: 10, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 5 },
  mapsBtnText:  { color: "#fff", fontSize: 12, fontWeight: "700" },
});
