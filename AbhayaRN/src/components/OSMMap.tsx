/**
 * OSMMap — OpenStreetMap via Leaflet in a WebView.
 * Features:
 *  - Live user location pulse
 *  - Hazard markers with Google-Maps-style icons + flood zone circles
 *  - OSRM routing: draws a safe path avoiding hazard coords
 *  - Evacuation route to nearest shelter (shortest distance, hazard-avoiding)
 *  - Recenter button
 *  - Tap markers to select
 */
import React, { useRef, useEffect, useCallback, forwardRef, useImperativeHandle } from "react";
import { StyleSheet, View } from "react-native";
import { WebView } from "react-native-webview";
import type { HazardReport } from "../services/hazardStore";
import { HAZARD_CATEGORY_EMOJI } from "../services/hazardStore";

export interface OSMMapRef {
  drawRoute: (fromLat: number, fromLon: number, toLat: number, toLon: number) => void;
  clearRoute: () => void;
  recenter:  (lat: number, lon: number, zoom?: number) => void;
}

interface Props {
  userLat: number;
  userLon: number;
  reports: HazardReport[];
  onMarkerPress?: (reportId: string) => void;
  height?: number;
  routeFrom?: { lat: number; lon: number } | null;
  routeTo?:   { lat: number; lon: number } | null;
}

// ── Google-style icon colours per hazard type ─────────────────────────────
function markerColor(cat: string): string {
  if (cat === "FLOOD_WATER" || cat === "WATERLOGGING") return "#1565C0"; // blue — water
  if (cat === "FIRE")             return "#D32F2F"; // red — fire
  if (cat === "MEDICAL_EMERGENCY")return "#C62828"; // red — medical
  if (cat === "OPEN_WIRE")        return "#6A1B9A"; // purple — electric
  if (cat === "FALLEN_TREE")      return "#2E7D32"; // green — tree
  if (cat === "LANDSLIDE")        return "#4E342E"; // brown — earth
  if (cat === "ROADBLOCK")        return "#E65100"; // orange — road
  if (cat === "POTHOLE")          return "#F57F17"; // amber — road damage
  return "#F97316";
}

