import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import { C } from "../theme/colors";

export default function NumberStepper({ label = "Number of People", value, onChange, min = 1, max = 50 }: { label?: string; value: number; onChange: (v: number) => void; min?: number; max?: number }) {
  return (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ color: C.textSecondary, fontSize: 12, marginBottom: 6, fontWeight: "500" }}>{label}</Text>
      <View style={{ flexDirection: "row", alignItems: "center", gap: 16 }}>
        <TouchableOpacity onPress={() => value > min && onChange(value - 1)} style={{ width: 40, height: 40, backgroundColor: C.surface, borderWidth: 1.5, borderColor: C.orange, borderRadius: 8, alignItems: "center", justifyContent: "center" }}>
          <Text style={{ color: C.orange, fontSize: 20, fontWeight: "700" }}>−</Text>
        </TouchableOpacity>
        <Text style={{ color: C.textPrimary, fontSize: 22, fontWeight: "700", minWidth: 36, textAlign: "center" }}>{value}</Text>
        <TouchableOpacity onPress={() => value < max && onChange(value + 1)} style={{ width: 40, height: 40, backgroundColor: C.surface, borderWidth: 1.5, borderColor: C.orange, borderRadius: 8, alignItems: "center", justifyContent: "center" }}>
          <Text style={{ color: C.orange, fontSize: 20, fontWeight: "700" }}>+</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}
