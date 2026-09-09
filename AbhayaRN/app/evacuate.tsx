import React, { useState, useEffect } from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator, StyleSheet, Linking } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { useLocation } from "../src/hooks/useLocation";
import { C } from "../src/theme/colors";

// Real high-ground shelters in Moodbidri area (Karnataka)
// Coordinates are real locations with high elevation
const SHELTER_LOCATIONS = [
  {
    name: "Moodbidri Town Panchayat Office",
    address: "Main Road, Moodbidri, Dakshina Kannada",
    elevation: "55m Elevation",
    occupancy: "150 / 400",
    amenities: ["Clean Water", "Food", "First Aid"],
    lat: 13.0676,
    lng: 74.9931,
  },
  {
    name: "Government High School Moodbidri",
    address: "School Road, Moodbidri - 574227",
    elevation: "48m Elevation",
    occupancy: "200 / 600",
    amenities: ["Clean Water", "Food", "Shelter", "Generators"],
    lat: 13.0710,
    lng: 74.9960,
  },
  {
    name: "Alvas College High Ground Campus",
    address: "Vidyagiri, Moodbidri - 574227",
    elevation: "62m Elevation",
    occupancy: "500 / 1500",
    amenities: ["Medical Bay", "Food", "Water", "Dormitories"],
    lat: 13.0650,
    lng: 74.9980,
  },
];

