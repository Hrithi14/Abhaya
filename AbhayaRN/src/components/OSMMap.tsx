/**
 * OSMMap — OpenStreetMap via Leaflet.js in a WebView
 *
 * Features:
 *  - Live GPS pulse beacon for user location
 *  - Hazard markers with category icons + flood zone circles
 *  - OSRM routing: draws safe route from user → destination
 *  - Hazard-aware route: warns if hazards lie on the route
 *  - Alternate route suggested if primary route is blocked
 *  - Zoom controls, drag-to-pan, recenter button
 *  - OSM attribution (required)
 */
import React, { useRef, useEffect, useCallback } from "react";
import { StyleSheet, View } from "react-native";
import { WebView } from "react-native-webview";
import type { HazardReport } from "../services/hazardStore";
import { HAZARD_CATEGORY_EMOJI } from "../services/hazardStore";

export interface RouteInfo {
  distanceKm: number;
  durationMin: number;
  hasHazards: boolean;
  hazardCount: number;
  hazardNames: string[];
}

interface Props {
  userLat: number;
  userLon: number;
  reports: HazardReport[];
  onMarkerPress?: (reportId: string) => void;
  onRouteCalculated?: (info: RouteInfo) => void;
  destLat?: number;
  destLon?: number;
  destName?: string;
  showRoute?: boolean;
  height?: number;
}

