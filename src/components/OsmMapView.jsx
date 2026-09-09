import React, { forwardRef, useEffect, useImperativeHandle, useRef } from "react";
import { StyleSheet } from "react-native";
import { WebView } from "react-native-webview";
import { getHazardColor } from "./HazardMarker";

const LEAFLET_HTML = `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  <style>
    html, body, #map { height: 100%; margin: 0; padding: 0; background: #e2e8f0; }
    .leaflet-control-attribution { font-size: 10px; }
    .user-dot {
      width: 16px; height: 16px; border-radius: 8px;
      background: #0284C7; border: 2px solid #fff;
      box-shadow: 0 0 0 6px rgba(56,189,248,0.35);
    }
    .dest-dot {
      width: 18px; height: 18px; border-radius: 9px;
      background: #16A34A; border: 2px solid #fff;
      box-shadow: 0 2px 4px rgba(0,0,0,0.35);
    }
    .hazard-pin {
      width: 28px; height: 28px; border-radius: 14px;
      border: 2px solid #fff; color: #fff;
      font-size: 14px; line-height: 24px; text-align: center;
      box-shadow: 0 2px 4px rgba(0,0,0,0.3);
    }
  </style>
</head>
<body>
  <div id="map"></div>
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
  <script>
    const map = L.map('map', { zoomControl: false, attributionControl: true }).setView([12.9141, 74.8560], 14);
    const tiles = L.tileLayer('https://tile.openstreetmap.de/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 18
    }).addTo(map);
    tiles.on('tileerror', function() {
      if (window._usedBackupTiles) return;
      window._usedBackupTiles = true;
      L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
        attribution: 'Tiles &copy; Esri &copy; OpenStreetMap',
        maxZoom: 18
      }).addTo(map);
    });

    let layers = L.layerGroup().addTo(map);

    function hazardIcon(color) {
      return L.divIcon({
        className: '',
        html: '<div class="hazard-pin" style="background:' + color + '">!</div>',
        iconSize: [28, 28],
        iconAnchor: [14, 28]
      });
    }

    const destIcon = L.divIcon({
      className: '',
      html: '<div class="dest-dot"></div>',
      iconSize: [18, 18],
      iconAnchor: [9, 9]
    });

    const userIcon = L.divIcon({
      className: '',
      html: '<div class="user-dot"></div>',
      iconSize: [16, 16],
      iconAnchor: [8, 8]
    });

    map.on('click', function(e) {
      if (window.ReactNativeWebView) {
        window.ReactNativeWebView.postMessage(JSON.stringify({
          type: 'mapTap',
          latitude: e.latlng.lat,
          longitude: e.latlng.lng
        }));
      }
    });

    window.updateMap = function(payload) {
      layers.clearLayers();
      const data = typeof payload === 'string' ? JSON.parse(payload) : payload;

      (data.reports || []).forEach(function(r) {
        if (typeof r.latitude !== 'number' || typeof r.longitude !== 'number') return;
        const isFlood = r.category === 'FLOOD WATER' || r.category === 'WATERLOGGING';
        if (isFlood) {
          L.circle([r.latitude, r.longitude], {
            radius: 140,
            color: '#F97316',
            weight: 2,
            fillColor: '#F97316',
            fillOpacity: 0.15
          }).addTo(layers);
        }
        L.marker([r.latitude, r.longitude], { icon: hazardIcon(r.color || '#EA580C') })
          .on('click', function() {
            if (window.ReactNativeWebView) {
              window.ReactNativeWebView.postMessage(JSON.stringify({ type: 'marker', id: r.id }));
            }
          })
          .addTo(layers);
      });

      if (data.userLocation && typeof data.userLocation.latitude === 'number') {
        L.marker([data.userLocation.latitude, data.userLocation.longitude], { icon: userIcon }).addTo(layers);
      }

      if (data.destination && typeof data.destination.latitude === 'number') {
        L.marker([data.destination.latitude, data.destination.longitude], { icon: destIcon })
          .bindTooltip(data.destination.name || 'Destination', { permanent: false })
          .addTo(layers);
      }

      if (data.route && data.route.length > 1) {
        const latlngs = data.route.map(function(p) { return [p.latitude, p.longitude]; });
        L.polyline(latlngs, { color: '#0284C7', weight: 5, opacity: 0.9 }).addTo(layers);
        map.fitBounds(latlngs, { padding: [28, 28] });
      }
    };

    window.flyTo = function(lat, lng, delta) {
      const zoom = delta && delta < 0.008 ? 16 : 14;
      map.flyTo([lat, lng], zoom, { duration: 0.6 });
    };

    document.addEventListener('message', handleRN);
    window.addEventListener('message', handleRN);
    function handleRN(event) {
      try {
        const msg = typeof event.data === 'string' ? JSON.parse(event.data) : event.data;
        if (msg.type === 'update') window.updateMap(msg.payload);
        if (msg.type === 'flyTo') window.flyTo(msg.lat, msg.lng, msg.delta);
      } catch (e) {}
    }
  </script>
</body>
</html>`;

