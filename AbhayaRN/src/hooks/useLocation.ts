import { useState, useEffect } from "react";
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

  const requestAndGet = async () => {
    setIsLoading(true);
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        setPermissionDenied(true);
        setLocation({ latitude: 0, longitude: 0, accuracy: 0, available: false });
        setIsLoading(false);
        return;
      }
      const loc = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.High });
      setLocation({
        latitude: loc.coords.latitude,
        longitude: loc.coords.longitude,
        accuracy: loc.coords.accuracy ?? 0,
        available: true,
      });
    } catch {
      setLocation({ latitude: 0, longitude: 0, accuracy: 0, available: false });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    requestAndGet();
    // Watch for updates
    let sub: Location.LocationSubscription | null = null;
    Location.watchPositionAsync(
      { accuracy: Location.Accuracy.High, timeInterval: 5000, distanceInterval: 5 },
      (loc) => {
        setLocation({
          latitude: loc.coords.latitude,
          longitude: loc.coords.longitude,
          accuracy: loc.coords.accuracy ?? 0,
          available: true,
        });
        setIsLoading(false);
      }
    ).then((s) => { sub = s; }).catch(() => {});

    return () => { sub?.remove(); };
  }, []);

  return { location, isLoading, permissionDenied, refresh: requestAndGet };
}
