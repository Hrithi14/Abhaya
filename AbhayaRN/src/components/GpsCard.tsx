import React from "react";
import { View, Text, ActivityIndicator } from "react-native";
import { C } from "../theme/colors";

interface LocationData {
  latitude: number;
  longitude: number;
  accuracy: number;
  available: boolean;
}

export default function GpsCard({ location, isLoading }: { location: LocationData | null; isLoading: boolean }) {
  return (
    <View style={{ backgroundColor: C.surface, borderRadius: 12, padding: 14, borderWidth: 1.5, borderColor: C.orange }}>
      {isLoading ? (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
          <ActivityIndicator color={C.orange} size="small" />
          <Text style={{ color: C.orange, fontSize: 13, fontWeight: "600" }}>Acquiring GPS…</Text>
        </View>
      ) : !location?.available ? (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
          <Text style={{ fontSize: 18 }}>📍</Text>
          <View>
            <Text style={{ color: C.emergencyRed, fontSize: 12, fontWeight: "700" }}>GPS UNAVAILABLE</Text>
            <Text style={{ color: C.textSecondary, fontSize: 11 }}>Enable location services</Text>
          </View>
        </View>
      ) : (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 10 }}>
          <Text style={{ fontSize: 18 }}>📍</Text>
          <View style={{ flex: 1 }}>
            <View style={{ flexDirection: "row", gap: 16 }}>
              <View>
                <Text style={{ color: C.textSecondary, fontSize: 10, letterSpacing: 0.5 }}>LAT</Text>
                <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "700" }}>{location.latitude.toFixed(6)}</Text>
              </View>
              <View>
                <Text style={{ color: C.textSecondary, fontSize: 10, letterSpacing: 0.5 }}>LNG</Text>
                <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "700" }}>{location.longitude.toFixed(6)}</Text>
              </View>
              <View>
                <Text style={{ color: C.textSecondary, fontSize: 10, letterSpacing: 0.5 }}>ACCURACY</Text>
                <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "700" }}>{Math.round(location.accuracy)} m</Text>
              </View>
            </View>
          </View>
          <View style={{ backgroundColor: C.orangeBg, paddingHorizontal: 8, paddingVertical: 3, borderRadius: 4, borderWidth: 1, borderColor: C.orange }}>
            <Text style={{ color: C.orange, fontSize: 10, fontWeight: "700" }}>GPS ●</Text>
          </View>
        </View>
      )}
    </View>
  );
}