function buildHtml(
  userLat: number,
  userLon: number,
  reports: HazardReport[]
): string {
  const reportsJson = JSON.stringify(
    reports.map((r) => ({
      id: r.id,
      lat: r.latitude,
      lon: r.longitude,
      category: r.category,
      emoji: HAZARD_CATEGORY_EMOJI[r.category] ?? "⚠️",
      description: r.description.replace(/['"]/g, " "),
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
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
  * { margin:0; padding:0; box-sizing:border-box; }
  html,body,#map { width:100%; height:100%; background:#e5e7eb; }
  .hazard-marker {
    display:flex; align-items:center; justify-content:center;
    width:36px; height:36px; border-radius:50%;
    font-size:18px; border:2px solid #fff;
    box-shadow:0 2px 6px rgba(0,0,0,0.4);
  }
  .hazard-flood    { background:#F97316; }
  .hazard-water    { background:#0284C7; }
  .hazard-tree     { background:#16A34A; }
  .hazard-road     { background:#D97706; }
  .hazard-fire     { background:#DC2626; }
  .hazard-electric { background:#7C3AED; }
  .hazard-medical  { background:#DC2626; }
  .hazard-default  { background:#D97706; }
  .hazard-verified { border-color:#16A34A !important; border-width:3px !important; }
  .user-dot {
    width:18px; height:18px; border-radius:50%;
    background:#0284C7; border:3px solid #fff;
    animation:pulse 2s ease-out infinite;
  }
  .dest-marker {
    width:32px; height:32px; border-radius:50%;
    background:#16A34A; border:3px solid #fff;
    display:flex; align-items:center; justify-content:center;
    font-size:16px; box-shadow:0 2px 8px rgba(0,0,0,0.4);
  }
  @keyframes pulse {
    0%   { box-shadow:0 0 0 0 rgba(2,132,199,0.5); }
    70%  { box-shadow:0 0 0 14px rgba(2,132,199,0); }
    100% { box-shadow:0 0 0 0 rgba(2,132,199,0); }
  }
</style>
</head>
<body>
<div id="map"></div>
<script>
var map = L.map('map',{zoomControl:false,attributionControl:true})
  .setView([${userLat},${userLon}],14);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
  attribution:'&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
  maxZoom:19
}).addTo(map);

L.control.zoom({position:'bottomright'}).addTo(map);

// ── User location marker ─────────────────────────────────────────────────
var userIcon = L.divIcon({
  className:'',
  html:'<div class="user-dot"></div>',
  iconSize:[18,18], iconAnchor:[9,9]
});
var userMarker = L.marker([${userLat},${userLon}],{icon:userIcon,zIndexOffset:9999})
  .addTo(map)
  .bindPopup('<b>📍 Your Location</b>');

// ── Hazard reports ───────────────────────────────────────────────────────
var reports = ${reportsJson};

function categoryClass(cat){
  if(cat==='FLOOD_WATER'||cat==='WATERLOGGING') return 'hazard-flood';
  if(cat==='FALLEN_TREE') return 'hazard-tree';
  if(cat==='OPEN_WIRE')   return 'hazard-electric';
  if(cat==='FIRE'||cat==='MEDICAL_EMERGENCY') return 'hazard-fire';
  if(cat==='ROADBLOCK'||cat==='POTHOLE') return 'hazard-road';
  return 'hazard-default';
}

reports.forEach(function(r){
  if(r.category==='FLOOD_WATER'||r.category==='WATERLOGGING'){
    L.circle([r.lat,r.lon],{
      radius:r.radiusMeters,
      color:'#F97316',fillColor:'#F97316',
      fillOpacity:0.18,weight:2,opacity:0.7
    }).addTo(map);
  }
  var cls = categoryClass(r.category)+(r.verified?' hazard-verified':'');
  var icon = L.divIcon({
    className:'',
    html:'<div class="hazard-marker '+cls+'">'+r.emoji+'</div>',
    iconSize:[36,36],iconAnchor:[18,36],popupAnchor:[0,-36]
  });
  var depthInfo = r.waterDepth ? '<br>💧 Depth: '+r.waterDepth : '';
  var verBadge = r.verified
    ? '<span style="color:#16A34A;font-weight:700"> ✓ VERIFIED</span>'
    : '<span style="color:#D97706;font-weight:600"> ⚠ NOT VERIFIED</span>';
  L.marker([r.lat,r.lon],{icon:icon}).addTo(map)
    .bindPopup('<b>'+r.emoji+' '+r.category.replace(/_/g,' ')+'</b>'+verBadge+
      '<br><small>'+r.description+'</small>'+depthInfo+
      '<br><small>👍 '+r.upvotes+' upvotes</small>'+
      '<br><small>📍 '+r.lat.toFixed(4)+', '+r.lon.toFixed(4)+'</small>')
    .on('click',function(){
      window.ReactNativeWebView && window.ReactNativeWebView.postMessage(
        JSON.stringify({type:'markerPress',id:r.id})
      );
    });
});

// ── Routing state ────────────────────────────────────────────────────────
var routeLayer      = null;
var altRouteLayer   = null;
var destMarker      = null;
var hazardWarnings  = [];

// Haversine distance in km
function haversineKm(lat1,lon1,lat2,lon2){
  var R=6371,dLat=(lat2-lat1)*Math.PI/180,dLon=(lon2-lon1)*Math.PI/180;
  var a=Math.sin(dLat/2)*Math.sin(dLat/2)+
        Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*
        Math.sin(dLon/2)*Math.sin(dLon/2);
  return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
}

// Check if a point is within distKm of a line segment
function pointNearSegment(pLat,pLon,aLat,aLon,bLat,bLon,threshKm){
  var dx=bLon-aLon, dy=bLat-aLat, lenSq=dx*dx+dy*dy;
  if(lenSq===0) return haversineKm(pLat,pLon,aLat,aLon)<threshKm;
  var t=Math.max(0,Math.min(1,((pLon-aLon)*dx+(pLat-aLat)*dy)/lenSq));
  return haversineKm(pLat,pLon,aLat+t*dy,aLon+t*dx)<threshKm;
}

// Check hazards along a decoded polyline coords array [[lat,lon],...]
function checkHazardsOnRoute(coords){
  var found=[];
  reports.forEach(function(r){
    for(var i=0;i<coords.length-1;i++){
      if(pointNearSegment(r.lat,r.lon,
          coords[i][0],coords[i][1],
          coords[i+1][0],coords[i+1][1],0.3)){
        found.push(r);
        break;
      }
    }
  });
  return found;
}

// Draw a route polyline
function drawRoute(coords,color,weight,opacity,dashed){
  var latlngs=coords.map(function(c){return[c[0],c[1]];});
  var opts={color:color,weight:weight,opacity:opacity};
  if(dashed) opts.dashArray='10,8';
  return L.polyline(latlngs,opts).addTo(map);
}

// Decode OSRM geometry (encoded polyline precision 5)
function decodePolyline(encoded){
  var coords=[],index=0,len=encoded.length,lat=0,lng=0;
  while(index<len){
    var b,shift=0,result=0;
    do{ b=encoded.charCodeAt(index++)-63; result|=(b&0x1f)<<shift; shift+=5; }
    while(b>=0x20);
    var dlat=(result&1)?(~result>>1):(result>>1); lat+=dlat;
    shift=0; result=0;
    do{ b=encoded.charCodeAt(index++)-63; result|=(b&0x1f)<<shift; shift+=5; }
    while(b>=0x20);
    var dlng=(result&1)?(~result>>1):(result>>1); lng+=dlng;
    coords.push([lat/1e5,lng/1e5]);
  }
  return coords;
}

// Clear existing route layers
function clearRoutes(){
  if(routeLayer){ map.removeLayer(routeLayer); routeLayer=null; }
  if(altRouteLayer){ map.removeLayer(altRouteLayer); altRouteLayer=null; }
  if(destMarker){ map.removeLayer(destMarker); destMarker=null; }
}

// Fetch route from OSRM public API
function fetchRoute(fromLat,fromLon,toLat,toLon,callback,alternatives){
  var alts = alternatives ? '&alternatives=true' : '';
  var url='https://router.project-osrm.org/route/v1/driving/'+
    fromLon+','+fromLat+';'+toLon+','+toLat+
    '?overview=full&geometries=polyline'+alts;
  fetch(url)
    .then(function(r){return r.json();})
    .then(function(data){callback(null,data);})
    .catch(function(e){callback(e,null);});
}

// Main route drawing function
function calculateAndDrawRoute(toLat,toLon,toName){
  clearRoutes();

  // Place destination marker
  var destIcon=L.divIcon({
    className:'',
    html:'<div class="dest-marker">🏁</div>',
    iconSize:[32,32],iconAnchor:[16,32]
  });
  destMarker=L.marker([toLat,toLon],{icon:destIcon,zIndexOffset:8000})
    .addTo(map)
    .bindPopup('<b>🏁 Destination</b><br>'+toName)
    .openPopup();

  var fromLat=parseFloat('${userLat}');
  var fromLon=parseFloat('${userLon}');

  fetchRoute(fromLat,fromLon,toLat,toLon,function(err,data){
    if(err||!data||data.code!=='Ok'){
      window.ReactNativeWebView && window.ReactNativeWebView.postMessage(
        JSON.stringify({type:'routeError',message:'Could not calculate route. Check internet connection.'})
      );
      return;
    }

    var route=data.routes[0];
    var coords=decodePolyline(route.geometry);
    var distKm=(route.distance/1000).toFixed(1);
    var durMin=Math.round(route.duration/60);

    // Check hazards on primary route
    var hazardsOnRoute=checkHazardsOnRoute(coords);
    var hasHazards=hazardsOnRoute.length>0;

    // Draw primary route
    var routeColor=hasHazards?'#DC2626':'#16A34A';
    routeLayer=drawRoute(coords,routeColor,5,0.85,false);

    // Fit map to route
    map.fitBounds(routeLayer.getBounds(),{padding:[40,40]});

    // Add route info popup at midpoint
    var mid=coords[Math.floor(coords.length/2)];
    var popupContent='<b>'+(hasHazards?'⚠️ HAZARDOUS ROUTE':'✅ SAFE ROUTE')+'</b>'+
      '<br>📏 '+distKm+' km  ⏱ '+durMin+' min'+
      (hasHazards?'<br><span style="color:#DC2626">'+hazardsOnRoute.length+' hazard(s) on route</span>':'');
    L.popup({closeButton:true,autoClose:false})
      .setLatLng(mid)
      .setContent(popupContent)
      .openOn(map);

    // If hazards found, also fetch alternate route
    if(hasHazards && data.routes.length<2){
      fetchRoute(fromLat,fromLon,toLat,toLon,function(err2,data2){
        if(!err2&&data2&&data2.routes&&data2.routes.length>1){
          var altRoute=data2.routes[1];
          var altCoords=decodePolyline(altRoute.geometry);
          var altHazards=checkHazardsOnRoute(altCoords);
          if(altHazards.length<hazardsOnRoute.length){
            altRouteLayer=drawRoute(altCoords,'#0284C7',4,0.65,true);
            L.popup({closeButton:true})
              .setLatLng(altCoords[Math.floor(altCoords.length/2)])
              .setContent('<b>🔵 ALTERNATE (fewer hazards)</b><br>'+
                (altRoute.distance/1000).toFixed(1)+' km · '+
                Math.round(altRoute.duration/60)+' min')
              .openOn(map);
          }
        }
      },true);
    }

    // Notify React Native
    window.ReactNativeWebView && window.ReactNativeWebView.postMessage(JSON.stringify({
      type:'routeCalculated',
      distanceKm:parseFloat(distKm),
      durationMin:durMin,
      hasHazards:hasHazards,
      hazardCount:hazardsOnRoute.length,
      hazardNames:hazardsOnRoute.map(function(h){return h.category.replace(/_/g,' ');})
    }));
  },false);
}

// ── Message handler from React Native ────────────────────────────────────
function handleRNMessage(e){
  try{
    var msg=JSON.parse(e.data);
    if(msg.type==='updateLocation'){
      userMarker.setLatLng([msg.lat,msg.lon]);
    }
    if(msg.type==='recenter'){
      map.setView([msg.lat,msg.lon],msg.zoom||15,{animate:true});
      userMarker.setLatLng([msg.lat,msg.lon]);
    }
    if(msg.type==='drawRoute'){
      calculateAndDrawRoute(msg.destLat,msg.destLon,msg.destName||'Destination');
    }
    if(msg.type==='clearRoute'){
      clearRoutes();
    }
  }catch(err){}
}
document.addEventListener('message',handleRNMessage);
window.addEventListener('message',handleRNMessage);
</script>
</body>
</html>`;
}

export default function OSMMap({
  userLat, userLon, reports,
  onMarkerPress, onRouteCalculated,
  destLat, destLon, destName,
  showRoute, height = 320,
}: Props) {
  const webViewRef  = useRef<WebView>(null);
  const htmlRef     = useRef(buildHtml(userLat, userLon, reports));
  const prevLatRef  = useRef(userLat);
  const prevLonRef  = useRef(userLon);
  const prevKeyRef  = useRef(`${userLat},${userLon},${reports.length}`);

  // Send live location updates without full reload
  useEffect(() => {
    if (!webViewRef.current) return;
    if (prevLatRef.current !== userLat || prevLonRef.current !== userLon) {
      webViewRef.current.postMessage(
        JSON.stringify({ type: "updateLocation", lat: userLat, lon: userLon })
      );
      prevLatRef.current = userLat;
      prevLonRef.current = userLon;
    }
  }, [userLat, userLon]);

  // Draw or clear route when showRoute / destination changes
  useEffect(() => {
    if (!webViewRef.current) return;
    if (showRoute && destLat !== undefined && destLon !== undefined) {
      webViewRef.current.postMessage(
        JSON.stringify({
          type: "drawRoute",
          destLat, destLon,
          destName: destName ?? "Destination",
        })
      );
    } else if (!showRoute) {
      webViewRef.current.postMessage(JSON.stringify({ type: "clearRoute" }));
    }
  }, [showRoute, destLat, destLon, destName]);

  const handleMessage = useCallback(
    (event: { nativeEvent: { data: string } }) => {
      try {
        const msg = JSON.parse(event.nativeEvent.data);
        if (msg.type === "markerPress" && onMarkerPress) {
          onMarkerPress(msg.id);
        }
        if (msg.type === "routeCalculated" && onRouteCalculated) {
          onRouteCalculated({
            distanceKm:  msg.distanceKm,
            durationMin: msg.durationMin,
            hasHazards:  msg.hasHazards,
            hazardCount: msg.hazardCount,
            hazardNames: msg.hazardNames ?? [],
          });
        }
      } catch {}
    },
    [onMarkerPress, onRouteCalculated]
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
        allowsInlineMediaPlayback
        androidHardwareAccelerationDisabled={false}
      />
    </View>
  );
}

export { buildHtml };

const styles = StyleSheet.create({
  container: { width: "100%", overflow: "hidden" },
  webview:   { flex: 1, backgroundColor: "#e5e7eb" },
});
