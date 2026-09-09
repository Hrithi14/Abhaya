import React from "react";
import { View, Text, TouchableOpacity, ScrollView } from "react-native";
import type { EmergencyRequest } from "../types/emergency";
import { EMERGENCY_TYPE_EMOJI, EMERGENCY_TYPE_LABELS } from "../types/emergency";
import PriorityBadge from "./PriorityBadge";
import StatusBadge from "./StatusBadge";
import { C } from "../theme/colors";

export default function SuccessScreen({ request, onViewRequests, onGoHome }: { request: EmergencyRequest; onViewRequests: () => void; onGoHome: () => void }) {
  return (
    <ScrollView style={{ flex: 1, backgroundColor: C.bg }} contentContainerStyle={{ padding: 24, alignItems: "center" }}>
      <Text style={{ fontSize: 56, marginTop: 40 }}>✅</Text>
      <Text style={{ color: C.actionGreen, fontSize: 22, fontWeight: "900", marginTop: 12 }}>Request Submitted</Text>
      <Text style={{ color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 4 }}>Your emergency request has been recorded</Text>

      <View style={{ backgroundColor: C.surface, borderRadius: 14, padding: 20, width: "100%", marginTop: 24, borderWidth: 1.5, borderColor: C.divider }}>
        {[
          ["Emergency ID", request.emergencyRequestId],
          ["Type", `${EMERGENCY_TYPE_EMOJI[request.emergencyType]} ${EMERGENCY_TYPE_LABELS[request.emergencyType]}`],
          ["Time", new Date(request.timestamp).toLocaleString("en-IN")],
          ["Location", request.latitude !== 0 ? `${request.latitude.toFixed(5)}, ${request.longitude.toFixed(5)}` : "GPS Unavailable"],
        ].map(([label, value]) => (
          <View key={label} style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 10 }}>
            <Text style={{ color: C.textSecondary, fontSize: 13, flex: 0.4 }}>{label}</Text>
            <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "600", flex: 0.6 }}>{value}</Text>
          </View>
        ))}
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
          <Text style={{ color: C.textSecondary, fontSize: 13 }}>Priority</Text>
          <PriorityBadge priority={request.priority} />
        </View>
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center" }}>
          <Text style={{ color: C.textSecondary, fontSize: 13 }}>Status</Text>
          <StatusBadge status={request.status} />
        </View>
      </View>

      <Text style={{ color: C.textSecondary, fontSize: 12, textAlign: "center", marginTop: 12, lineHeight: 18 }}>
        To call emergency services directly, use CALL 112 from the home screen.
      </Text>

      <TouchableOpacity onPress={onViewRequests} style={{ backgroundColor: C.emergencyRed, borderRadius: 12, padding: 16, width: "100%", alignItems: "center", marginTop: 24 }}>
        <Text style={{ color: "#fff", fontWeight: "700", fontSize: 15 }}>VIEW MY EMERGENCIES</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={onGoHome} style={{ borderWidth: 1.5, borderColor: C.orange, borderRadius: 12, padding: 16, width: "100%", alignItems: "center", marginTop: 10 }}>
        <Text style={{ color: C.orange, fontWeight: "600", fontSize: 15 }}>GO HOME</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
