import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { calculateDistance, formatDistance, formatTimeAgo } from "../utils/distance";
import { getHazardIcon, getHazardColor } from "./HazardMarker";

export default function ReportCard({ report, userLocation, onPress, onUpvote }) {
  const distanceMeters = calculateDistance(
    userLocation?.latitude,
    userLocation?.longitude,
    report.latitude,
    report.longitude
  );
  const distanceText = formatDistance(distanceMeters);
  const timeText = formatTimeAgo(report.createdAt);
  const badgeColor = getHazardColor(report.category);

  return (
    <TouchableOpacity style={styles.card} activeOpacity={0.85} onPress={onPress}>
      {/* Category header & Verification Badge */}
      <View style={styles.headerRow}>
        <View style={styles.categoryBadgeContainer}>
          <View style={[styles.iconCircle, { backgroundColor: badgeColor }]}>
            {getHazardIcon(report.category)}
          </View>
          <Text style={styles.categoryTitle}>
            {report.category} | {timeText}
          </Text>
        </View>

        {report.verified ? (
          <View style={styles.verifiedBadge}>
            <Text style={styles.verifiedText}>VERIFIED</Text>
          </View>
        ) : (
          <View style={styles.unverifiedBadge}>
            <Text style={styles.unverifiedText}>NOT VERIFIED</Text>
          </View>
        )}
      </View>

      {/* Description */}
      <Text style={styles.description} numberOfLines={2}>
        {report.description}
      </Text>

      {/* Water Depth if applicable */}
      {report.waterDepth ? (
        <View style={styles.waterDepthBadge}>
          <Ionicons name="water-outline" size={12} color="#0369A1" />
          <Text style={styles.waterDepthText}> Water Depth: {report.waterDepth}</Text>
        </View>
      ) : null}

      {/* GPS & Distance & Upvotes */}
      <View style={styles.footerRow}>
        <View style={styles.locationContainer}>
          <Ionicons name="location-sharp" size={12} color="#64748B" />
          <Text style={styles.gpsText}>
            GPS: {report.latitude?.toFixed(3)}, {report.longitude?.toFixed(3)}
          </Text>
          <Text style={styles.dotSeparator}>•</Text>
          <Text style={styles.distanceText}>{distanceText}</Text>
        </View>

        <TouchableOpacity
          style={styles.upvoteButton}
          onPress={onUpvote}
          hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
        >
          <Ionicons name="caret-up" size={16} color="#F97316" />
          <Text style={styles.upvoteCount}>{report.upvotes || 0} votes</Text>
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 12,
    marginVertical: 5,
    marginHorizontal: 12,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    borderWidth: 1,
    borderColor: "#F1F5F9",
  },
  headerRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 6,
  },
  categoryBadgeContainer: {
    flexDirection: "row",
    alignItems: "center",
    flex: 1,
  },
  iconCircle: {
    width: 24,
    height: 24,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
    marginRight: 6,
  },
  categoryTitle: {
    fontWeight: "700",
    fontSize: 12,
    color: "#0F172A",
    flexShrink: 1,
  },
  verifiedBadge: {
    backgroundColor: "#DCFCE7",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: "#16A34A",
  },
  verifiedText: {
    color: "#16A34A",
    fontSize: 9,
    fontWeight: "900",
    letterSpacing: 0.5,
  },
  unverifiedBadge: {
    backgroundColor: "#FEF3C7",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: "#F59E0B",
  },
  unverifiedText: {
    color: "#B45309",
    fontSize: 9,
    fontWeight: "900",
    letterSpacing: 0.5,
  },
  description: {
    fontSize: 13,
    lineHeight: 18,
    color: "#334155",
    marginBottom: 6,
  },
  waterDepthBadge: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#E0F2FE",
    paddingHorizontal: 6,
    paddingVertical: 3,
    borderRadius: 4,
    alignSelf: "flex-start",
    marginBottom: 6,
  },
  waterDepthText: {
    fontSize: 11,
    color: "#0369A1",
    fontWeight: "700",
  },
  footerRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingTop: 4,
    borderTopWidth: 1,
    borderTopColor: "#F8FAFC",
  },
  locationContainer: {
    flexDirection: "row",
    alignItems: "center",
  },
  gpsText: {
    fontSize: 11,
    color: "#64748B",
    marginLeft: 3,
  },
  dotSeparator: {
    color: "#94A3B8",
    marginHorizontal: 5,
    fontSize: 12,
  },
  distanceText: {
    fontSize: 11,
    fontWeight: "700",
    color: "#EA580C",
  },
  upvoteButton: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFF7ED",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 6,
  },
  upvoteCount: {
    fontSize: 11,
    fontWeight: "700",
    color: "#0F172A",
    marginLeft: 2,
  },
});
