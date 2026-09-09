import React from "react";
import { View, StyleSheet } from "react-native";
import { Ionicons, MaterialCommunityIcons } from "@expo/vector-icons";

export function getHazardIcon(category) {
  switch (category) {
    case "FLOOD WATER":
      return <Ionicons name="water" size={16} color="#FFFFFF" />;
    case "WATERLOGGING":
      return <MaterialCommunityIcons name="waves" size={16} color="#FFFFFF" />;
    case "FALLEN TREE":
      return <MaterialCommunityIcons name="tree" size={16} color="#FFFFFF" />;
    case "ROADBLOCK":
      return <MaterialCommunityIcons name="road-variant" size={16} color="#FFFFFF" />;
    case "POTHOLE":
      return <Ionicons name="warning" size={16} color="#FFFFFF" />;
    case "OPEN WIRE":
      return <Ionicons name="flash" size={16} color="#FFFFFF" />;
    case "LANDSLIDE":
      return <MaterialCommunityIcons name="terrain" size={16} color="#FFFFFF" />;
    case "FIRE":
      return <Ionicons name="flame" size={16} color="#FFFFFF" />;
    case "MEDICAL EMERGENCY":
      return <Ionicons name="medkit" size={16} color="#FFFFFF" />;
    default:
      return <Ionicons name="alert-circle" size={16} color="#FFFFFF" />;
  }
}

export function getHazardColor(category) {
  switch (category) {
    case "FLOOD WATER":
    case "WATERLOGGING":
      return "#F97316"; // Primary Orange
    case "FIRE":
    case "MEDICAL EMERGENCY":
      return "#DC2626"; // Emergency Red
    case "OPEN WIRE":
    case "ROADBLOCK":
      return "#F59E0B"; // Amber
    default:
      return "#EA580C";
  }
}

export default function HazardMarker({ category, verified }) {
  const bgColor = getHazardColor(category);
  const borderColor = verified ? "#16A34A" : "#FFFFFF";

  return (
    <View style={styles.container}>
      <View style={[styles.badge, { backgroundColor: bgColor, borderColor }]}>
        {getHazardIcon(category)}
      </View>
      <View style={[styles.arrow, { borderTopColor: bgColor }]} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
  },
  badge: {
    width: 32,
    height: 32,
    borderRadius: 16,
    borderWidth: 2,
    alignItems: "center",
    justifyContent: "center",
    elevation: 4,
    shadowColor: "#000",
    shadowOpacity: 0.3,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 3,
  },
  arrow: {
    width: 0,
    height: 0,
    borderLeftWidth: 5,
    borderRightWidth: 5,
    borderTopWidth: 5,
    borderStyle: "solid",
    backgroundColor: "transparent",
    borderLeftColor: "transparent",
    borderRightColor: "transparent",
    marginTop: -1,
  },
});
