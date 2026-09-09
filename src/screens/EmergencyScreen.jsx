import React from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  Linking,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";

const EMERGENCY_CONTACTS = [
  {
    id: "ec1",
    name: "Government Wenlock District Hospital",
    type: "HOSPITAL",
    phone: "+91 824 244 4444",
    address: "Hampankatta, Mangaluru",
    note: "24/7 Level-1 Emergency & Trauma Care",
  },
  {
    id: "ec2",
    name: "KMC Hospital Ambedkar Circle",
    type: "HOSPITAL",
    phone: "+91 824 244 5858",
    address: "Balmatta, Mangaluru",
    note: "Flood rescue triage & multi-specialty",
  },
  {
    id: "ec3",
    name: "Mangalore North Police Station (Bunder)",
    type: "POLICE",
    phone: "+91 824 222 0800",
    address: "Bunder Port Area, Mangaluru",
    note: "Coastal patrol & flood barrier unit",
  },
  {
    id: "ec4",
    name: "Kadri Police Station & Rescue Base",
    type: "POLICE",
    phone: "+91 824 222 0801",
    address: "Kadri Hills, Mangaluru",
    note: "High terrain emergency dispatch",
  },
];

const SAFE_SHELTERS = [
  {
    id: "sh1",
    name: "High Ground Town Hall Emergency Shelter",
    address: "Town Hall Compound, Hampankatta",
    elevationMeters: 28,
    distanceKm: 0.8,
    capacity: 650,
    currentOccupancy: 120,
    supplies: ["Clean Water", "Medical Camp", "Food Packets", "Backup Power"],
  },
  {
    id: "sh2",
    name: "St. Aloysius Community High Shelter",
    address: "Light House Hill Rd, Mangaluru",
    elevationMeters: 42,
    distanceKm: 1.4,
    capacity: 900,
    currentOccupancy: 310,
    supplies: ["Dry Rations", "Infant Care", "Satellite Comms", "Blankets"],
  },
];

