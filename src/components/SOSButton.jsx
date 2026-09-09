import React from "react";
import { TouchableOpacity, Text, StyleSheet, Linking, Alert, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export default function SOSButton({ style }) {
  const handleSOSCall = async () => {
    const url = "tel:112";
    try {
      const supported = await Linking.canOpenURL(url);
      if (supported) {
        await Linking.openURL(url);
      } else {
        Alert.alert(
          "Emergency 112",
          "Dialing 112 directly. Please dial 112 on your phone keypad immediately for Police, Fire, and Flood Ambulance."
        );
      }
    } catch (err) {
      Alert.alert("Emergency 112", "Calling 112...");
      Linking.openURL(url);
    }
  };

  return (
    <TouchableOpacity
      style={[styles.button, style]}
      activeOpacity={0.85}
      onPress={handleSOSCall}
    >
      <View style={styles.iconCircle}>
        <Ionicons name="call" size={16} color="#DC2626" />
      </View>
      <Text style={styles.text}>SOS / CALL 112</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    backgroundColor: "#DC2626", // Emergency Red strictly for SOS
    borderRadius: 28,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 18,
    paddingVertical: 12,
    elevation: 6,
    shadowColor: "#000",
    shadowOpacity: 0.35,
    shadowOffset: { width: 0, height: 4 },
    shadowRadius: 6,
  },
  iconCircle: {
    width: 26,
    height: 26,
    borderRadius: 13,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 8,
  },
  text: {
    color: "#FFFFFF",
    fontWeight: "900",
    fontSize: 14,
    letterSpacing: 0.8,
  },
});
