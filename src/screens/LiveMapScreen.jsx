import React, { useState, useRef, useMemo, useEffect } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  FlatList,
  ActivityIndicator,
  StyleSheet,
  StatusBar,
  Alert,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import AISummaryCard from "../components/AISummaryCard";
import ReportCard from "../components/ReportCard";
import ReportDetailModal from "../components/ReportDetailModal";
import OsmMapView from "../components/OsmMapView";
import { generateAISituationSummary } from "../utils/hazardSummary";
import { generateLiveSummaryWithAI } from "../services/aiSummaryService";
import { upvoteReport } from "../services/reportService";
import { fetchSafeRoute, searchDestinations } from "../services/routingService";

export default function LiveMapScreen({
  reports,
  userLocation,
  isLocationLoading,
  onNavigateToEmergency,
}) {
  const mapRef = useRef(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedReport, setSelectedReport] = useState(null);
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);
  const [aiSummaryText, setAiSummaryText] = useState("");

  // Navigation & Rerouting States
  const [destination, setDestination] = useState(null);
  const [routeCoordinates, setRouteCoordinates] = useState([]);
  const [isRouting, setIsRouting] = useState(false);

  const liveSummary = useMemo(() => generateAISituationSummary(reports), [reports]);

  useEffect(() => {
    setAiSummaryText(liveSummary);

    let isMounted = true;
    generateLiveSummaryWithAI(reports).then((summary) => {
      if (isMounted && summary) {
        setAiSummaryText(summary);
      }
    });

    return () => {
      isMounted = false;
    };
  }, [reports, liveSummary]);

  const filteredReports = useMemo(() => {
    if (!searchQuery.trim()) return reports;
    const query = searchQuery.toLowerCase();
    return reports.filter(
      (r) =>
        r.category?.toLowerCase().includes(query) ||
        r.description?.toLowerCase().includes(query)
    );
  }, [reports, searchQuery]);

  // Recalculate dynamic route whenever reports or destination change
  useEffect(() => {
    if (userLocation && destination) {
      handleCalculateSafeRoute(destination);
    }
  }, [reports, destination, userLocation]);

  const handleCalculateSafeRoute = async (targetCoords) => {
    if (!userLocation) {
      Alert.alert("GPS Required", "Waiting for current location coordinates...");
      return;
    }

    setIsRouting(true);
    // Filter active/verified hazards to send to the routing API
    const activeHazards = reports.filter((r) => r.verified || (r.votes && r.votes >= 2));

    const path = await fetchSafeRoute(userLocation, targetCoords, activeHazards);
    setIsRouting(false);

    if (path && path.length > 0) {
      setRouteCoordinates(path);
      setDestination(targetCoords);
    } else {
      Alert.alert("Route Warning", "Unable to compute safe path around active hazard zones.");
    }
  };

  const clearNavigation = () => {
    setDestination(null);
    setRouteCoordinates([]);
  };

  const handleReportCardPress = (report) => {
    if (mapRef.current) {
      mapRef.current.animateToRegion(
        {
          latitude: report.latitude,
          longitude: report.longitude,
          latitudeDelta: 0.005,
          longitudeDelta: 0.005,
        },
        700
      );
    }
    setSelectedReport(report);
    setIsDetailModalVisible(true);
  };

  const handleMarkerPress = (report) => {
    setSelectedReport(report);
    setIsDetailModalVisible(true);
  };

  const handleUpvote = (reportId) => {
    upvoteReport(reportId);
    if (selectedReport && selectedReport.id === reportId) {
      setSelectedReport((prev) => ({ ...prev, upvotes: (prev.upvotes || 0) + 1 }));
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#0F172A" />

      {/* 1. Dark Navy Top Header with Search and Emergency Bell */}
      <View style={styles.header}>
        <View style={styles.headerTopRow}>
          <View style={styles.brandContainer}>
            <View style={styles.brandIconBox}>
              <Ionicons name="warning" size={16} color="#FFFFFF" />
            </View>
            <Text style={styles.brandText}>ABHAYA</Text>
            <View style={styles.liveBadge}>
              <Text style={styles.liveBadgeText}>LIVE DISASTER OPS</Text>
            </View>
          </View>

          <TouchableOpacity
            style={styles.bellButton}
            onPress={onNavigateToEmergency}
          >
            <Ionicons name="notifications" size={22} color="#FFFFFF" />
            {reports.length > 0 && (
              <View style={styles.bellBadge}>
                <Text style={styles.bellBadgeText}>{reports.length}</Text>
              </View>
            )}
          </TouchableOpacity>
        </View>

        {/* Search Bar */}
        <View style={styles.searchBar}>
          <Ionicons name="search" size={18} color="#94A3B8" />
          <TextInput
            placeholder="Search hazard, road, or shelter..."
            placeholderTextColor="#94A3B8"
            value={searchQuery}
            onChangeText={setSearchQuery}
            style={styles.searchInput}
          />
          {searchQuery.length > 0 && (
            <TouchableOpacity onPress={() => setSearchQuery("")}>
              <Ionicons name="close-circle" size={16} color="#94A3B8" />
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* 2. AI Situation Summary Card */}
      <AISummaryCard summaryText={aiSummaryText} />

      {/* GPS Calibration status indicator */}
      {isLocationLoading && (
        <View style={styles.gpsLoadingBanner}>
          <ActivityIndicator size="small" color="#F97316" />
          <Text style={styles.gpsLoadingText}>
            Calibrating continuous GPS coordinates...
          </Text>
        </View>
      )}

      {/* Rerouting Active Navigation Bar */}
      {destination && (
        <View style={styles.routeBanner}>
          <View style={styles.routeInfo}>
            <Ionicons name="navigate-circle" size={20} color="#0284C7" />
            <Text style={styles.routeText}>
              {isRouting ? "Calculating hazard-free route..." : "Hazard Avoidance Navigation Active"}
            </Text>
          </View>
          <TouchableOpacity onPress={clearNavigation} style={styles.clearRouteBtn}>
            <Ionicons name="close-sharp" size={16} color="#0F172A" />
          </TouchableOpacity>
        </View>
      )}

      {/* 3. OpenStreetMap via Leaflet WebView */}
      <View style={styles.mapContainer}>
        <OsmMapView
          ref={mapRef}
          userLocation={userLocation}
          reports={filteredReports}
          routeCoordinates={routeCoordinates}
          onMarkerPress={handleMarkerPress}
        />

        <Text style={styles.osmAttribution}>© OpenStreetMap</Text>
      </View>

      {/* 4. Community Reports Feed Header */}
      <View style={styles.feedHeader}>
        <View style={styles.feedHeaderTitleRow}>
          <Text style={styles.feedHeaderTitle}>COMMUNITY REPORTS</Text>
          <View style={styles.feedCountBadge}>
            <Text style={styles.feedCountText}>{filteredReports.length} active</Text>
          </View>
        </View>
        <Text style={styles.feedSubText}>Live Sync • Mangaluru</Text>
      </View>

      {/* 5. Scrollable Community Reports Feed */}
      <FlatList
        data={filteredReports}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <ReportCard
            report={item}
            userLocation={userLocation}
            onPress={() => handleReportCardPress(item)}
            onUpvote={() => handleUpvote(item.id)}
          />
        )}
        style={styles.feedList}
        contentContainerStyle={styles.feedListContent}
        ListEmptyComponent={
          <View style={styles.emptyFeedContainer}>
            <Text style={styles.emptyFeedText}>No hazards match your filter.</Text>
          </View>
        }
      />

      {/* Report Detail Modal with Route Avoidance Button */}
      <ReportDetailModal
        visible={isDetailModalVisible}
        report={selectedReport}
        userLocation={userLocation}
        onClose={() => setIsDetailModalVisible(false)}
        onUpvote={handleUpvote}
        onLocateOnMap={handleReportCardPress}
        onNavigateAround={(target) => {
          setIsDetailModalVisible(false);
          handleCalculateSafeRoute(target);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  header: {
    backgroundColor: "#0F172A",
    paddingHorizontal: 16,
    paddingTop: 8,
    paddingBottom: 12,
  },
  headerTopRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
  },
  brandContainer: {
    flexDirection: "row",
    alignItems: "center",
  },
  brandIconBox: {
    backgroundColor: "#F97316",
    borderRadius: 6,
    padding: 4,
    marginRight: 8,
  },
  brandText: {
    fontSize: 18,
    fontWeight: "900",
    color: "#FFFFFF",
    letterSpacing: 1.2,
  },
  liveBadge: {
    backgroundColor: "rgba(249, 115, 22, 0.2)",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    marginLeft: 6,
  },
  liveBadgeText: {
    color: "#F97316",
    fontSize: 9,
    fontWeight: "800",
  },
  bellButton: {
    padding: 6,
    position: "relative",
  },
  bellBadge: {
    position: "absolute",
    top: 2,
    right: 2,
    backgroundColor: "#DC2626",
    borderRadius: 8,
    minWidth: 16,
    height: 16,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 3,
  },
  bellBadgeText: {
    color: "#FFFFFF",
    fontSize: 9,
    fontWeight: "900",
  },
  searchBar: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#1E293B",
    borderRadius: 8,
    paddingHorizontal: 10,
    height: 40,
  },
  searchInput: {
    flex: 1,
    color: "#FFFFFF",
    fontSize: 13,
    marginLeft: 8,
  },
  gpsLoadingBanner: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FEF3C7",
    paddingVertical: 4,
  },
  gpsLoadingText: {
    fontSize: 11,
    color: "#92400E",
    fontWeight: "600",
    marginLeft: 6,
  },
  routeBanner: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#E0F2FE",
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderBottomWidth: 1,
    borderBottomColor: "#BAE6FD",
  },
  routeInfo: {
    flexDirection: "row",
    alignItems: "center",
  },
  routeText: {
    fontSize: 12,
    fontWeight: "700",
    color: "#0369A1",
    marginLeft: 6,
  },
  clearRouteBtn: {
    padding: 4,
  },
  mapContainer: {
    flex: 1.35,
    minHeight: 220,
    width: "100%",
    position: "relative",
    backgroundColor: "#E2E8F0",
  },
  osmAttribution: {
    position: "absolute",
    bottom: 4,
    left: 6,
    fontSize: 9,
    color: "#475569",
    backgroundColor: "rgba(255,255,255,0.75)",
    paddingHorizontal: 4,
    paddingVertical: 1,
  },
  feedList: {
    flex: 1,
    maxHeight: 260,
  },
  feedHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 10,
    backgroundColor: "#FFFFFF",
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
  },
  feedHeaderTitleRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  feedHeaderTitle: {
    fontWeight: "900",
    fontSize: 13,
    color: "#0F172A",
    letterSpacing: 0.5,
  },
  feedCountBadge: {
    backgroundColor: "#0F172A",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 10,
    marginLeft: 6,
  },
  feedCountText: {
    fontSize: 10,
    fontWeight: "700",
    color: "#FFFFFF",
  },
  feedSubText: {
    fontSize: 11,
    color: "#64748B",
  },
  feedListContent: {
    paddingVertical: 6,
    paddingBottom: 90,
  },
  emptyFeedContainer: {
    padding: 30,
    alignItems: "center",
  },
  emptyFeedText: {
    color: "#94A3B8",
    fontSize: 13,
  },
});