import React from "react";
import { View, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { C } from "../../src/theme/colors";

export default function LiveMapScreen() {
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: C.bg, justifyContent: "center", alignItems: "center" }}>
      <Text style={{ fontSize: 48 }}>🗺️</Text>
      <Text style={{ color: C.textPrimary, fontSize: 18, fontWeight: "700", marginTop: 12 }}>Live Map</Text>
      <Text style={{ color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 6, paddingHorizontal: 32 }}>
        This module is being developed by a teammate.{"\n"}Integration coming soon.
      </Text>
    </SafeAreaView>
  );
}
