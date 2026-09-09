const ORS_KEY = process.env.EXPO_PUBLIC_ORS_API_KEY;
const OSRM_URL = "https://router.project-osrm.org/route/v1/driving";
const NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

export const KNOWN_DESTINATIONS = [
  {
    id: "sh1",
    name: "High Ground Town Hall Shelter",
    address: "Town Hall Compound, Hampankatta",
    latitude: 12.8703,
    longitude: 74.8424,
  },
  {
    id: "sh2",
    name: "St. Aloysius Community High Shelter",
    address: "Light House Hill Rd, Mangaluru",
    latitude: 12.8732,
    longitude: 74.8456,
  },
  {
    id: "sh3",
    name: "Kadri Park High Ground",
    address: "Kadri Hills, Mangaluru",
    latitude: 12.8894,
    longitude: 74.8562,
  },
];

export async function searchDestinations(query) {
  const q = (query || "").trim().toLowerCase();
  if (q.length < 2) return [];

  const local = KNOWN_DESTINATIONS.filter(
    (d) =>
      d.name.toLowerCase().includes(q) ||
      d.address.toLowerCase().includes(q) ||
      q.includes("shelter")
  );

  try {
    const url = `${NOMINATIM_URL}?format=json&limit=5&addressdetails=0&q=${encodeURIComponent(
      `${query}, Mangaluru, Karnataka`
    )}`;
    const response = await fetch(url, {
      headers: { "User-Agent": "AbhayaDisasterApp/1.0 (abhaya@local)" },
    });
    const data = await response.json();
    const remote = (Array.isArray(data) ? data : []).map((item, index) => ({
      id: `nom_${item.place_id || index}`,
      name: item.display_name?.split(",")[0] || query,
      address: item.display_name,
      latitude: Number(item.lat),
      longitude: Number(item.lon),
    }));
    const merged = [...local];
    remote.forEach((place) => {
      if (!merged.some((m) => Math.abs(m.latitude - place.latitude) < 0.0004)) {
        merged.push(place);
      }
    });
    return merged.slice(0, 6);
  } catch (error) {
    console.warn("Destination search failed:", error?.message);
    return local;
  }
}

function toLngLat(point) {
  return `${point.longitude},${point.latitude}`;
}

function dist2(a, b) {
  const dLat = a.latitude - b.latitude;
  const dLng = a.longitude - b.longitude;
  return dLat * dLat + dLng * dLng;
}

function offsetAroundHazard(start, end, hazard) {
  const vx = end.longitude - start.longitude;
  const vy = end.latitude - start.latitude;
  const len = Math.sqrt(vx * vx + vy * vy) || 1;
  const px = -vy / len;
  const py = vx / len;
  const offset = 0.0022;
  return {
    latitude: hazard.latitude + py * offset,
    longitude: hazard.longitude + px * offset,
  };
}

async function fetchOsrmRoute(points) {
  const path = points.map(toLngLat).join(";");
  const url = `${OSRM_URL}/${path}?overview=full&geometries=geojson&alternatives=false`;
  const response = await fetch(url);
  const data = await response.json();
  if (data.code !== "Ok" || !data.routes?.[0]?.geometry?.coordinates) {
    return null;
  }
  return data.routes[0].geometry.coordinates.map(([lng, lat]) => ({
    latitude: lat,
    longitude: lng,
  }));
}

async function fetchOrsRoute(start, end, hazards) {
  if (!ORS_KEY) return null;
  const avoidPolygons = hazards.map((h) => {
    const delta = 0.0015;
    return [[
      [h.longitude - delta, h.latitude - delta],
      [h.longitude + delta, h.latitude - delta],
      [h.longitude + delta, h.latitude + delta],
      [h.longitude - delta, h.latitude + delta],
      [h.longitude - delta, h.latitude - delta],
    ]];
  });

  const response = await fetch(
    "https://api.openrouteservice.org/v2/directions/driving-car/geojson",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: ORS_KEY,
      },
      body: JSON.stringify({
        coordinates: [
          [start.longitude, start.latitude],
          [end.longitude, end.latitude],
        ],
        ...(avoidPolygons.length
          ? {
              options: {
                avoid_polygons: {
                  type: "MultiPolygon",
                  coordinates: avoidPolygons,
                },
              },
            }
          : {}),
      }),
    }
  );
  const data = await response.json();
  if (!data.features?.[0]?.geometry?.coordinates) return null;
  return data.features[0].geometry.coordinates.map(([lng, lat]) => ({
    latitude: lat,
    longitude: lng,
  }));
}

/**
 * Route from GPS source to a chosen destination, bending around nearby hazards.
 */
export const fetchSafeRoute = async (start, end, hazards = []) => {
  if (
    !start?.latitude ||
    !start?.longitude ||
    !end?.latitude ||
    !end?.longitude
  ) {
    return null;
  }

  try {
    const orsPath = await fetchOrsRoute(start, end, hazards);
    if (orsPath?.length) return orsPath;

    const nearbyHazards = (hazards || []).filter((h) => {
      if (typeof h.latitude !== "number" || typeof h.longitude !== "number") {
        return false;
      }
      const mid = {
        latitude: (start.latitude + end.latitude) / 2,
        longitude: (start.longitude + end.longitude) / 2,
      };
      return dist2(mid, h) < 0.00008 || dist2(start, h) < 0.00005 || dist2(end, h) < 0.00005;
    });

    const waypoints = [start];
    nearbyHazards.slice(0, 3).forEach((hazard) => {
      waypoints.push(offsetAroundHazard(start, end, hazard));
    });
    waypoints.push(end);

    return await fetchOsrmRoute(waypoints);
  } catch (error) {
    console.warn("Routing Error:", error?.message);
    return null;
  }
};
