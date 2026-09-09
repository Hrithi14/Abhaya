import React from "react";
import { View, Text, TextInput, TextInputProps } from "react-native";
import { C } from "../theme/colors";

interface Props extends TextInputProps { label: string; error?: string; }

export default function FormField({ label, error, ...props }: Props) {
  return (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ color: C.textSecondary, fontSize: 12, marginBottom: 4, fontWeight: "500" }}>{label}</Text>
      <TextInput
        {...props}
        placeholderTextColor={C.textDisabled}
        style={{ backgroundColor: C.surface, borderWidth: 1.5, borderColor: error ? C.emergencyRed : C.divider, borderRadius: 10, paddingHorizontal: 14, paddingVertical: 12, color: C.textPrimary, fontSize: 14, ...(props.style as object) }}
      />
      {error ? <Text style={{ color: C.emergencyRed, fontSize: 11, marginTop: 3 }}>{error}</Text> : null}
    </View>
  );
}
