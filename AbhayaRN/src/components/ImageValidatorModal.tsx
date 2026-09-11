/**
 * ImageValidatorModal
 * Shows while Gemini Vision is analysing the photo,
 * then shows VALID (green) or INVALID (red) result with reason.
 */
import React from "react";
import {
  Modal, View, Text, TouchableOpacity,
  ActivityIndicator, StyleSheet, Image,
} from "react-native";
import { C } from "../theme/colors";

export type ValidatorState =
  | "idle"
  | "checking"
  | "valid"
  | "invalid";

interface Props {
  state: ValidatorState;
  imageUri?: string;
  validLabel?: string;       // e.g. "Flood water"
  validConfidence?: string;  // e.g. "HIGH"
  invalidReason?: string;
  onAccept: () => void;      // user confirms after VALID
  onRetry: () => void;       // user picks another photo after INVALID
  onCancel: () => void;      // dismiss
}

export default function ImageValidatorModal({
  state, imageUri,
  validLabel, validConfidence, invalidReason,
  onAccept, onRetry, onCancel,
}: Props) {
  const visible = state !== "idle";

  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onCancel}>
      <View style={s.overlay}>
        <View style={s.card}>

          {/* Preview thumbnail */}
          {imageUri && (
            <Image source={{ uri: imageUri }} style={s.thumbnail} resizeMode="cover" />
          )}

          {/* ── Checking ── */}
          {state === "checking" && (
            <View style={s.body}>
              <ActivityIndicator size="large" color={C.orange} />
              <Text style={s.checkingTitle}>Analysing Photo…</Text>
              <Text style={s.checkingSub}>
                AI is checking if this photo shows a real disaster or hazard scene.
              </Text>
              <View style={s.techBadge}>
                <Text style={s.techText}>🤖 Gemini Vision API</Text>
              </View>
            </View>
          )}

          {/* ── Valid ── */}
          {state === "valid" && (
            <View style={s.body}>
              <Text style={s.resultIcon}>✅</Text>
              <Text style={[s.resultTitle, { color: "#16A34A" }]}>Photo Verified</Text>
              <View style={s.resultCard}>
                <Row label="Detected Hazard" value={validLabel ?? "Hazard scene"} />
                <Row label="AI Confidence"   value={validConfidence ?? "HIGH"} />
                <Row label="Status"          value="Approved for submission" />
              </View>
              <Text style={s.resultSub}>
                This photo has been verified as a genuine hazard scene by AI.
              </Text>
              <TouchableOpacity onPress={onAccept} style={[s.btn, { backgroundColor: "#16A34A" }]}>
                <Text style={s.btnText}>✓  USE THIS PHOTO</Text>
              </TouchableOpacity>
            </View>
          )}

          {/* ── Invalid ── */}
          {state === "invalid" && (
            <View style={s.body}>
              <Text style={s.resultIcon}>❌</Text>
              <Text style={[s.resultTitle, { color: C.emergencyRed }]}>Photo Rejected</Text>
              <View style={[s.resultCard, { borderColor: C.emergencyRed }]}>
                <Text style={s.invalidReason}>{invalidReason}</Text>
              </View>
              <Text style={s.resultSub}>
                Please upload a photo that clearly shows a flood, fallen tree, fire, pothole, or other disaster hazard.
              </Text>
              <TouchableOpacity onPress={onRetry} style={[s.btn, { backgroundColor: C.orange }]}>
                <Text style={s.btnText}>📷  TRY ANOTHER PHOTO</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={onCancel} style={s.cancelBtn}>
                <Text style={s.cancelText}>Continue without photo</Text>
              </TouchableOpacity>
            </View>
          )}

        </View>
      </View>
    </Modal>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 6 }}>
      <Text style={{ color: "#64748B", fontSize: 12, flex: 0.45 }}>{label}</Text>
      <Text style={{ color: "#0F172A", fontSize: 12, fontWeight: "700", flex: 0.55 }}>{value}</Text>
    </View>
  );
}

const s = StyleSheet.create({
  overlay:    { flex: 1, backgroundColor: "rgba(0,0,0,0.65)", justifyContent: "center", alignItems: "center", padding: 20 },
  card:       { backgroundColor: "#fff", borderRadius: 20, width: "100%", overflow: "hidden", shadowColor: "#000", shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.2, shadowRadius: 20, elevation: 12 },
  thumbnail:  { width: "100%", height: 180 },
  body:       { padding: 20, alignItems: "center" },

  // Checking
  checkingTitle:{ color: "#0F172A", fontSize: 17, fontWeight: "800", marginTop: 14, marginBottom: 6 },
  checkingSub:  { color: "#64748B", fontSize: 13, textAlign: "center", lineHeight: 19, marginBottom: 14 },
  techBadge:    { backgroundColor: "#F0F9FF", borderRadius: 8, paddingHorizontal: 12, paddingVertical: 5, borderWidth: 1, borderColor: "#BAE6FD" },
  techText:     { color: "#0369A1", fontSize: 11, fontWeight: "700" },

  // Result shared
  resultIcon:  { fontSize: 44, marginBottom: 8 },
  resultTitle: { fontSize: 20, fontWeight: "900", marginBottom: 12 },
  resultCard:  { backgroundColor: "#F8FAFC", borderRadius: 10, padding: 14, width: "100%", marginBottom: 10, borderWidth: 1, borderColor: "#E2E8F0" },
  resultSub:   { color: "#64748B", fontSize: 12, textAlign: "center", lineHeight: 18, marginBottom: 16 },
  invalidReason:{ color: "#DC2626", fontSize: 13, lineHeight: 19, textAlign: "center" },

  // Buttons
  btn:        { width: "100%", borderRadius: 12, padding: 14, alignItems: "center", marginBottom: 8 },
  btnText:    { color: "#fff", fontWeight: "800", fontSize: 14, letterSpacing: 0.4 },
  cancelBtn:  { paddingVertical: 10 },
  cancelText: { color: "#94A3B8", fontSize: 13 },
});
