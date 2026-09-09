import React from "react";
import {
  View,
  Text,
  Modal,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Image,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { calculateDistance, formatDistance, formatTimeAgo } from "../utils/distance";
import { getHazardIcon, getHazardColor } from "./HazardMarker";

export default function ReportDetailModal({
  visible,
  report,
  userLocation,
  onClose,
  onUpvote,
  onLocateOnMap,
}) {
  if (!report) return null;

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
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <View style={styles.overlay}>
        <View style={styles.modalContent}>
          {/* Header */}
          <View style={styles.modalHeader}>
            <View style={styles.categoryRow}>
              <View style={[styles.iconCircle, { backgroundColor: badgeColor }]}>
                {getHazardIcon(report.category)}
              </View>
              <View>
                <Text style={styles.categoryTitle}>{report.category}</Text>
                <Text style={styles.timeText}>{timeText}</Text>
              </View>
            </View>

            <TouchableOpacity onPress={onClose} style={styles.closeBtn}>
              <Ionicons name="close" size={22} color="#64748B" />
            </TouchableOpacity>
          </View>

          <ScrollView style={styles.body}>
            {/* Verification Status */}
            <View style={styles.statusRow}>
              {report.verified ? (
                <View style={styles.verifiedBadge}>
                  <Ionicons name="shield-checkmark" size={14} color="#16A34A" />
                  <Text style={styles.verifiedText}> VERIFIED HAZARD ALERT</Text>
                </View>
              ) : (
                <View style={styles.unverifiedBadge}>
                  <Ionicons name="alert-circle" size={14} color="#B45309" />
                  <Text style={styles.unverifiedText}> COMMUNITY REPORT (UNVERIFIED)</Text>
                </View>
              )}
            </View>

            {/* Description */}
            <Text style={styles.descriptionText}>{report.description}</Text>

            {/* Water Depth */}
            {report.waterDepth ? (
              <View style={styles.detailBox}>
                <Ionicons name="water" size={18} color="#0284C7" />
                <View style={styles.detailTextContainer}>
                  <Text style={styles.detailLabel}>Water Depth</Text>
                  <Text style={styles.detailValue}>{report.waterDepth}</Text>
                </View>
              </View>
            ) : null}

            {/* Location coordinates & distance */}
            <View style={styles.detailBox}>
              <Ionicons name="location" size={18} color="#F97316" />
              <View style={styles.detailTextContainer}>
                <Text style={styles.detailLabel}>GPS Coordinates</Text>
                <Text style={styles.detailValue}>
                  {report.latitude?.toFixed(5)}, {report.longitude?.toFixed(5)}
                </Text>
                <Text style={styles.distanceHighlight}>
                  Distance from your location: {distanceText}
                </Text>
              </View>
            </View>

            {/* Optional Image */}
            {report.imageUrl ? (
              <View style={styles.imageContainer}>
                <Image
                  source={{ uri: report.imageUrl }}
                  style={styles.hazardImage}
                  resizeMode="cover"
                />
              </View>
            ) : null}
          </ScrollView>

          {/* Action Buttons */}
          <View style={styles.actionsRow}>
            <TouchableOpacity
              style={styles.upvoteButton}
              onPress={() => onUpvote(report.id)}
            >
              <Ionicons name="thumbs-up" size={18} color="#0F172A" />
              <Text style={styles.upvoteButtonText}> Upvote ({report.upvotes || 0})</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.locateButton}
              onPress={() => {
                onLocateOnMap(report);
                onClose();
              }}
            >
              <Ionicons name="navigate" size={18} color="#FFFFFF" />
              <Text style={styles.locateButtonText}> Locate on Map</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: "rgba(15, 23, 42, 0.6)",
    justifyContent: "flex-end",
  },
  modalContent: {
    backgroundColor: "#FFFFFF",
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 20,
    maxHeight: "80%",
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  categoryRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  iconCircle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  categoryTitle: {
    fontSize: 16,
    fontWeight: "900",
    color: "#0F172A",
  },
  timeText: {
    fontSize: 11,
    color: "#64748B",
  },
  closeBtn: {
    padding: 6,
  },
  body: {
    marginVertical: 6,
  },
  statusRow: {
    marginBottom: 10,
  },
  verifiedBadge: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#DCFCE7",
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
    alignSelf: "flex-start",
  },
  verifiedText: {
    fontSize: 11,
    color: "#16A34A",
    fontWeight: "800",
  },
  unverifiedBadge: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FEF3C7",
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
    alignSelf: "flex-start",
  },
  unverifiedText: {
    fontSize: 11,
    color: "#B45309",
    fontWeight: "800",
  },
  descriptionText: {
    fontSize: 14,
    lineHeight: 20,
    color: "#1E293B",
    marginBottom: 14,
  },
  detailBox: {
    flexDirection: "row",
    alignItems: "flex-start",
    backgroundColor: "#F8FAFC",
    padding: 10,
    borderRadius: 8,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
  },
  detailTextContainer: {
    marginLeft: 8,
    flex: 1,
  },
  detailLabel: {
    fontSize: 11,
    color: "#64748B",
    fontWeight: "600",
  },
  detailValue: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0F172A",
  },
  distanceHighlight: {
    fontSize: 12,
    color: "#EA580C",
    fontWeight: "700",
    marginTop: 2,
  },
  imageContainer: {
    borderRadius: 8,
    overflow: "hidden",
    marginVertical: 8,
  },
  hazardImage: {
    width: "100%",
    height: 180,
  },
  actionsRow: {
    flexDirection: "row",
    marginTop: 14,
    gap: 10,
  },
  upvoteButton: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: "#CBD5E1",
    borderRadius: 8,
    paddingVertical: 12,
  },
  upvoteButtonText: {
    fontSize: 13,
    fontWeight: "700",
    color: "#0F172A",
  },
  locateButton: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#F97316",
    borderRadius: 8,
    paddingVertical: 12,
  },
  locateButtonText: {
    fontSize: 13,
    fontWeight: "800",
    color: "#FFFFFF",
  },
});
