import React from "react";
import { Modal, View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { C } from "../theme/colors";

interface Props {
  visible: boolean; title: string; message: string;
  confirmText: string; confirmColor?: string;
  onConfirm: () => void; onCancel: () => void;
}

export default function ConfirmModal({ visible, title, message, confirmText, confirmColor = C.orange, onConfirm, onCancel }: Props) {
  return (
    <Modal visible={visible} transparent animationType="fade">
      <View style={s.overlay}>
        <View style={s.card}>
          <Text style={s.title}>{title}</Text>
          <Text style={s.message}>{message}</Text>
          <View style={s.buttons}>
            <TouchableOpacity onPress={onCancel} style={s.cancelBtn}>
              <Text style={{ color: C.textSecondary, fontWeight: "600" }}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={onConfirm} style={[s.confirmBtn, { backgroundColor: confirmColor }]}>
              <Text style={{ color: "#fff", fontWeight: "700" }}>{confirmText}</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const s = StyleSheet.create({
  overlay:    { flex: 1, backgroundColor: "rgba(0,0,0,0.45)", justifyContent: "center", alignItems: "center", padding: 24 },
  card:       { backgroundColor: C.white, borderRadius: 16, padding: 24, width: "100%", shadowColor: "#000", shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.15, shadowRadius: 20, elevation: 10 },
  title:      { color: C.textPrimary, fontSize: 17, fontWeight: "700", marginBottom: 10 },
  message:    { color: C.textSecondary, fontSize: 14, lineHeight: 20, marginBottom: 20 },
  buttons:    { flexDirection: "row", gap: 12, justifyContent: "flex-end" },
  cancelBtn:  { paddingVertical: 10, paddingHorizontal: 16 },
  confirmBtn: { paddingVertical: 10, paddingHorizontal: 20, borderRadius: 8 },
});
