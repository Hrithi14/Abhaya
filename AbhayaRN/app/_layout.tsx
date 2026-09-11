import React, { useEffect } from "react";
import { Stack, useRouter, useSegments } from "expo-router";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";
import { AuthProvider, useAuth } from "../src/context/AuthContext";

function AuthRedirect() {
  const { user } = useAuth();
  const router   = useRouter();
  const segments = useSegments();

  useEffect(() => {
    // Don't act until segments resolve
    if (!segments || segments.length === 0) return;

    const inTabs  = segments[0] === "(tabs)";
    const inLogin = segments[0] === "login";

    if (user === "ready") {
      if (!inLogin) router.replace("/login");
    } else {
      if (!inTabs) router.replace("/");
    }
  }, [user, segments]);

  return null;
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <SafeAreaProvider>
        <StatusBar style="light" backgroundColor="#0F172A" />
        <AuthRedirect />
        <Stack
          screenOptions={{
            headerShown: false,
            animation: "slide_from_right",
            contentStyle: { backgroundColor: "#FFFFFF" },
          }}
        >
          <Stack.Screen name="login"            />
          <Stack.Screen name="(tabs)"           />
          <Stack.Screen name="water-rescue"     />
          <Stack.Screen name="medical"          />
          <Stack.Screen name="boat-rescue"      />
          <Stack.Screen name="shelter"          />
          <Stack.Screen name="evacuate"         />
          <Stack.Screen name="my-requests"      />
          <Stack.Screen name="emergency-detail" />
          <Stack.Screen name="offer-help"       />
        </Stack>
      </SafeAreaProvider>
    </AuthProvider>
  );
}
