import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { View, Platform } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { C } from "../../src/theme/colors";

export default function TabLayout() {
  const insets = useSafeAreaInsets();

  // Extra bottom padding so tab bar clears Android gesture nav bar
  const tabBarHeight = 58 + Math.max(insets.bottom, 8);

  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: "#fff",
          borderTopColor: C.divider,
          borderTopWidth: 1,
          height: tabBarHeight,
          paddingBottom: Math.max(insets.bottom, 10),
          paddingTop: 6,
          elevation: 12,
          shadowColor: "#000",
          shadowOffset: { width: 0, height: -2 },
          shadowOpacity: 0.08,
          shadowRadius: 8,
        },
        tabBarActiveTintColor: C.orange,
        tabBarInactiveTintColor: C.textSecondary,
        tabBarLabelStyle: {
          fontSize: 10,
          fontWeight: "700",
          letterSpacing: 0.3,
          marginTop: 2,
        },
      }}
    >
      {/* Tab 1 — LIVE MAP */}
      <Tabs.Screen
        name="live-map"
        options={{
          title: "LIVE MAP",
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="map" size={size} color={color} />
          ),
        }}
      />

      {/* Tab 2 — REPORT */}
      <Tabs.Screen
        name="report"
        options={{
          title: "REPORT",
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="warning" size={size} color={color} />
          ),
        }}
      />

      {/* Tab 3 — EMERGENCY (centre tab, highlighted) */}
      <Tabs.Screen
        name="index"
        options={{
          title: "EMERGENCY",
          tabBarIcon: ({ color, size }) => (
            <View
              style={{
                width: size + 14,
                height: size + 14,
                borderRadius: (size + 14) / 2,
                backgroundColor: color === C.orange ? C.orange : "#F1F5F9",
                alignItems: "center",
                justifyContent: "center",
                marginBottom: 2,
                elevation: color === C.orange ? 4 : 0,
                shadowColor: C.orange,
                shadowOffset: { width: 0, height: 2 },
                shadowOpacity: 0.3,
                shadowRadius: 4,
              }}
            >
              <Ionicons
                name="medkit"
                size={size}
                color={color === C.orange ? "#fff" : color}
              />
            </View>
          ),
        }}
      />

      {/* Tab 4 — PROFILE (was Admin) */}
      <Tabs.Screen
        name="admin"
        options={{
          title: "PROFILE",
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="person-circle-outline" size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
