/**
 * OSMMap — Full OpenStreetMap via Leaflet.js running inside a WebView.
 * Supports: live user location pulse, hazard markers, flood zone circles,
 * zoom controls, tap-to-select markers, recenter button.
 * Matches the Kotlin app's OpenStreetMapCompose feature set exactly.
 */
import React, { useRef, useEffect, useCallback } from "react";
import { StyleSheet, View, Platform } from "react-native";
import { WebView } from "react-native-webview";
import type { HazardReport } from "../services/hazardStore";
import { HAZARD_CATEGORY_EMOJI } from "../services/hazardStore";

interface Props {
  userLat: number;
  userLon: number;
  reports: HazardReport[];
  onMarkerPress?: (reportId: string) => void;
  height?: number;
}

function buildHtml(
  userLat: number,
  userLon: number,
  reports: HazardReport[]
): string {
  // Serialise reports to safe JSON
  const reportsJson = JSON.stringify(
    reports.map((r) => ({
      id: r.id,
      lat: r.latitude,
      lon: r.longitude,
      category: r.category,
      emoji: HAZARD_CATEGORY_EMOJI[r.category] ?? "⚠️",
      description: r.description.replace(/'/g, "\\'"),
      verified: r.verified,
      upvotes: r.upvotes,
      waterDepth: r.waterDepth ?? "",
      radiusMeters: r.radiusMeters,
    }))
  );

  return `<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  html, body, #map { width: 100%; height: 100%; background: #e5e7eb; }
  .hazard-marker {
    display: flex; align-items: center; justify-content: center;
    width: 36px; height: 36px; border-radius: 50%;
    font-size: 18px; border: 2px solid #fff;
    box-shadow: 0 2px 6px rgba(0,0,0,0.4);
  }
  .hazard-flood    { background: #F97316; }
  .hazard-water    { background: #0284C7; }
  .hazard-tree     { background: #16A34A; }
  .hazard-road     { background: #D97706; }
  .hazard-fire     { background: #DC2626; }
  .hazard-electric { background: #7C3AED; }
  .hazard-medical  { background: #DC2626; }
  .hazard-default  { background: #D97706; }
  .hazard-verified { border-color: #16A34A !important; border-width: 3px !important; }
  .user-dot {
    width: 18px; height: 18px; border-radius: 50%;
    background: #0284C7; border: 3px solid #fff;
    box-shadow: 0 0 0 6px rgba(2,132,199,0.25);
    animation: pulse 2s ease-out infinite;
  }
  @keyframes pulse {
    0%   { box-shadow: 0 0 0 0 rgba(2,132,199,0.5); }
    70%  { box-shadow: 0 0 0 14px rgba(2,132,199,0); }
    100% { box-shadow: 0 0 0 0 rgba(2,132,199,0); }
  }
</style>
</head>
<body>
<div id="map"></div>
<script>
  var map = L.map('map', { zoomControl: false, attributionControl: true })
    .setView([${userLat}, ${userLon}], 14);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 19
  }).addTo(map);

  // Custom zoom control (bottom-right)
  L.control.zoom({ position: 'bottomright' }).addTo(map);

  // ── User location marker (animated pulse) ──────────────────────────────
  var userIcon = L.divIcon({
    className: '', html: '<div class="user-dot"></div>',
    iconSize: [18, 18], iconAnchor: [9, 9]
  });
  var userMarker = L.marker([${userLat}, ${userLon}], { icon: userIcon, zIndexOffset: 9999 })
    .addTo(map)
    .bindPopup('<b>📍 Your Location</b>');

  // ── Hazard markers + flood circles ─────────────────────────────────────
  var reports = ${reportsJson};

  function categoryClass(cat) {
    if (cat === 'FLOOD_WATER' || cat === 'WATERLOGGING') return 'hazard-flood';
    if (cat === 'FALLEN_TREE')    return 'hazard-tree';
    if (cat === 'OPEN_WIRE')      return 'hazard-electric';
    if (cat === 'FIRE' || cat === 'MEDICAL_EMERGENCY') return 'hazard-fire';
    if (cat === 'ROADBLOCK' || cat === 'POTHOLE') return 'hazard-road';
    return 'hazard-default';
  }

  reports.forEach(function(r) {
    // Flood danger zone circle
    if (r.category === 'FLOOD_WATER' || r.category === 'WATERLOGGING') {
      L.circle([r.lat, r.lon], {
        radius: r.radiusMeters,
        color: '#F97316', fillColor: '#F97316',
        fillOpacity: 0.18, weight: 2, opacity: 0.7
      }).addTo(map);
    }

    // Marker
    var cls = categoryClass(r.category) + (r.verified ? ' hazard-verified' : '');
    var icon = L.divIcon({
      className: '',
      html: '<div class="hazard-marker ' + cls + '">' + r.emoji + '</div>',
      iconSize: [36, 36], iconAnchor: [18, 36], popupAnchor: [0, -36]
    });

    var depthInfo = r.waterDepth ? '<br>💧 Depth: ' + r.waterDepth : '';
    var verifiedBadge = r.verified
      ? '<span style="color:#16A34A;font-weight:700"> ✓ VERIFIED</span>'
      : '<span style="color:#D97706;font-weight:600"> ⚠ NOT VERIFIED</span>';

    L.marker([r.lat, r.lon], { icon: icon })
      .addTo(map)
      .bindPopup(
        '<b>' + r.emoji + ' ' + r.category.replace(/_/g,' ') + '</b>' + verifiedBadge +
        '<br><small>' + r.description + '</small>' + depthInfo +
        '<br><small>👍 ' + r.upvotes + ' upvotes</small>' +
        '<br><small>📍 ' + r.lat.toFixed(4) + ', ' + r.lon.toFixed(4) + '</small>'
      )
      .on('click', function() {
        window.ReactNativeWebView && window.ReactNativeWebView.postMessage(
          JSON.stringify({ type: 'markerPress', id: r.id })
        );
      });
  });

  // ── Handle messages from React Native ─────────────────────────────────
  document.addEventListener('message', handleRNMessage);
  window.addEventListener('message', handleRNMessage);

  function handleRNMessage(e) {
    try {
      var msg = JSON.parse(e.data);
      if (msg.type === 'updateLocation') {
        var ll = [msg.lat, msg.lon];
        userMarker.setLatLng(ll);
      }
      if (msg.type === 'recenter') {
        map.setView([msg.lat, msg.lon], msg.zoom || 15, { animate: true });
        userMarker.setLatLng([msg.lat, msg.lon]);
      }
    } catch(err) {}
  }
</script>
</body>
</html>`;
}

export default function OSMMap({ userLat, userLon, reports, onMarkerPress, height = 320 }: Props) {
  const webViewRef = useRef<WebView>(null);
  const htmlRef = useRef<string>("");

  // Build initial HTML with all current reports
  const html = buildHtml(userLat, userLon, reports);
  htmlRef.current = html;

  // Push live location updates without full reload
  const prevLatRef = useRef(userLat);
  const prevLonRef = useRef(userLon);

  useEffect(() => {
    if (
      webViewRef.current &&
      (prevLatRef.current !== userLat || prevLonRef.current !== userLon)
    ) {
      const msg = JSON.stringify({ type: "updateLocation", lat: userLat, lon: userLon });
      webViewRef.current.postMessage(msg);
      prevLatRef.current = userLat;
      prevLonRef.current = userLon;
    }
  }, [userLat, userLon]);

  const handleRecenter = useCallback(() => {
    if (webViewRef.current) {
      const msg = JSON.stringify({ type: "recenter", lat: userLat, lon: userLon, zoom: 15 });
      webViewRef.current.postMessage(msg);
    }
  }, [userLat, userLon]);

  const handleMessage = useCallback(
    (event: { nativeEvent: { data: string } }) => {
      try {
        const msg = JSON.parse(event.nativeEvent.data);
        if (msg.type === "markerPress" && onMarkerPress) {
          onMarkerPress(msg.id);
        }
      } catch {}
    },
    [onMarkerPress]
  );

  return (
    <View style={[styles.container, { height }]}>
      <WebView
        ref={webViewRef}
        source={{ html: htmlRef.current }}
        style={styles.webview}
        onMessage={handleMessage}
        javaScriptEnabled
        domStorageEnabled
        originWhitelist={["*"]}
        mixedContentMode="always"
        // Allow loading OSM tiles (external network)
        allowsInlineMediaPlayback
        // Android hardware acceleration
        androidHardwareAccelerationDisabled={false}
      />
    </View>
  );
}

export { buildHtml };

const styles = StyleSheet.create({
  container: { width: "100%", overflow: "hidden", borderRadius: 0 },
  webview:   { flex: 1, backgroundColor: "#e5e7eb" },
});
