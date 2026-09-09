import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { View, Text } from "react-native";
import { C } from "../../src/theme/colors";

// Placeholder screen component for tabs not owned by this module
function PlaceholderTab({ label }: { label: string }) {
  return (
    <View style={{ flex: 1, backgroundColor: C.bg, justifyContent: "center", alignItems: "center" }}>
      <Text style={{ color: C.textSecondary, fontSize: 16 }}>{label}</Text>
      <Text style={{ color: C.textDisabled, fontSize: 13, marginTop: 8 }}>Module not yet integrated</Text>
    </View>
  );
}

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: C.white,
          borderTopColor: C.divider,
          borderTopWidth: 1,
          height: 60,
          paddingBottom: 6,
        },
        tabBarActiveTintColor: C.orange,
        tabBarInactiveTintColor: C.textSecondary,
        tabBarLabelStyle: { fontSize: 10, fontWeight: "600" },
      }}
    >
      {/* Tab 1 — LIVE MAP (teammate's module — placeholder) */}
      <Tabs.Screen
        name="live-map"
        options={{
          title: "LIVE MAP",
          tabBarIcon: ({ color, size }) => <Ionicons name="map-outline" size={size} color={color} />,
        }}
      />

      {/* Tab 2 — REPORT (teammate's module — placeholder) */}
      <Tabs.Screen
        name="report"
        options={{
          title: "REPORT",
          tabBarIcon: ({ color, size }) => <Ionicons name="warning-outline" size={size} color={color} />,
        }}
      />

      {/* Tab 3 — EMERGENCY (YOUR module — active) */}
      <Tabs.Screen
        name="index"
        options={{
          title: "EMERGENCY",
          tabBarIcon: ({ color, size }) => (
            <View style={{ width: size + 8, height: size + 8, borderRadius: (size + 8) / 2, backgroundColor: color === C.orange ? C.orange : "transparent", alignItems: "center", justifyContent: "center" }}>
              <Ionicons name="medkit" size={size - 2} color={color === C.orange ? "#fff" : color} />
            </View>
          ),
        }}
      />

      {/* Tab 4 — ADMIN (teammate's module — placeholder) */}
      <Tabs.Screen
        name="admin"
        options={{
          title: "ADMIN",
          tabBarIcon: ({ color, size }) => <Ionicons name="person-outline" size={size} color={color} />,
        }}
      />
    </Tabs>
  );
}
