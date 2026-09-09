import * as Location from "expo-location";

export const DEFAULT_MANGALURU_LOCATION = {
  latitude: 12.9141,
  longitude: 74.8560,
  latitudeDelta: 0.015,
  longitudeDelta: 0.015,
};

/**
 * Requests location permission and gets initial position
 */
export async function getInitialUserLocation() {
  try {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== "granted") {
      console.warn("Location permission not granted, using default coordinates.");
      return {
        location: {
          latitude: DEFAULT_MANGALURU_LOCATION.latitude,
          longitude: DEFAULT_MANGALURU_LOCATION.longitude,
        },
        permissionGranted: false,
      };
    }

    const position = await Location.getCurrentPositionAsync({
      accuracy: Location.Accuracy.Balanced,
    });

    return {
      location: {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy,
      },
      permissionGranted: true,
    };
  } catch (error) {
    console.warn("Error obtaining initial GPS location:", error);
    return {
      location: {
        latitude: DEFAULT_MANGALURU_LOCATION.latitude,
        longitude: DEFAULT_MANGALURU_LOCATION.longitude,
      },
      permissionGranted: false,
      error: error.message,
    };
  }
}

/**
 * Starts continuous watching of user GPS position (updates while walking)
 */
export async function watchUserLocation(onLocationUpdate, onError) {
  try {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== "granted") {
      if (onError) onError(new Error("Permission denied"));
      return null;
    }

    const subscription = await Location.watchPositionAsync(
      {
        accuracy: Location.Accuracy.High,
        timeInterval: 3000,
        distanceInterval: 3, // update every 3 meters walked
      },
      (position) => {
        onLocationUpdate({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          heading: position.coords.heading,
          speed: position.coords.speed,
          accuracy: position.coords.accuracy,
        });
      }
    );

    return subscription;
  } catch (err) {
    if (onError) onError(err);
    return null;
  }
}
