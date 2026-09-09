import React from "react";
import { TouchableOpacity, Text } from "react-native";
import { C } from "../theme/colors";

export default function ToggleChip({ label, selected, onToggle }: { label: string; selected: boolean; onToggle: (v: boolean) => void }) {
  return (
    <TouchableOpacity
      onPress={() => onToggle(!selected)}
      style={{ flex: 1, backgroundColor: selected ? C.orange : C.surface, borderWidth: 1.5, borderColor: selected ? C.orange : C.divider, borderRadius: 8, paddingVertical: 9, paddingHorizontal: 6, alignItems: "center", justifyContent: "center" }}
      activeOpacity={0.8}
    >
      <Text style={{ color: selected ? "#fff" : C.textSecondary, fontSize: 12, fontWeight: "600" }}>
        {selected ? "✓ " : ""}{label}
      </Text>
    </TouchableOpacity>
  );
}