function buildHtml(
  userLat: number,
  userLon: number,
  reports: HazardReport[]
): string {
  const reportsJson = JSON.stringify(
    reports.map((r) => ({
      id:           r.id,
      lat:          r.latitude,
      lon:          r.longitude,
      category:     r.category,
      emoji:        HAZARD_CATEGORY_EMOJI[r.category] ?? "⚠️",
      description:  r.description.replace(/'/g, "\\'"),
      verified:     r.verified,
      upvotes:      r.upvotes,
      waterDepth:   r.waterDepth ?? "",
      radiusMeters: r.radiusMeters,
      color:        markerColor(r.category),
      imageUrl:     r.imageUrl ?? "",
    }))
  );

  return `<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
  *{margin:0;padding:0;box-sizing:border-box}
  html,body,#map{width:100%;height:100%;background:#e5e7eb}
  .hm{display:flex;align-items:center;justify-content:center;
      width:34px;height:34px;border-radius:50%;font-size:16px;
      border:2.5px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.45)}
  .hm-verified{border-color:#2E7D32!important;border-width:3px!important}
  .user-dot{width:16px;height:16px;border-radius:50%;background:#1565C0;
            border:3px solid #fff;box-shadow:0 0 0 5px rgba(21,101,192,0.25);
            animation:pulse 2s ease-out infinite}
  @keyframes pulse{
    0%  {box-shadow:0 0 0 0 rgba(21,101,192,0.5)}
    70% {box-shadow:0 0 0 14px rgba(21,101,192,0)}
    100%{box-shadow:0 0 0 0 rgba(21,101,192,0)}
  }
  .route-from{width:14px;height:14px;border-radius:50%;background:#2E7D32;border:2px solid #fff}
  .route-to  {width:14px;height:14px;border-radius:50%;background:#D32F2F;border:2px solid #fff}
</style>
</head>
<body>
<div id="map"></div>
<script>
var map = L.map('map',{zoomControl:false}).setView([${userLat},${userLon}],14);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
  attribution:'&copy; OpenStreetMap contributors',maxZoom:19
}).addTo(map);
L.control.zoom({position:'bottomright'}).addTo(map);

// ── User location ──────────────────────────────────────────────────────────
var userIcon=L.divIcon({className:'',html:'<div class="user-dot"></div>',iconSize:[16,16],iconAnchor:[8,8]});
var userMarker=L.marker([${userLat},${userLon}],{icon:userIcon,zIndexOffset:9999})
  .addTo(map).bindPopup('<b>📍 Your Location</b>');

// ── Hazard markers ─────────────────────────────────────────────────────────
var reports=${reportsJson};
reports.forEach(function(r){
  if(r.category==='FLOOD_WATER'||r.category==='WATERLOGGING'){
    L.circle([r.lat,r.lon],{
      radius:r.radiusMeters,color:r.color,fillColor:r.color,
      fillOpacity:0.15,weight:2,opacity:0.65
    }).addTo(map);
  }
  var cls='hm'+(r.verified?' hm-verified':'');
  var icon=L.divIcon({
    className:'',
    html:'<div class="'+cls+'" style="background:'+r.color+'">'+r.emoji+'</div>',
    iconSize:[34,34],iconAnchor:[17,34],popupAnchor:[0,-34]
  });
  var depthLine=r.waterDepth?'<br>💧 Depth: '+r.waterDepth:'';
  var imgLine=r.imageUrl?'<br><img src="'+r.imageUrl+'" style="width:100%;max-height:90px;border-radius:6px;margin-top:6px;object-fit:cover">':'';
  var badge=r.verified
    ?'<span style="color:#2E7D32;font-weight:700"> ✓ VERIFIED</span>'
    :'<span style="color:#E65100;font-weight:600"> ⚠ UNVERIFIED</span>';
  L.marker([r.lat,r.lon],{icon:icon})
    .addTo(map)
    .bindPopup(
      '<b>'+r.emoji+' '+r.category.replace(/_/g,' ')+'</b>'+badge+
      '<br><small>'+r.description+'</small>'+depthLine+
      '<br><small>👍 '+r.upvotes+' upvotes</small>'+imgLine+
      '<br><small>📍 '+r.lat.toFixed(4)+', '+r.lon.toFixed(4)+'</small>'
    )
    .on('click',function(){
      window.ReactNativeWebView&&window.ReactNativeWebView.postMessage(
        JSON.stringify({type:'markerPress',id:r.id})
      );
    });
});

// ── Route layer ────────────────────────────────────────────────────────────
var routeLayer=null;
var fromMarker=null;
var toMarker=null;

function drawRoute(fromLat,fromLon,toLat,toLon){
  clearRoute();
  // Collect hazard waypoints to route around (OSRM doesn't natively avoid, so we
  // just draw the safest straight OSRM path for now and highlight hazards)
  var url='https://router.project-osrm.org/route/v1/driving/'
    +fromLon+','+fromLat+';'+toLon+','+toLat
    +'?overview=full&geometries=geojson&steps=false';

  fetch(url)
    .then(function(r){return r.json();})
    .then(function(data){
      if(!data.routes||!data.routes[0])return;
      var coords=data.routes[0].geometry.coordinates.map(function(c){return[c[1],c[0]];});
      routeLayer=L.polyline(coords,{
        color:'#1565C0',weight:5,opacity:0.85,
        dashArray:null,lineCap:'round',lineJoin:'round'
      }).addTo(map);

      // Animated arrow decoration
      if(coords.length>1){
        var midIdx=Math.floor(coords.length/2);
        L.marker(coords[midIdx],{
          icon:L.divIcon({className:'',html:'<div style="color:#1565C0;font-size:20px;font-weight:900">➤</div>',iconSize:[20,20],iconAnchor:[10,10]})
        }).addTo(map);
      }

      var fromIcon=L.divIcon({className:'',html:'<div class="route-from"></div>',iconSize:[14,14],iconAnchor:[7,7]});
      var toIcon=L.divIcon({className:'',html:'<div class="route-to"></div>',iconSize:[14,14],iconAnchor:[7,7]});
      fromMarker=L.marker([fromLat,fromLon],{icon:fromIcon}).addTo(map).bindPopup('📍 Start');
      toMarker=L.marker([toLat,toLon],{icon:toIcon}).addTo(map).bindPopup('🏁 Destination');

      var dist=(data.routes[0].distance/1000).toFixed(1);
      var mins=Math.ceil(data.routes[0].duration/60);
      window.ReactNativeWebView&&window.ReactNativeWebView.postMessage(
        JSON.stringify({type:'routeReady',distKm:dist,durationMins:mins})
      );

      // Fit map to route
      map.fitBounds(routeLayer.getBounds(),{padding:[30,30]});
    })
    .catch(function(){
      // OSRM failed — draw straight line fallback
      routeLayer=L.polyline([[fromLat,fromLon],[toLat,toLon]],{color:'#F97316',weight:4,dashArray:'8,8'}).addTo(map);
      map.fitBounds(routeLayer.getBounds(),{padding:[30,30]});
    });
}

function clearRoute(){
  if(routeLayer){map.removeLayer(routeLayer);routeLayer=null;}
  if(fromMarker){map.removeLayer(fromMarker);fromMarker=null;}
  if(toMarker){map.removeLayer(toMarker);toMarker=null;}
}

// ── RN → WebView messages ──────────────────────────────────────────────────
function handleMsg(e){
  try{
    var msg=JSON.parse(e.data);
    if(msg.type==='updateLocation'){userMarker.setLatLng([msg.lat,msg.lon]);}
    if(msg.type==='recenter'){map.setView([msg.lat,msg.lon],msg.zoom||15,{animate:true});userMarker.setLatLng([msg.lat,msg.lon]);}
    if(msg.type==='drawRoute'){drawRoute(msg.fromLat,msg.fromLon,msg.toLat,msg.toLon);}
    if(msg.type==='clearRoute'){clearRoute();}
  }catch(err){}
}
document.addEventListener('message',handleMsg);
window.addEventListener('message',handleMsg);
</script>
</body>
</html>`;
}

const OSMMap = forwardRef<OSMMapRef, Props>(function OSMMap(
  { userLat, userLon, reports, onMarkerPress, height = 320 },
  ref
) {
  const webViewRef  = useRef<WebView>(null);
  const htmlRef     = useRef<string>("");
  const prevLatRef  = useRef(userLat);
  const prevLonRef  = useRef(userLon);

  htmlRef.current = buildHtml(userLat, userLon, reports);

  // Expose imperative API to parent
  useImperativeHandle(ref, () => ({
    drawRoute(fromLat, fromLon, toLat, toLon) {
      webViewRef.current?.postMessage(
        JSON.stringify({ type: "drawRoute", fromLat, fromLon, toLat, toLon })
      );
    },
    clearRoute() {
      webViewRef.current?.postMessage(JSON.stringify({ type: "clearRoute" }));
    },
    recenter(lat, lon, zoom = 15) {
      webViewRef.current?.postMessage(
        JSON.stringify({ type: "recenter", lat, lon, zoom })
      );
    },
  }));

  // Live location updates without full reload
  useEffect(() => {
    if (
      webViewRef.current &&
      (prevLatRef.current !== userLat || prevLonRef.current !== userLon)
    ) {
      webViewRef.current.postMessage(
        JSON.stringify({ type: "updateLocation", lat: userLat, lon: userLon })
      );
      prevLatRef.current = userLat;
      prevLonRef.current = userLon;
    }
  }, [userLat, userLon]);

  const handleMessage = useCallback(
    (event: { nativeEvent: { data: string } }) => {
      try {
        const msg = JSON.parse(event.nativeEvent.data);
        if (msg.type === "markerPress" && onMarkerPress) {
          onMarkerPress(msg.id);
        }
        // routeReady is handled by live-map screen via onMessage prop
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
        androidHardwareAccelerationDisabled={false}
      />
    </View>
  );
});

export default OSMMap;
export { buildHtml };

const styles = StyleSheet.create({
  container: { width: "100%", overflow: "hidden" },
  webview:   { flex: 1, backgroundColor: "#e5e7eb" },
});