export default function EmergencyScreen() {
  const dialNumber = (phone) => {
    const cleanPhone = phone.replace(/[^0-9+]/g, "");
    Linking.openURL(`tel:${cleanPhone}`);
  };

  return (
    <View style={styles.container}>
      {/* Top Header */}
      <View style={styles.header}>
        <View style={styles.headerIcon}>
          <Ionicons name="medical" size={20} color="#FFFFFF" />
        </View>
        <View>
          <Text style={styles.headerTitle}>EMERGENCY & DISASTER RELIEF</Text>
          <Text style={styles.headerSubtitle}>
            24/7 Rapid Help • Karnataka Disaster Management
          </Text>
        </View>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* National 112 Banner */}
        <View style={styles.bannerCard}>
          <Text style={styles.bannerSubhead}>NATIONAL EMERGENCY RESPONSE</Text>
          <Text style={styles.bannerDesc}>
            Unified Police, Ambulance, Fire & Flood Evacuation
          </Text>
          <TouchableOpacity
            style={styles.sosBannerButton}
            activeOpacity={0.85}
            onPress={() => dialNumber("112")}
          >
            <Ionicons name="call" size={20} color="#FFFFFF" />
            <Text style={styles.sosBannerButtonText}> CALL 112 NOW</Text>
          </TouchableOpacity>
        </View>

        {/* Section: Safe High-Ground Shelters */}
        <View style={styles.sectionHeaderRow}>
          <Ionicons name="shield-checkmark" size={18} color="#16A34A" />
          <Text style={styles.sectionTitle}> SAFE HIGH-GROUND SHELTERS</Text>
        </View>

        {SAFE_SHELTERS.map((shelter) => (
          <View key={shelter.id} style={styles.shelterCard}>
            <View style={styles.shelterTop}>
              <View style={{ flex: 1 }}>
                <Text style={styles.shelterName}>{shelter.name}</Text>
                <Text style={styles.shelterAddress}>{shelter.address}</Text>
              </View>
              <View style={styles.elevationBadge}>
                <Text style={styles.elevationText}>
                  {shelter.elevationMeters}m Elevation
                </Text>
              </View>
            </View>

            <View style={styles.shelterMetaRow}>
              <Text style={styles.occupancyText}>
                Occupancy: {shelter.currentOccupancy} / {shelter.capacity} citizens
              </Text>
              <Text style={styles.shelterDistance}>{shelter.distanceKm} km away</Text>
            </View>

            <View style={styles.suppliesRow}>
              {shelter.supplies.map((sup, idx) => (
                <View key={idx} style={styles.supplyPill}>
                  <Text style={styles.supplyText}>{sup}</Text>
                </View>
              ))}
            </View>
          </View>
        ))}

        {/* Section: Nearest Hospitals & Police */}
        <View style={styles.sectionHeaderRow}>
          <Ionicons name="bandage" size={18} color="#F97316" />
          <Text style={styles.sectionTitle}> NEAREST HOSPITALS & POLICE</Text>
        </View>

        {EMERGENCY_CONTACTS.map((item) => {
          const isHospital = item.type === "HOSPITAL";
          return (
            <View key={item.id} style={styles.contactCard}>
              <View
                style={[
                  styles.contactIconCircle,
                  { backgroundColor: isHospital ? "#FEF2F2" : "#F0FDF4" },
                ]}
              >
                <Ionicons
                  name={isHospital ? "medkit" : "shield"}
                  size={18}
                  color={isHospital ? "#DC2626" : "#16A34A"}
                />
              </View>

              <View style={styles.contactInfo}>
                <Text style={styles.contactName}>{item.name}</Text>
                <Text style={styles.contactAddress}>{item.address}</Text>
                <Text style={styles.contactNote}>{item.note}</Text>
              </View>

              <TouchableOpacity
                style={styles.callCircleButton}
                activeOpacity={0.8}
                onPress={() => dialNumber(item.phone)}
              >
                <Ionicons name="call" size={18} color="#EA580C" />
              </TouchableOpacity>
            </View>
          );
        })}
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
    backgroundColor: "#DC2626", // Red used strictly for emergency
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
  bannerCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 16,
    alignItems: "center",
    marginBottom: 16,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    elevation: 2,
  },
  bannerSubhead: {
    fontSize: 12,
    fontWeight: "900",
    color: "#DC2626",
    letterSpacing: 0.8,
  },
  bannerDesc: {
    fontSize: 12,
    color: "#64748B",
    marginTop: 2,
    marginBottom: 14,
  },
  sosBannerButton: {
    backgroundColor: "#DC2626",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 10,
    width: "100%",
    height: 48,
  },
  sosBannerButtonText: {
    color: "#FFFFFF",
    fontSize: 15,
    fontWeight: "900",
    letterSpacing: 1,
  },
  sectionHeaderRow: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 10,
  },
  sectionTitle: {
    fontSize: 13,
    fontWeight: "900",
    color: "#0F172A",
  },
  shelterCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    padding: 12,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  shelterTop: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  shelterName: {
    fontSize: 14,
    fontWeight: "800",
    color: "#0F172A",
  },
  shelterAddress: {
    fontSize: 12,
    color: "#64748B",
  },
  elevationBadge: {
    backgroundColor: "#EFF6FF",
    paddingHorizontal: 6,
    paddingVertical: 3,
    borderRadius: 6,
    alignSelf: "flex-start",
  },
  elevationText: {
    color: "#1D4ED8",
    fontSize: 11,
    fontWeight: "800",
  },
  shelterMetaRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginVertical: 8,
  },
  occupancyText: {
    fontSize: 12,
    color: "#334155",
    fontWeight: "600",
  },
  shelterDistance: {
    fontSize: 12,
    fontWeight: "800",
    color: "#EA580C",
  },
  suppliesRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 6,
  },
  supplyPill: {
    backgroundColor: "#F1F5F9",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
  },
  supplyText: {
    fontSize: 10,
    color: "#475569",
  },
  contactCard: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    padding: 12,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  contactIconCircle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  contactInfo: {
    flex: 1,
  },
  contactName: {
    fontSize: 13,
    fontWeight: "800",
    color: "#0F172A",
  },
  contactAddress: {
    fontSize: 11,
    color: "#64748B",
  },
  contactNote: {
    fontSize: 11,
    color: "#0284C7",
    fontWeight: "600",
  },
  callCircleButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "rgba(249, 115, 22, 0.12)",
    alignItems: "center",
    justifyContent: "center",
  },
});
