import React, { useState, useEffect } from "react";
import { View, Text, ScrollView, TouchableOpacity, Linking, Image } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter, useLocalSearchParams } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import PriorityBadge from "../src/components/PriorityBadge";
import StatusBadge from "../src/components/StatusBadge";
import ConfirmModal from "../src/components/ConfirmModal";
import { getEmergencyById, updateEmergencyStatus } from "../src/services/storage";
import type { EmergencyRequest } from "../src/types/emergency";
import { EMERGENCY_TYPE_EMOJI, EMERGENCY_TYPE_LABELS, WATER_DEPTH_LABELS, MEDICAL_TYPE_LABELS } from "../src/types/emergency";
import { C } from "../src/theme/colors";

export default function EmergencyDetailScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const [request, setRequest] = useState<EmergencyRequest | null>(null);
  const [showCancel, setShowCancel] = useState(false);

  useEffect(() => { if (id) getEmergencyById(id).then(setRequest); }, [id]);

  const handleCancel = async () => {
    if (!request) return;
    await updateEmergencyStatus(request.emergencyRequestId, "CANCELLED");
    setRequest({ ...request, status: "CANCELLED" });
  };

  if (!request) {
    return <SafeAreaView style={{ flex: 1, backgroundColor: C.bg, justifyContent: "center", alignItems: "center" }}><Text style={{ color: C.textSecondary }}>Loading…</Text></SafeAreaView>;
  }

  const isTerminal = ["RESOLVED","CANCELLED","FALSE_REPORT"].includes(request.status);

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: C.bg }}>
      <View style={{ flexDirection: "row", alignItems: "center", padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider }}>
        <TouchableOpacity onPress={() => router.back()} style={{ marginRight: 12 }}>
          <Ionicons name="arrow-back" size={24} color={C.textPrimary} />
        </TouchableOpacity>
        <Text style={{ color: C.emergencyRed, fontSize: 18, fontWeight: "700" }}>Emergency Details</Text>
      </View>

      <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
          <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
            <Text style={{ fontSize: 24 }}>{EMERGENCY_TYPE_EMOJI[request.emergencyType]}</Text>
            <Text style={{ color: C.textPrimary, fontSize: 18, fontWeight: "700" }}>{EMERGENCY_TYPE_LABELS[request.emergencyType]}</Text>
          </View>
          <PriorityBadge priority={request.priority} />
        </View>
        <View style={{ marginBottom: 16 }}><StatusBadge status={request.status} /></View>

        <InfoCard title="Emergency Info">
          <Row label="Emergency ID" value={request.emergencyRequestId} />
          <Row label="Timestamp" value={new Date(request.timestamp).toLocaleString("en-IN")} />
          <Row label="Latitude" value={request.latitude.toFixed(6)} />
          <Row label="Longitude" value={request.longitude.toFixed(6)} />
          <Row label="Accuracy" value={`${Math.round(request.gpsAccuracy)} m`} />
          <Row label="People" value={request.numberOfPeople.toString()} />
          {request.buildingFloor ? <Row label="Building/Floor" value={request.buildingFloor} /> : null}
          {request.contactNumber ? <Row label="Contact" value={request.contactNumber} /> : null}
        </InfoCard>

        {request.waterDepth && (
          <InfoCard title="Water Conditions">
            <Row label="Water Depth" value={WATER_DEPTH_LABELS[request.waterDepth]} />
            {request.waterRising !== undefined && <Row label="Water Rising" value={request.waterRising ? "Yes" : "No"} />}
            <Row label="People Trapped" value={request.peopleTrapped ? "Yes" : "No"} />
          </InfoCard>
        )}

        {request.medicalEmergencyType && (
          <InfoCard title="Medical Details">
            <Row label="Medical Type" value={MEDICAL_TYPE_LABELS[request.medicalEmergencyType]} />
            {request.personUnconscious && <Row label="Unconscious" value="Yes" />}
            {request.breathingProblem && <Row label="Breathing Problem" value="Yes" />}
            {request.severeBleeding && <Row label="Severe Bleeding" value="Yes" />}
          </InfoCard>
        )}

        {(request.children || request.elderly || request.disabled || request.injured) && (
          <InfoCard title="Vulnerable / Injured">
            <Row label="Affected" value={[request.children && "Children", request.elderly && "Elderly", request.disabled && "Disabled", request.injured && "Injured"].filter(Boolean).join(", ")} />
          </InfoCard>
        )}

        {request.description ? <InfoCard title="Description"><Text style={{ color: C.textPrimary, fontSize: 14, lineHeight: 20 }}>{request.description}</Text></InfoCard> : null}

        {request.photoUri && (
          <InfoCard title="Photo">
            <Image source={{ uri: request.photoUri }} style={{ width: "100%", height: 200, borderRadius: 8 }} resizeMode="cover" />
          </InfoCard>
        )}

        {request.escalationRequired && (
          <View style={{ backgroundColor: C.emergencyBg, borderRadius: 10, padding: 12, marginTop: 8, borderWidth: 1, borderColor: C.emergencyRed }}>
            <Text style={{ color: C.emergencyRed, fontSize: 13 }}>🚨 Escalation required — flagged for priority response.</Text>
          </View>
        )}

        <View style={{ marginTop: 20, gap: 10 }}>
          {request.assignedResponderId && request.responderContact && (
            <TouchableOpacity onPress={() => Linking.openURL(`tel:${request.responderContact}`)} style={{ backgroundColor: C.orange, borderRadius: 12, padding: 16, flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8 }}>
              <Ionicons name="call" size={18} color="#fff" />
              <Text style={{ color: "#fff", fontWeight: "700" }}>CALL RESPONDER</Text>
            </TouchableOpacity>
          )}
          {!isTerminal && (
            <TouchableOpacity onPress={() => setShowCancel(true)} style={{ borderWidth: 1.5, borderColor: C.emergencyRed, borderRadius: 12, padding: 16, alignItems: "center" }}>
              <Text style={{ color: C.emergencyRed, fontWeight: "600" }}>CANCEL REQUEST</Text>
            </TouchableOpacity>
          )}
        </View>
        <View style={{ height: 24 }} />
      </ScrollView>

      <ConfirmModal visible={showCancel} title="Cancel Emergency?" message="This will mark the request as cancelled. Are you sure?" confirmText="Yes, Cancel" confirmColor={C.emergencyRed} onConfirm={() => { setShowCancel(false); handleCancel(); }} onCancel={() => setShowCancel(false)} />
    </SafeAreaView>
  );
}

function InfoCard({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={{ backgroundColor: C.surface, borderRadius: 12, padding: 14, marginBottom: 12, borderWidth: 1, borderColor: C.divider }}>
      <Text style={{ color: C.orange, fontSize: 11, fontWeight: "700", letterSpacing: 0.8, textTransform: "uppercase", marginBottom: 10 }}>{title}</Text>
      {children}
    </View>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 6 }}>
      <Text style={{ color: C.textSecondary, fontSize: 13, flex: 0.45 }}>{label}</Text>
      <Text style={{ color: C.textPrimary, fontSize: 13, fontWeight: "500", flex: 0.55 }}>{value}</Text>
    </View>
  );
}
