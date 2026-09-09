import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter, useLocalSearchParams } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import FormField from "../src/components/FormField";
import SectionHeader from "../src/components/SectionHeader";
import ToggleChip from "../src/components/ToggleChip";
import { saveHelpOffer } from "../src/services/storage";
import { generateHelpOfferId } from "../src/services/idGenerator";
import type { HelpType } from "../src/types/emergency";
import { HELP_TYPE_LABELS } from "../src/types/emergency";
import { C } from "../src/theme/colors";

const USER_ID = "user-demo-001";
const HELP_TYPES = Object.keys(HELP_TYPE_LABELS) as HelpType[];

export default function OfferHelpScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const [helpType, setHelpType] = useState<HelpType>("OTHER");
  const [message, setMessage] = useState("");
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async () => {
    await saveHelpOffer({ helpOfferId: generateHelpOfferId(), emergencyRequestId: id ?? "", userId: USER_ID, helpType, message, timestamp: Date.now(), status: "OFFERED" });
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: C.bg, justifyContent: "center", alignItems: "center", padding: 32 }}>
        <Text style={{ fontSize: 56 }}>✅</Text>
        <Text style={{ color: C.actionGreen, fontSize: 22, fontWeight: "900", marginTop: 16 }}>Help Offered!</Text>
        <Text style={{ color: C.textSecondary, fontSize: 14, textAlign: "center", marginTop: 8 }}>Your offer has been recorded. The citizen may contact you.</Text>
        <TouchableOpacity onPress={() => router.back()} style={{ backgroundColor: C.orange, borderRadius: 12, padding: 16, marginTop: 32, width: "100%", alignItems: "center" }}>
          <Text style={{ color: "#fff", fontWeight: "700", fontSize: 15 }}>DONE</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: C.bg }}>
      <View style={{ flexDirection: "row", alignItems: "center", padding: 16, borderBottomWidth: 1, borderBottomColor: C.divider }}>
        <TouchableOpacity onPress={() => router.back()} style={{ marginRight: 12 }}>
          <Ionicons name="arrow-back" size={24} color={C.textPrimary} />
        </TouchableOpacity>
        <Text style={{ color: C.emergencyRed, fontSize: 18, fontWeight: "700" }}>Offer Help</Text>
      </View>
      <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
        <Text style={{ color: C.textSecondary, fontSize: 12, marginBottom: 16 }}>Emergency: {id}</Text>
        <SectionHeader title="What Can You Offer?" />
        {[HELP_TYPES.slice(0,3), HELP_TYPES.slice(3,6), HELP_TYPES.slice(6)].map((row, i) => (
          <View key={i} style={{ flexDirection: "row", gap: 8, marginBottom: 8 }}>
            {row.map((t) => <ToggleChip key={t} label={HELP_TYPE_LABELS[t]} selected={helpType === t} onToggle={() => setHelpType(t)} />)}
            {row.length < 3 && [...Array(3 - row.length)].map((_, j) => <View key={j} style={{ flex: 1 }} />)}
          </View>
        ))}
        <SectionHeader title="Message (Optional)" />
        <FormField label="" value={message} onChangeText={setMessage} placeholder="Briefly describe how you can help…" multiline numberOfLines={4} style={{ height: 100, textAlignVertical: "top" }} />
        <TouchableOpacity onPress={handleSubmit} style={{ backgroundColor: C.orange, borderRadius: 14, padding: 18, alignItems: "center", marginTop: 16, marginBottom: 24 }}>
          <Text style={{ color: "#fff", fontWeight: "700", fontSize: 15 }}>SUBMIT HELP OFFER</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}
