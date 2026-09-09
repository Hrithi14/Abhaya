import { useState, useEffect, useRef } from "react";
import * as Location from "expo-location";

export interface LocationData {
  latitude: number;
  longitude: number;
  accuracy: number;
  available: boolean;
}

export function useLocation() {
  const [location, setLocation] = useState<LocationData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [permissionDenied, setPermissionDenied] = useState(false);
  const subRef = useRef<Location.LocationSubscription | null>(null);

  const requestAndWatch = async () => {
    setIsLoading(true);
    try {
      // Request permission
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        setPermissionDenied(true);
        setLocation({ latitude: 0, longitude: 0, accuracy: 0, available: false });
        setIsLoading(false);
        return;
      }

      // Get immediate position first
      try {
        const pos = await Location.getCurrentPositionAsync({
          accuracy: Location.Accuracy.BestForNavigation,
        });
        setLocation({
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
          accuracy: pos.coords.accuracy ?? 0,
          available: true,
        });
        setIsLoading(false);
      } catch {
        // fallback to last known
        const last = await Location.getLastKnownPositionAsync();
        if (last) {
          setLocation({
            latitude: last.coords.latitude,
            longitude: last.coords.longitude,
            accuracy: last.coords.accuracy ?? 999,
            available: true,
          });
        }
        setIsLoading(false);
      }

      // Then watch for live updates
      subRef.current?.remove();
      subRef.current = await Location.watchPositionAsync(
        {
          accuracy: Location.Accuracy.BestForNavigation,
          timeInterval: 3000,      // update every 3 seconds
          distanceInterval: 5,     // or every 5 meters
        },
        (pos) => {
          setLocation({
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude,
            accuracy: pos.coords.accuracy ?? 0,
            available: true,
          });
          setIsLoading(false);
        }
      );
    } catch (e) {
      setLocation({ latitude: 0, longitude: 0, accuracy: 0, available: false });
      setIsLoading(false);
    }
  };

  useEffect(() => {
    requestAndWatch();
    return () => {
      subRef.current?.remove();
    };
  }, []);

  return { location, isLoading, permissionDenied, refresh: requestAndWatch };
}
