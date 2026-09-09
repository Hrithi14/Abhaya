import React from "react";
import { View, Text } from "react-native";
import type { EmergencyPriority } from "../types/emergency";
import { C } from "../theme/colors";

const COLORS: Record<EmergencyPriority, { bg: string; text: string }> = {
  CRITICAL: { bg: C.critical,  text: "#fff" },
  HIGH:     { bg: C.high,      text: "#fff" },
  MEDIUM:   { bg: C.medium,    text: "#fff" },
  LOW:      { bg: C.low,       text: "#fff" },
};

export default function PriorityBadge({ priority }: { priority: EmergencyPriority }) {
  const { bg, text } = COLORS[priority];
  return (
    <View style={{ backgroundColor: bg, paddingHorizontal: 8, paddingVertical: 3, borderRadius: 4 }}>
      <Text style={{ color: text, fontSize: 10, fontWeight: "700", letterSpacing: 0.8 }}>{priority}</Text>
    </View>
  );
}
