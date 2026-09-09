import { Stack } from "expo-router";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

export default function RootLayout() {
  return (
    <SafeAreaProvider>
      <StatusBar style="light" backgroundColor="#0D1117" />
      <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: "#0D1117" } }}>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="water-rescue" options={{ headerShown: false }} />
        <Stack.Screen name="medical" options={{ headerShown: false }} />
        <Stack.Screen name="boat-rescue" options={{ headerShown: false }} />
        <Stack.Screen name="shelter" options={{ headerShown: false }} />
        <Stack.Screen name="my-requests" options={{ headerShown: false }} />
        <Stack.Screen name="emergency-detail" options={{ headerShown: false }} />
        <Stack.Screen name="offer-help" options={{ headerShown: false }} />
        <Stack.Screen name="evacuate" options={{ headerShown: false }} />
      </Stack>
    </SafeAreaProvider>
  );
}
