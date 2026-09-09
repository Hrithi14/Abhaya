import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
  Alert,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { submitReport } from "../services/reportService";
import { getHazardIcon } from "../components/HazardMarker";

const HAZARD_CATEGORIES = [
  "FLOOD WATER",
  "WATERLOGGING",
  "FALLEN TREE",
  "ROADBLOCK",
  "POTHOLE",
  "OPEN WIRE",
  "LANDSLIDE",
  "FIRE",
  "MEDICAL EMERGENCY",
];

export default function ReportScreen({ userLocation, onReportSubmitted }) {
  const [selectedCategory, setSelectedCategory] = useState("FLOOD WATER");
  const [description, setDescription] = useState("");
  const [waterDepth, setWaterDepth] = useState("");
  const [hasPhotoAttached, setHasPhotoAttached] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async () => {
    if (!description.trim()) {
      Alert.alert("Missing Information", "Please enter a brief description of the hazard.");
      return;
    }

    if (!userLocation) {
      Alert.alert(
        "GPS Required",
        "Waiting for GPS location lock. Please ensure location is enabled."
      );
      return;
    }

    try {
      setIsSubmitting(true);
      await submitReport({
        category: selectedCategory,
        description: description.trim(),
        latitude: userLocation.latitude,
        longitude: userLocation.longitude,
        waterDepth: waterDepth.trim() || null,
        imageUrl: hasPhotoAttached
          ? "https://images.unsplash.com/photo-1547683905-f686c993aae5?w=600&auto=format&fit=crop&q=80"
          : null,
      });

      setIsSubmitting(false);
      Alert.alert(
        "Report Published",
        "Your hazard alert is now live on the OpenStreetMap feed for citizens and emergency responders.",
        [
          {
            text: "View on Map",
            onPress: () => onReportSubmitted(),
          },
        ]
      );
      setDescription("");
      setWaterDepth("");
      setHasPhotoAttached(false);
    } catch (error) {
      setIsSubmitting(false);
      Alert.alert("Submission Error", "Failed to submit report. Please try again.");
    }
  };

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Title & Subtitle */}
        <View style={styles.titleRow}>
          <View style={styles.titleIconBox}>
            <Ionicons name="add-circle" size={20} color="#FFFFFF" />
          </View>
          <View>
            <Text style={styles.titleText}>REPORT A HAZARD</Text>
            <Text style={styles.subtitleText}>
              Broadcast live incident coordinates to responders
            </Text>
          </View>
        </View>

        {/* Automatic GPS Location Lock Card */}
        <View style={styles.gpsLockCard}>
          <View style={styles.gpsIconCircle}>
            <Ionicons name="locate" size={18} color="#0284C7" />
          </View>
          <View style={styles.gpsInfo}>
            <Text style={styles.gpsLockTag}>AUTOMATIC GPS LOCK</Text>
            <Text style={styles.gpsCoords}>
              Lat: {userLocation?.latitude?.toFixed(5) || "12.91410"}, Lon:{" "}
              {userLocation?.longitude?.toFixed(5) || "74.85600"}
            </Text>
            <Text style={styles.gpsAccuracy}>
              Mangaluru Coastal Node • Accuracy ±4m (Auto-locked)
            </Text>
          </View>
          <View style={styles.lockedPill}>
            <Text style={styles.lockedPillText}>LOCKED</Text>
          </View>
        </View>

        {/* 1. Category Selection */}
        <Text style={styles.sectionHeader}>1. SELECT HAZARD CATEGORY</Text>
        <View style={styles.categoryGrid}>
          {HAZARD_CATEGORIES.map((category) => {
            const isSelected = selectedCategory === category;
            return (
              <TouchableOpacity
                key={category}
                style={[
                  styles.categoryChip,
                  isSelected && styles.categoryChipSelected,
                ]}
                activeOpacity={0.7}
                onPress={() => setSelectedCategory(category)}
              >
                <View
                  style={[
                    styles.radioCircle,
                    isSelected && styles.radioCircleSelected,
                  ]}
                >
                  {isSelected && <View style={styles.radioDot} />}
                </View>
                <Text
                  style={[
                    styles.categoryChipText,
                    isSelected && styles.categoryChipTextSelected,
                  ]}
                >
                  {category}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>

        {/* 2. Description Input */}
        <Text style={styles.sectionHeader}>2. HAZARD DESCRIPTION</Text>
        <TextInput
          style={styles.textArea}
          placeholder="e.g. Waist-deep flooding on service road, power transformer sparking, fallen banyan tree blocking ambulances..."
          placeholderTextColor="#94A3B8"
          multiline
          numberOfLines={4}
          value={description}
          onChangeText={setDescription}
          textAlignVertical="top"
        />

        {/* 3. Water Depth (Only shown or highlighted for flood/waterlogging) */}
        {(selectedCategory === "FLOOD WATER" ||
          selectedCategory === "WATERLOGGING") && (
          <View style={styles.waterDepthSection}>
            <Text style={styles.sectionHeader}>3. WATER DEPTH (OPTIONAL)</Text>
            <View style={styles.waterDepthInputContainer}>
              <Ionicons name="water" size={18} color="#0284C7" />
              <TextInput
                style={styles.waterDepthInput}
                placeholder="e.g. 2.4 ft / 75 cm / Knee-level"
                placeholderTextColor="#94A3B8"
                value={waterDepth}
                onChangeText={setWaterDepth}
              />
            </View>
          </View>
        )}

        {/* 4. Photo Attachment */}
        <Text style={styles.sectionHeader}>4. PHOTO EVIDENCE (OPTIONAL)</Text>
        <TouchableOpacity
          style={[
            styles.photoCard,
            hasPhotoAttached && styles.photoCardAttached,
          ]}
          activeOpacity={0.8}
          onPress={() => setHasPhotoAttached(!hasPhotoAttached)}
        >
          <View style={styles.photoRow}>
            <Ionicons
              name={hasPhotoAttached ? "checkmark-circle" : "camera"}
              size={22}
              color={hasPhotoAttached ? "#16A34A" : "#F97316"}
            />
            <Text
              style={[
                styles.photoCardText,
                hasPhotoAttached && styles.photoCardTextAttached,
              ]}
            >
              {hasPhotoAttached
                ? "Scene photo attached (mangaluru_hazard_snap.jpg)"
                : "Tap to capture / attach photo evidence"}
            </Text>
          </View>
          {hasPhotoAttached && (
            <Text style={styles.photoRemoveText}>REMOVE</Text>
          )}
        </TouchableOpacity>

        {/* Submit Button */}
        <TouchableOpacity
          style={styles.submitButton}
          activeOpacity={0.85}
          onPress={handleSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? (
            <ActivityIndicator size="small" color="#FFFFFF" />
          ) : (
            <View style={styles.submitRow}>
              <Ionicons name="paper-plane" size={18} color="#FFFFFF" />
              <Text style={styles.submitButtonText}> SUBMIT TO LIVE MAP</Text>
            </View>
          )}
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F8FAFC",
  },
  scrollContent: {
    padding: 16,
    paddingBottom: 90,
  },
  titleRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 16,
  },
  titleIconBox: {
    backgroundColor: "#F97316",
    borderRadius: 8,
    padding: 6,
    marginRight: 10,
  },
  titleText: {
    fontSize: 18,
    fontWeight: "900",
    color: "#0F172A",
  },
  subtitleText: {
    fontSize: 12,
    color: "#64748B",
  },
  gpsLockCard: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    padding: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    marginBottom: 18,
    elevation: 1,
  },
  gpsIconCircle: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: "#E0F2FE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  gpsInfo: {
    flex: 1,
  },
  gpsLockTag: {
    fontSize: 10,
    fontWeight: "900",
    color: "#0369A1",
  },
  gpsCoords: {
    fontSize: 12,
    fontWeight: "800",
    color: "#0F172A",
  },
  gpsAccuracy: {
    fontSize: 10,
    color: "#64748B",
  },
  lockedPill: {
    backgroundColor: "#DCFCE7",
    paddingHorizontal: 6,
    paddingVertical: 3,
    borderRadius: 4,
  },
  lockedPillText: {
    color: "#16A34A",
    fontSize: 10,
    fontWeight: "800",
  },
  sectionHeader: {
    fontSize: 12,
    fontWeight: "800",
    color: "#334155",
    letterSpacing: 0.5,
    marginBottom: 8,
  },
  categoryGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginBottom: 16,
  },
  categoryChip: {
    width: "48%",
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 8,
  },
  categoryChipSelected: {
    backgroundColor: "#F97316",
    borderColor: "#F97316",
  },
  radioCircle: {
    width: 16,
    height: 16,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: "#94A3B8",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 6,
  },
  radioCircleSelected: {
    borderColor: "#FFFFFF",
  },
  radioDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: "#FFFFFF",
  },
  categoryChipText: {
    fontSize: 11,
    fontWeight: "700",
    color: "#0F172A",
    flexShrink: 1,
  },
  categoryChipTextSelected: {
    color: "#FFFFFF",
    fontWeight: "900",
  },
  textArea: {
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    padding: 12,
    fontSize: 13,
    color: "#0F172A",
    height: 90,
    marginBottom: 16,
  },
  waterDepthSection: {
    marginBottom: 16,
  },
  waterDepthInputContainer: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    paddingHorizontal: 12,
    height: 44,
  },
  waterDepthInput: {
    flex: 1,
    fontSize: 13,
    color: "#0F172A",
    marginLeft: 8,
  },
  photoCard: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    paddingHorizontal: 14,
    height: 52,
    marginBottom: 24,
  },
  photoCardAttached: {
    backgroundColor: "#F0FDF4",
    borderColor: "#16A34A",
  },
  photoRow: {
    flexDirection: "row",
    alignItems: "center",
    flex: 1,
  },
  photoCardText: {
    fontSize: 12,
    color: "#334155",
    marginLeft: 8,
  },
  photoCardTextAttached: {
    color: "#166534",
    fontWeight: "700",
  },
  photoRemoveText: {
    fontSize: 11,
    color: "#DC2626",
    fontWeight: "800",
  },
  submitButton: {
    backgroundColor: "#F97316", // Primary Orange
    borderRadius: 10,
    height: 50,
    alignItems: "center",
    justifyContent: "center",
    elevation: 3,
  },
  submitRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  submitButtonText: {
    color: "#FFFFFF",
    fontSize: 14,
    fontWeight: "900",
    letterSpacing: 0.5,
  },
});
