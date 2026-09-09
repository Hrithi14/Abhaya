import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export const TABS = {
  LIVE_MAP: "LIVE MAP",
  REPORT: "REPORT",
  EMERGENCY: "EMERGENCY",
  ADMIN: "ADMIN",
};

export default function BottomNavigation({ currentTab, onSelectTab }) {
  const navItems = [
    {
      key: TABS.LIVE_MAP,
      label: "LIVE MAP",
      icon: "map",
      iconOutline: "map-outline",
    },
    {
      key: TABS.REPORT,
      label: "REPORT",
      icon: "warning",
      iconOutline: "warning-outline",
    },
    {
      key: TABS.EMERGENCY,
      label: "EMERGENCY",
      icon: "medical",
      iconOutline: "medical-outline",
    },
    {
      key: TABS.ADMIN,
      label: "ADMIN",
      icon: "shield",
      iconOutline: "shield-outline",
    },
  ];

  return (
    <View style={styles.container}>
      {navItems.map((item) => {
        const isActive = currentTab === item.key;
        const color = isActive ? "#38BDF8" : "#94A3B8";

        return (
          <TouchableOpacity
            key={item.key}
            style={styles.tabButton}
            activeOpacity={0.8}
            onPress={() => onSelectTab(item.key)}
          >
            <View style={[styles.iconWrapper, isActive && styles.activeIconWrapper]}>
              <Ionicons
                name={isActive ? item.icon : item.iconOutline}
                size={22}
                color={color}
              />
            </View>
            <Text style={[styles.tabLabel, { color }]}>{item.label}</Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    backgroundColor: "#0F172A", // Dark navy footer
    borderTopWidth: 1,
    borderTopColor: "#1E293B",
    paddingVertical: 6,
    paddingBottom: 14,
    justifyContent: "space-around",
    alignItems: "center",
  },
  tabButton: {
    alignItems: "center",
    justifyContent: "center",
    flex: 1,
    paddingVertical: 4,
  },
  iconWrapper: {
    paddingHorizontal: 12,
    paddingVertical: 3,
    borderRadius: 12,
  },
  activeIconWrapper: {
    backgroundColor: "rgba(56, 189, 248, 0.15)",
  },
  tabLabel: {
    fontSize: 10,
    fontWeight: "700",
    letterSpacing: 0.5,
    marginTop: 2,
  },
});
