import React from "react";
import { View, Text } from "react-native";
import type { EmergencyStatus } from "../types/emergency";
import { STATUS_LABELS } from "../types/emergency";
import { C } from "../theme/colors";

const COLORS: Record<EmergencyStatus, string> = {
  ACTIVE:             C.emergencyRed,
  ACKNOWLEDGED:       C.high,
  RESCUE_IN_PROGRESS: C.orange,
  RESOLVED:           C.actionGreen,
  CANCELLED:          C.textDisabled,
  FALSE_REPORT:       C.textDisabled,
};

export default function StatusBadge({ status }: { status: EmergencyStatus }) {
  return (
    <View style={{ backgroundColor: COLORS[status], paddingHorizontal: 8, paddingVertical: 3, borderRadius: 4 }}>
      <Text style={{ color: "#fff", fontSize: 10, fontWeight: "700", letterSpacing: 0.8 }}>
        {STATUS_LABELS[status].toUpperCase()}
      </Text>
    </View>
  );
}
