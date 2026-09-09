import React, { useState, useCallback } from "react";
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter, useFocusEffect } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import EmergencyCard from "../src/components/EmergencyCard";
import { getActiveEmergencies } from "../src/services/storage";
import type { EmergencyRequest } from "../src/types/emergency";
import { C } from "../src/theme/colors";

const USER_ID = "user-demo-001";

export default function MyRequestsScreen() {
  const router = useRouter();
  const [requests, setRequests] = useState<EmergencyRequest[]>([]);
  const [loading, setLoading] = useState(true);

  useFocusEffect(useCallback(() => {
    setLoading(true);
    getActiveEmergencies(USER_ID).then((d) => { setRequests(d); setLoading(false); });
  }, []));

  return (
    <SafeAreaView style={s.screen}>
      <View style={s.topBar}>
        <TouchableOpacity onPress={() => router.back()}><Ionicons name="arrow-back" size={24} color={C.textPrimary} /></TouchableOpacity>
        <Text style={s.topBarTitle}>My Active Emergencies</Text>
      </View>
      {loading ? (
        <View style={s.center}><ActivityIndicator color={C.orange} size="large" /></View>
      ) : requests.length === 0 ? (
        <View style={s.center}>
          <Text style={{ fontSize: 48 }}>✅</Text>
          <Text style={s.emptyTitle}>No Active Emergencies</Text>
          <Text style={s.emptySub}>You have no active requests right now.</Text>
        </View>
      ) : (
        <FlatList
          data={requests}
          keyExtractor={(i) => i.emergencyRequestId}
          contentContainerStyle={{ padding: 16 }}
          renderItem={({ item }) => (
            <EmergencyCard request={item} onPress={() => router.push({ pathname: "/emergency-detail", params: { id: item.emergencyRequestId } })} />
          )}
        />
      )}
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  screen: { flex: 1, backgroundColor: C.bg },
  topBar: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider },
  topBarTitle: { color: C.emergencyRed, fontSize: 18, fontWeight: "700" },
  center: { flex: 1, justifyContent: "center", alignItems: "center", padding: 32 },
  emptyTitle: { color: C.textPrimary, fontSize: 18, fontWeight: "700", marginTop: 12 },
  emptySub: { color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 6 },
});
