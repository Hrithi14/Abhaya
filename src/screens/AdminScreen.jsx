import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  Switch,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { toggleReportVerification } from "../services/reportService";

export default function AdminScreen({ reports }) {
  const [offlineTilesEnabled, setOfflineTilesEnabled] = useState(true);
  const [floodSirenEnabled, setFloodSirenEnabled] = useState(true);
  const [continuousGpsEnabled, setContinuousGpsEnabled] = useState(true);

  const totalReports = reports.length;
  const verifiedReports = reports.filter((r) => r.verified).length;
  const totalUpvotes = reports.reduce((acc, curr) => acc + (curr.upvotes || 0), 0);

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.headerIcon}>
          <Ionicons name="shield-checkmark" size={20} color="#0F172A" />
        </View>
        <View>
          <Text style={styles.headerTitle}>ADMIN & RESPONDER CONSOLE</Text>
          <Text style={styles.headerSubtitle}>
            ABHAYA Disaster Coordination • Node Mangaluru
          </Text>
        </View>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* User / Officer Profile Card */}
        <View style={styles.profileCard}>
          <View style={styles.profileAvatar}>
            <Ionicons name="person" size={24} color="#FFFFFF" />
          </View>
          <View style={styles.profileInfo}>
            <Text style={styles.profileName}>Volunteer Field Responder</Text>
            <Text style={styles.profileBadge}>
              Badge ID: KA-19-MNG-402 • Status: Active Duty
            </Text>
          </View>
          <View style={styles.activeTag}>
            <Text style={styles.activeTagText}>VERIFIED</Text>
          </View>
        </View>

        {/* Stats Overview: Reports submitted, Verified reports, Upvotes */}
        <View style={styles.statsRow}>
          <View style={styles.statCard}>
            <Text style={[styles.statValue, { color: "#F97316" }]}>
              {totalReports}
            </Text>
            <Text style={styles.statLabel}>Reports</Text>
          </View>

          <View style={styles.statCard}>
            <Text style={[styles.statValue, { color: "#16A34A" }]}>
              {verifiedReports}
            </Text>
            <Text style={styles.statLabel}>Verified</Text>
          </View>

          <View style={styles.statCard}>
            <Text style={[styles.statValue, { color: "#0284C7" }]}>
              {totalUpvotes}
            </Text>
            <Text style={styles.statLabel}>Upvotes</Text>
          </View>
        </View>

        {/* Basic System Settings */}
        <Text style={styles.sectionTitle}>BASIC SYSTEM SETTINGS</Text>
        <View style={styles.settingsCard}>
          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingName}>Offline Tile Caching</Text>
              <Text style={styles.settingDesc}>
                Pre-fetch OpenStreetMap tiles for low-signal areas
              </Text>
            </View>
            <Switch
              value={offlineTilesEnabled}
              onValueChange={setOfflineTilesEnabled}
              thumbColor="#F97316"
              trackColor={{ false: "#CBD5E1", true: "#FED7AA" }}
            />
          </View>

          <View style={styles.settingDivider} />

          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingName}>Severe Flash Flood Siren</Text>
              <Text style={styles.settingDesc}>
                Trigger alert tone when near rising flood perimeter
              </Text>
            </View>
            <Switch
              value={floodSirenEnabled}
              onValueChange={setFloodSirenEnabled}
              thumbColor="#F97316"
              trackColor={{ false: "#CBD5E1", true: "#FED7AA" }}
            />
          </View>

          <View style={styles.settingDivider} />

          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingName}>Continuous GPS Tracking</Text>
              <Text style={styles.settingDesc}>
                Broadcast live coordinates at 3-second intervals
              </Text>
            </View>
            <Switch
              value={continuousGpsEnabled}
              onValueChange={setContinuousGpsEnabled}
              thumbColor="#F97316"
              trackColor={{ false: "#CBD5E1", true: "#FED7AA" }}
            />
          </View>
        </View>

        {/* Report Moderation Section */}
        <Text style={styles.sectionTitle}>MODERATE HAZARD REPORTS</Text>
        {reports.map((report) => (
          <View key={report.id} style={styles.moderationCard}>
            <View style={styles.moderationHeader}>
              <Text style={styles.moderationCategory}>
                {report.category} ({report.id})
              </Text>
              <TouchableOpacity
                style={[
                  styles.verifyActionBtn,
                  report.verified ? styles.btnVerified : styles.btnUnverified,
                ]}
                onPress={() =>
                  toggleReportVerification(report.id, report.verified)
                }
              >
                <Text
                  style={[
                    styles.verifyActionText,
                    report.verified
                      ? styles.textVerified
                      : styles.textUnverified,
                  ]}
                >
                  {report.verified ? "Mark Unverified" : "Verify Report"}
                </Text>
              </TouchableOpacity>
            </View>

            <Text style={styles.moderationDesc}>{report.description}</Text>
            <Text style={styles.moderationCoords}>
              GPS: {report.latitude?.toFixed(4)}, {report.longitude?.toFixed(4)} •{" "}
              {report.upvotes || 0} upvotes
            </Text>
          </View>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#0F172A",
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  headerIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: "#38BDF8",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  headerTitle: {
    fontSize: 15,
    fontWeight: "900",
    color: "#FFFFFF",
  },
  headerSubtitle: {
    fontSize: 11,
    color: "#94A3B8",
  },
  scrollContent: {
    padding: 16,
    paddingBottom: 90,
  },
  profileCard: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 12,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  profileAvatar: {
    width: 42,
    height: 42,
    borderRadius: 21,
    backgroundColor: "#1E293B",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  profileInfo: {
    flex: 1,
  },
  profileName: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
  },
  profileBadge: {
    fontSize: 11,
    color: "#64748B",
  },
  activeTag: {
    backgroundColor: "#DCFCE7",
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
  },
  activeTagText: {
    color: "#16A34A",
    fontSize: 10,
    fontWeight: "800",
  },
  statsRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 8,
    marginBottom: 16,
  },
  statCard: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    padding: 12,
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  statValue: {
    fontSize: 22,
    fontWeight: "900",
  },
  statLabel: {
    fontSize: 11,
    color: "#64748B",
    marginTop: 2,
    fontWeight: "600",
  },
  sectionTitle: {
    fontSize: 12,
    fontWeight: "800",
    color: "#334155",
    letterSpacing: 0.5,
    marginVertical: 8,
  },
  settingsCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    padding: 12,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  settingRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 6,
  },
  settingInfo: {
    flex: 1,
    paddingRight: 10,
  },
  settingName: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0F172A",
  },
  settingDesc: {
    fontSize: 11,
    color: "#64748B",
  },
  settingDivider: {
    height: 1,
    backgroundColor: "#F1F5F9",
    marginVertical: 4,
  },
  moderationCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  moderationHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 4,
  },
  moderationCategory: {
    fontSize: 13,
    fontWeight: "800",
    color: "#0F172A",
  },
  verifyActionBtn: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  btnVerified: {
    backgroundColor: "#FEF3C7",
  },
  btnUnverified: {
    backgroundColor: "#DCFCE7",
  },
  verifyActionText: {
    fontSize: 10,
    fontWeight: "800",
  },
  textVerified: {
    color: "#B45309",
  },
  textUnverified: {
    color: "#16A34A",
  },
  moderationDesc: {
    fontSize: 12,
    color: "#334155",
    marginBottom: 4,
  },
  moderationCoords: {
    fontSize: 11,
    color: "#64748B",
  },
});
