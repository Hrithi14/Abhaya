import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export default function AISummaryCard({ message, summaryText }) {
  const body = message || summaryText || "";
  return (
    <View style={styles.card}>
      <View style={styles.iconContainer}>
        <Ionicons name="sparkles" size={16} color="#0F172A" />
      </View>
      <View style={styles.textContainer}>
        <View style={styles.titleRow}>
          <Text style={styles.title}>AI SITUATION SUMMARY |</Text>
          <Text style={styles.liveTag}> LIVE</Text>
        </View>
        <Text style={styles.message} numberOfLines={3}>
          {body}
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#FEF3C7", // Amber card below header
    borderColor: "#FDE68A",
    borderWidth: 1,
    borderRadius: 8,
    marginHorizontal: 12,
    marginVertical: 8,
    padding: 10,
    flexDirection: "row",
    alignItems: "flex-start",
  },
  iconContainer: {
    backgroundColor: "#F59E0B",
    borderRadius: 4,
    padding: 4,
    marginRight: 8,
    marginTop: 1,
  },
  textContainer: {
    flex: 1,
  },
  titleRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 2,
  },
  title: {
    fontWeight: "900",
    fontSize: 12,
    color: "#92400E",
    letterSpacing: 0.5,
  },
  liveTag: {
    fontWeight: "800",
    fontSize: 10,
    color: "#DC2626",
  },
  message: {
    fontSize: 12,
    lineHeight: 16,
    color: "#78350F",
    fontWeight: "500",
  },
});
