import React, { useState, useEffect } from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { useLocation } from "../src/hooks/useLocation";
import { C } from "../src/theme/colors";

const ROUTE = {
  safeDestination: "Government Higher Secondary School, High Ground",
  safeRoute: "Take MG Road north → Turn right at Post Office → Continue 1.2 km to school",
  distanceKm: 1.8, estimatedTimeMinutes: 22,
  nearestHighElevation: "Hill View Park (Elevation: 48 m)",
  nearestReliefCenter: "Govt. HS School — Relief Center (0.8 km)",
  avoidedRedZones: ["River Junction RED Zone", "Market Area RED Zone"],
};

export default function EvacuateScreen() {
  const router = useRouter();
  const { location } = useLocation();
  const [loading, setLoading] = useState(true);
  useEffect(() => { const t = setTimeout(() => setLoading(false), 1200); return () => clearTimeout(t); }, []);

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: C.bg }}>
      <View style={{ flexDirection: "row", alignItems: "center", padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider }}>
        <TouchableOpacity onPress={() => router.back()} style={{ marginRight: 12 }}>
          <Ionicons name="arrow-back" size={24} color={C.textPrimary} />
        </TouchableOpacity>
        <Text style={{ fontSize: 20 }}>🏃</Text>
        <Text style={{ color: C.emergencyRed, fontSize: 18, fontWeight: "700", marginLeft: 8 }}>Evacuate to Safety</Text>
        <View style={{ flex: 1 }} />
        <TouchableOpacity onPress={() => setLoading(true)}><Ionicons name="refresh" size={22} color={C.textSecondary} /></TouchableOpacity>
      </View>

      {loading ? (
        <View style={{ flex: 1, justifyContent: "center", alignItems: "center", gap: 12 }}>
          <ActivityIndicator color={C.orange} size="large" />
          <Text style={{ color: C.textSecondary, fontSize: 14 }}>Calculating safe route…</Text>
        </View>
      ) : (
        <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
          <View style={{ backgroundColor: C.orangeBg, borderRadius: 12, padding: 14, marginBottom: 14, flexDirection: "row", alignItems: "center", gap: 10, borderWidth: 1, borderColor: C.orange }}>
            <Text style={{ fontSize: 22 }}>✅</Text>
            <View>
              <Text style={{ color: C.orange, fontWeight: "900", fontSize: 14, letterSpacing: 0.8 }}>ROUTE AVAILABLE</Text>
              <Text style={{ color: C.textSecondary, fontSize: 12 }}>Safe evacuation route identified</Text>
            </View>
          </View>

          {[
            { title: "SAFE DESTINATION", content: (
              <View>
                <Text style={{ color: C.textPrimary, fontSize: 15, fontWeight: "700" }}>{ROUTE.safeDestination}</Text>
                <View style={{ flexDirection: "row", gap: 24, marginTop: 8 }}>
                  <View><Text style={{ color: C.textSecondary, fontSize: 10, letterSpacing: 0.5 }}>DISTANCE</Text><Text style={{ color: C.textPrimary, fontSize: 16, fontWeight: "700" }}>{ROUTE.distanceKm} km</Text></View>
                  <View><Text style={{ color: C.textSecondary, fontSize: 10, letterSpacing: 0.5 }}>ETA</Text><Text style={{ color: C.textPrimary, fontSize: 16, fontWeight: "700" }}>{ROUTE.estimatedTimeMinutes} min</Text></View>
                </View>
              </View>
            )},
            { title: "ROUTE", content: <Text style={{ color: C.textPrimary, fontSize: 14, lineHeight: 22 }}>{ROUTE.safeRoute}</Text> },
            { title: "HIGH-ELEVATION SAFE LOCATION", content: <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}><Text style={{ fontSize: 20 }}>⛰️</Text><Text style={{ color: C.textPrimary, fontSize: 14 }}>{ROUTE.nearestHighElevation}</Text></View> },
            { title: "NEAREST RELIEF CENTER", content: <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}><Text style={{ fontSize: 20 }}>🏥</Text><Text style={{ color: C.textPrimary, fontSize: 14 }}>{ROUTE.nearestReliefCenter}</Text></View> },
          ].map((card) => (
            <View key={card.title} style={{ backgroundColor: C.surface, borderRadius: 12, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider }}>
              <Text style={{ color: C.orange, fontSize: 11, fontWeight: "700", letterSpacing: 0.8, textTransform: "uppercase", marginBottom: 8 }}>{card.title}</Text>
              {card.content}
            </View>
          ))}

          <View style={{ backgroundColor: C.surface, borderRadius: 12, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider }}>
            <Text style={{ color: C.orange, fontSize: 11, fontWeight: "700", letterSpacing: 0.8, textTransform: "uppercase", marginBottom: 8 }}>AVOIDED RED ZONES</Text>
            {ROUTE.avoidedRedZones.map((z, i) => (
              <View key={i} style={{ flexDirection: "row", alignItems: "center", gap: 6, marginBottom: 4 }}>
                <Text style={{ fontSize: 14 }}>🚫</Text>
                <Text style={{ color: C.emergencyRed, fontSize: 13 }}>{z}</Text>
              </View>
            ))}
          </View>

          {location?.available && (
            <View style={{ backgroundColor: C.surface, borderRadius: 12, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider }}>
              <Text style={{ color: C.orange, fontSize: 11, fontWeight: "700", letterSpacing: 0.8, marginBottom: 8 }}>YOUR CURRENT LOCATION</Text>
              <Text style={{ color: C.textPrimary, fontSize: 13 }}>{location.latitude.toFixed(6)}, {location.longitude.toFixed(6)}</Text>
              <Text style={{ color: C.textSecondary, fontSize: 12, marginTop: 2 }}>Accuracy: {Math.round(location.accuracy)} m</Text>
            </View>
          )}
          <View style={{ height: 24 }} />
        </ScrollView>
      )}
    </SafeAreaView>
  );
}
