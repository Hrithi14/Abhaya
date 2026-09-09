import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import type { EmergencyRequest } from "../types/emergency";
import { EMERGENCY_TYPE_EMOJI, EMERGENCY_TYPE_LABELS } from "../types/emergency";
import PriorityBadge from "./PriorityBadge";
import StatusBadge from "./StatusBadge";
import { C } from "../theme/colors";

export default function EmergencyCard({ request, onPress }: { request: EmergencyRequest; onPress: () => void }) {
  const date = new Date(request.timestamp).toLocaleDateString("en-IN", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" });
  return (
    <TouchableOpacity
      onPress={onPress}
      style={{ backgroundColor: C.surface, borderRadius: 12, padding: 16, marginBottom: 10, borderWidth: 1.5, borderColor: C.divider, shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 6, elevation: 2 }}
      activeOpacity={0.8}
    >
      <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center" }}>
        <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
          <Text style={{ fontSize: 20 }}>{EMERGENCY_TYPE_EMOJI[request.emergencyType]}</Text>
          <Text style={{ color: C.textPrimary, fontWeight: "700", fontSize: 15 }}>{EMERGENCY_TYPE_LABELS[request.emergencyType]}</Text>
        </View>
        <PriorityBadge priority={request.priority} />
      </View>
      <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 8 }}>
        <Text style={{ color: C.textSecondary, fontSize: 12, fontWeight: "500" }}>{request.emergencyRequestId}</Text>
        <StatusBadge status={request.status} />
      </View>
      <Text style={{ color: C.textSecondary, fontSize: 12, marginTop: 4 }}>{date}</Text>
      {request.assignedResponderId && (
        <Text style={{ color: C.orange, fontSize: 12, fontWeight: "500", marginTop: 4 }}>👮 Responder assigned</Text>
      )}
    </TouchableOpacity>
  );
}