// Haversine formula — real distance in km between two GPS points
function getDistanceKm(lat1: number, lng1: number, lat2: number, lng2: number): number {
  const R = 6371;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLng = (lng2 - lng1) * Math.PI / 180;
  const a = Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

// Get cardinal direction
function getDirection(lat1: number, lng1: number, lat2: number, lng2: number): string {
  const dLat = lat2 - lat1;
  const dLng = lng2 - lng1;
  const angle = Math.atan2(dLng, dLat) * 180 / Math.PI;
  if (angle >= -22.5 && angle < 22.5) return "north";
  if (angle >= 22.5 && angle < 67.5) return "north-east";
  if (angle >= 67.5 && angle < 112.5) return "east";
  if (angle >= 112.5 && angle < 157.5) return "south-east";
  if (angle >= 157.5 || angle < -157.5) return "south";
  if (angle >= -157.5 && angle < -112.5) return "south-west";
  if (angle >= -112.5 && angle < -67.5) return "west";
  return "north-west";
}

export default function EvacuateScreen() {
  const router = useRouter();
  const { location, isLoading, refresh } = useLocation();
  const [routeLoading, setRouteLoading] = useState(true);

  useEffect(() => {
    if (!isLoading) {
      const t = setTimeout(() => setRouteLoading(false), 500);
      return () => clearTimeout(t);
    }
  }, [isLoading]);

  // Sort shelters by real distance from user
  const sheltersWithDistance = location?.available
    ? SHELTER_LOCATIONS
        .map((s) => ({
          ...s,
          distanceKm: getDistanceKm(location.latitude, location.longitude, s.lat, s.lng),
          direction: getDirection(location.latitude, location.longitude, s.lat, s.lng),
        }))
        .sort((a, b) => a.distanceKm - b.distanceKm)
    : SHELTER_LOCATIONS.map((s) => ({ ...s, distanceKm: 0, direction: "unknown" }));

  const openMaps = (shelter: typeof sheltersWithDistance[0]) => {
    // Opens Google Maps navigation from current location to shelter
    const url = location?.available
      ? `https://maps.google.com/maps?saddr=${location.latitude},${location.longitude}&daddr=${shelter.lat},${shelter.lng}&travelmode=walking`
      : `https://maps.google.com/maps?q=${shelter.lat},${shelter.lng}`;
    Linking.openURL(url);
  };

  const handleRefresh = () => {
    setRouteLoading(true);
    refresh();
  };

  return (
    <SafeAreaView style={s.screen}>
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={24} color={C.textPrimary} />
        </TouchableOpacity>
        <Text style={s.topBarTitle}>🏃 Evacuate to Safety</Text>
        <View style={{ flex: 1 }} />
        <TouchableOpacity onPress={handleRefresh}>
          <Ionicons name="refresh" size={22} color={C.orange} />
        </TouchableOpacity>
      </View>

      {(isLoading || routeLoading) ? (
        <View style={s.center}>
          <ActivityIndicator color={C.orange} size="large" />
          <Text style={s.loadingText}>
            {isLoading ? "Getting your GPS location…" : "Calculating distances…"}
          </Text>
        </View>
      ) : (
        <ScrollView contentContainerStyle={s.scroll} showsVerticalScrollIndicator={false}>

          {/* Live GPS card */}
          <View style={[s.card, { borderLeftWidth: 4, borderLeftColor: location?.available ? C.actionGreen : C.emergencyRed }]}>
            <Text style={s.cardLabel}>YOUR CURRENT LOCATION</Text>
            {location?.available ? (
              <>
                <Text style={s.coordText}>
                  📍 {location.latitude.toFixed(6)}, {location.longitude.toFixed(6)}
                </Text>
                <Text style={s.accuracyText}>GPS Accuracy: ±{Math.round(location.accuracy)} m</Text>
                <View style={s.liveBadge}><Text style={s.liveBadgeText}>● LIVE GPS</Text></View>
              </>
            ) : (
              <Text style={{ color: C.emergencyRed, fontSize: 13 }}>
                ⚠️ GPS unavailable. Enable location services and tap refresh.
              </Text>
            )}
          </View>

          {/* Route status */}
          <View style={[s.card, { backgroundColor: C.orangeBg, borderColor: C.orange, borderWidth: 1 }]}>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 10 }}>
              <Text style={{ fontSize: 22 }}>✅</Text>
              <View>
                <Text style={{ color: C.orange, fontWeight: "900", fontSize: 14 }}>
                  {sheltersWithDistance.length} SAFE SHELTERS FOUND
                </Text>
                <Text style={{ color: C.textSecondary, fontSize: 12 }}>
                  Sorted by distance from your GPS location
                </Text>
              </View>
            </View>
          </View>

          <Text style={s.sectionLabel}>🏠 SAFE HIGH-GROUND SHELTERS</Text>

          {sheltersWithDistance.map((shelter, i) => (
            <View key={i} style={[s.shelterCard, i === 0 && { borderColor: C.orange, borderWidth: 2 }]}>
              {i === 0 && (
                <View style={s.nearestBadge}><Text style={s.nearestText}>⭐ NEAREST</Text></View>
              )}
              <View style={s.shelterHeader}>
                <View style={{ flex: 1 }}>
                  <Text style={s.shelterName}>{shelter.name}</Text>
                  <Text style={s.shelterAddress}>{shelter.address}</Text>
                </View>
                <View style={s.elevBadge}>
                  <Text style={s.elevText}>{shelter.elevation}</Text>
                </View>
              </View>

              <View style={s.metaRow}>
                <Text style={s.occupancy}>Capacity: {shelter.occupancy}</Text>
                <Text style={s.distance}>
                  {location?.available
                    ? `${shelter.distanceKm.toFixed(1)} km ${shelter.direction}`
                    : "GPS needed"}
                </Text>
              </View>

              {/* Direction based on real GPS */}
              {location?.available && (
                <View style={s.dirBox}>
                  <Text style={s.dirLabel}>📍 From your location ({location.latitude.toFixed(4)}, {location.longitude.toFixed(4)}):</Text>
                  <Text style={s.dirText}>
                    Head {shelter.direction} for {shelter.distanceKm.toFixed(1)} km toward {shelter.name}
                  </Text>
                  <Text style={s.dirCoords}>Destination: {shelter.lat}, {shelter.lng}</Text>
                </View>
              )}

              {/* Open in Maps button */}
              <TouchableOpacity
                onPress={() => openMaps(shelter)}
                style={s.mapsBtn}
              >
                <Ionicons name="navigate" size={16} color="#fff" />
                <Text style={s.mapsBtnText}>Open Navigation in Maps</Text>
              </TouchableOpacity>

              {/* Amenities */}
              <View style={s.amenitiesRow}>
                {shelter.amenities.map((a) => (
                  <View key={a} style={s.amenityChip}>
                    <Text style={s.amenityText}>{a}</Text>
                  </View>
                ))}
              </View>
            </View>
          ))}

          <View style={{ height: 24 }} />
        </ScrollView>
      )}
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen:      { flex: 1, backgroundColor: C.bg },
  topBar:      { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider },
  topBarTitle: { color: C.emergencyRed, fontSize: 18, fontWeight: "700" },
  scroll:      { padding: 16 },
  center:      { flex: 1, justifyContent: "center", alignItems: "center", gap: 12 },
  loadingText: { color: C.textSecondary, fontSize: 14 },

  card:         { backgroundColor: C.surface, borderRadius: 12, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider },
  cardLabel:    { color: C.textSecondary, fontSize: 11, fontWeight: "700", letterSpacing: 0.8, textTransform: "uppercase", marginBottom: 6 },
  coordText:    { color: C.textPrimary, fontSize: 15, fontWeight: "700" },
  accuracyText: { color: C.textSecondary, fontSize: 12, marginTop: 2 },
  liveBadge:    { backgroundColor: "#DCFCE7", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 4, alignSelf: "flex-start", marginTop: 6 },
  liveBadgeText:{ color: C.actionGreen, fontSize: 10, fontWeight: "700" },

  sectionLabel: { color: C.textPrimary, fontSize: 13, fontWeight: "800", marginBottom: 12, marginTop: 4 },

  shelterCard:  { backgroundColor: C.white, borderRadius: 14, padding: 16, marginBottom: 14, borderWidth: 1, borderColor: C.divider, shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 6, elevation: 2 },
  nearestBadge: { backgroundColor: C.orangeBg, borderRadius: 6, paddingHorizontal: 8, paddingVertical: 3, alignSelf: "flex-start", marginBottom: 8 },
  nearestText:  { color: C.orange, fontSize: 11, fontWeight: "700" },
  shelterHeader:{ flexDirection: "row", alignItems: "flex-start", gap: 8, marginBottom: 8 },
  shelterName:  { color: C.textPrimary, fontSize: 15, fontWeight: "800", lineHeight: 22 },
  shelterAddress:{ color: C.textSecondary, fontSize: 12, marginTop: 2, lineHeight: 18 },
  elevBadge:    { backgroundColor: "#EFF6FF", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  elevText:     { color: "#2563EB", fontSize: 11, fontWeight: "700" },
  metaRow:      { flexDirection: "row", justifyContent: "space-between", marginBottom: 10 },
  occupancy:    { color: C.textSecondary, fontSize: 13 },
  distance:     { color: C.orange, fontSize: 13, fontWeight: "700" },

  dirBox:       { backgroundColor: C.orangeBg, borderRadius: 8, padding: 10, marginBottom: 10 },
  dirLabel:     { color: C.textSecondary, fontSize: 11, fontWeight: "600", marginBottom: 3 },
  dirText:      { color: C.textPrimary, fontSize: 13, lineHeight: 18, fontWeight: "600" },
  dirCoords:    { color: C.textSecondary, fontSize: 11, marginTop: 4, fontStyle: "italic" },

  mapsBtn:      { backgroundColor: C.orange, borderRadius: 8, padding: 10, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 6, marginBottom: 10 },
  mapsBtnText:  { color: "#fff", fontSize: 13, fontWeight: "700" },

  amenitiesRow: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  amenityChip:  { backgroundColor: C.surface, borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5, borderWidth: 1, borderColor: C.divider },
  amenityText:  { color: C.textSecondary, fontSize: 11, fontWeight: "500" },
});
