import React from "react";
import { Text } from "react-native";
import { C } from "../theme/colors";

export default function SectionHeader({ title }: { title: string }) {
  return (
    <Text style={{ color: C.emergencyRed, fontSize: 11, fontWeight: "700", letterSpacing: 1.2, textTransform: "uppercase", marginTop: 16, marginBottom: 8 }}>
      {title}
    </Text>
  );
}
