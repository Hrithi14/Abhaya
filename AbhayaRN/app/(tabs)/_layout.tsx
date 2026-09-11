import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { View } from "react-native";
import { C } from "../../src/theme/colors";

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: "#fff",
          borderTopColor: C.divider,
          borderTopWidth: 1,
          height: 62,
          paddingBottom: 8,
          paddingTop: 4,
        },
        tabBarActiveTintColor: C.orange,
        tabBarInactiveTintColor: C.textSecondary,
        tabBarLabelStyle: { fontSize: 10, fontWeight: "700", letterSpacing: 0.3 },
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
                width: size + 10,
                height: size + 10,
                borderRadius: (size + 10) / 2,
                backgroundColor: color === C.orange ? C.orange : "transparent",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Ionicons
                name="medkit"
                size={size - 2}
                color={color === C.orange ? "#fff" : color}
              />
            </View>
          ),
        }}
      />

      {/* Tab 4 — ADMIN */}
      <Tabs.Screen
        name="admin"
        options={{
          title: "ADMIN",
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="shield-checkmark-outline" size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