function buildPayload(userLocation, reports, destination, routeCoordinates) {
  return {
    userLocation: userLocation
      ? { latitude: userLocation.latitude, longitude: userLocation.longitude }
      : null,
    destination: destination
      ? {
          latitude: destination.latitude,
          longitude: destination.longitude,
          name: destination.name || "Destination",
        }
      : null,
    route: (routeCoordinates || []).filter(
      (p) => typeof p.latitude === "number" && typeof p.longitude === "number"
    ),
    reports: (reports || [])
      .filter((r) => typeof r.latitude === "number" && typeof r.longitude === "number")
      .map((r) => ({
        id: r.id,
        latitude: r.latitude,
        longitude: r.longitude,
        category: r.category,
        color: getHazardColor(r.category),
      })),
  };
}

const OsmMapView = forwardRef(function OsmMapView(
  { userLocation, reports, destination, routeCoordinates, onMarkerPress, onMapPress, style },
  ref
) {
  const webRef = useRef(null);

  const post = (message) => {
    const data = JSON.stringify(message);
    webRef.current?.postMessage(data);
    webRef.current?.injectJavaScript(
      `window.dispatchEvent(new MessageEvent('message', { data: ${JSON.stringify(data)} })); true;`
    );
  };

  useImperativeHandle(ref, () => ({
    animateToRegion: (region) => {
      post({
        type: "flyTo",
        lat: region.latitude,
        lng: region.longitude,
        delta: region.latitudeDelta,
      });
    },
  }));

  const syncMap = () => {
    post({
      type: "update",
      payload: buildPayload(userLocation, reports, destination, routeCoordinates),
    });
  };

  useEffect(() => {
    syncMap();
  }, [userLocation, reports, destination, routeCoordinates]);

  return (
    <WebView
      ref={webRef}
      originWhitelist={["*"]}
      source={{ html: LEAFLET_HTML, baseUrl: "https://unpkg.com/" }}
      style={[styles.map, style]}
      javaScriptEnabled
      domStorageEnabled
      mixedContentMode="always"
      userAgent="Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 Abhaya/1.0"
      androidLayerType="hardware"
      setSupportMultipleWindows={false}
      onLoadEnd={syncMap}
      onMessage={(event) => {
        try {
          const msg = JSON.parse(event.nativeEvent.data);
          if (msg.type === "marker" && onMarkerPress) {
            const report = (reports || []).find((r) => r.id === msg.id);
            if (report) onMarkerPress(report);
          }
          if (msg.type === "mapTap" && onMapPress) {
            onMapPress({
              latitude: msg.latitude,
              longitude: msg.longitude,
            });
          }
        } catch (_e) {}
      }}
    />
  );
});

const styles = StyleSheet.create({
  map: {
    flex: 1,
    backgroundColor: "#E2E8F0",
  },
});

export default OsmMapView;
