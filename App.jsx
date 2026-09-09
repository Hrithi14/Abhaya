import React, { useState, useEffect } from "react";
import { View, StyleSheet, StatusBar } from "react-native";
import { SafeAreaProvider, SafeAreaView } from "react-native-safe-area-context";
import {
  getInitialUserLocation,
  watchUserLocation,
  DEFAULT_MANGALURU_LOCATION,
} from "./src/services/locationService";
import { subscribeToReports, DEFAULT_REPORTS } from "./src/services/reportService";
import BottomNavigation, { TABS } from "./src/components/BottomNavigation";
import SOSButton from "./src/components/SOSButton";

// Screens
import LiveMapScreen from "./src/screens/LiveMapScreen";
import ReportScreen from "./src/screens/ReportScreen";
import EmergencyScreen from "./src/screens/EmergencyScreen";
import AdminScreen from "./src/screens/AdminScreen";

export default function App() {
  const [currentTab, setCurrentTab] = useState(TABS.LIVE_MAP);
  const [userLocation, setUserLocation] = useState(DEFAULT_MANGALURU_LOCATION);
  const [isLocationLoading, setIsLocationLoading] = useState(true);
  const [reports, setReports] = useState(DEFAULT_REPORTS);

  // 1. Initialize GPS and start continuous tracking
  useEffect(() => {
    let isMounted = true;
    let locationSubscription = null;

    async function initLocation() {
      const initial = await getInitialUserLocation();
      if (isMounted && initial.location) {
        setUserLocation(initial.location);
        setIsLocationLoading(false);
      }

      // Continuous GPS tracking (updates as user moves)
      locationSubscription = await watchUserLocation(
        (coords) => {
          if (isMounted) {
            setUserLocation(coords);
            setIsLocationLoading(false);
          }
        },
        (err) => {
          console.warn("GPS tracking error:", err);
          if (isMounted) setIsLocationLoading(false);
        }
      );
    }

    initLocation();

    return () => {
      isMounted = false;
      if (locationSubscription && locationSubscription.remove) {
        locationSubscription.remove();
      }
    };
  }, []);

  // 2. Real-time Firestore sync listener
  useEffect(() => {
    const unsubscribe = subscribeToReports(
      (updatedReports) => {
        setReports(updatedReports);
      },
      (error) => {
        console.warn("Real-time listener warning:", error);
      }
    );

    return () => {
      if (unsubscribe) unsubscribe();
    };
  }, []);

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.root}>
      <StatusBar barStyle="light-content" backgroundColor="#0F172A" />

      {/* Screen Views */}
      <View style={styles.content}>
        {currentTab === TABS.LIVE_MAP && (
          <LiveMapScreen
            reports={reports}
            userLocation={userLocation}
            isLocationLoading={isLocationLoading}
            onNavigateToEmergency={() => setCurrentTab(TABS.EMERGENCY)}
          />
        )}

        {currentTab === TABS.REPORT && (
          <ReportScreen
            userLocation={userLocation}
            onReportSubmitted={() => setCurrentTab(TABS.LIVE_MAP)}
          />
        )}

        {currentTab === TABS.EMERGENCY && <EmergencyScreen />}

        {currentTab === TABS.ADMIN && <AdminScreen reports={reports} />}
      </View>

      {/* Floating SOS only on Report / Admin, not on the live map */}
      {currentTab !== TABS.EMERGENCY && currentTab !== TABS.LIVE_MAP && (
        <View style={styles.floatingSOSContainer}>
          <SOSButton />
        </View>
      )}

      {/* Dark Navy Bottom Navigation Footer */}
      <BottomNavigation
        currentTab={currentTab}
        onSelectTab={(tab) => setCurrentTab(tab)}
      />
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: "#0F172A",
  },
  content: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  floatingSOSContainer: {
    position: "absolute",
    bottom: 74,
    right: 16,
    zIndex: 99,
  },
});
