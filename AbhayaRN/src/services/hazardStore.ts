/**
 * Shared in-memory store for hazard reports (ported from Kotlin HazardRepository).
 * Uses a simple event-emitter pattern so multiple screens can subscribe to updates.
 */

export type HazardCategory =
  | "FLOOD_WATER"
  | "WATERLOGGING"
  | "FALLEN_TREE"
  | "ROADBLOCK"
  | "POTHOLE"
  | "OPEN_WIRE"
  | "LANDSLIDE"
  | "FIRE"
  | "MEDICAL_EMERGENCY";

export const HAZARD_CATEGORY_LABELS: Record<HazardCategory, string> = {
  FLOOD_WATER:      "FLOOD WATER",
  WATERLOGGING:     "WATERLOGGING",
  FALLEN_TREE:      "FALLEN TREE",
  ROADBLOCK:        "ROADBLOCK",
  POTHOLE:          "POTHOLE",
  OPEN_WIRE:        "OPEN WIRE",
  LANDSLIDE:        "LANDSLIDE",
  FIRE:             "FIRE",
  MEDICAL_EMERGENCY:"MEDICAL EMERGENCY",
};

export const HAZARD_CATEGORY_EMOJI: Record<HazardCategory, string> = {
  FLOOD_WATER:      "🌊",
  WATERLOGGING:     "💧",
  FALLEN_TREE:      "🌳",
  ROADBLOCK:        "🚧",
  POTHOLE:          "⚠️",
  OPEN_WIRE:        "⚡",
  LANDSLIDE:        "🏔️",
  FIRE:             "🔥",
  MEDICAL_EMERGENCY:"🏥",
};

export interface HazardReport {
  id: string;
  category: HazardCategory;
  description: string;
  latitude: number;
  longitude: number;
  createdAt: number; // epoch ms
  verified: boolean;
  upvotes: number;
  waterDepth?: string;
  imageUrl?: string;
  radiusMeters: number;
}

export interface EmergencyContact {
  name: string;
  type: string;
  phone: string;
  address: string;
  distanceKm: number;
  note?: string;
}

export interface ShelterFacility {
  name: string;
  elevationMeters: number;
  capacity: number;
  currentOccupancy: number;
  address: string;
  distanceKm: number;
  supplies: string[];
}

// ── Default location: Mangaluru, Karnataka ─────────────────────────────────
export const DEFAULT_LATITUDE = 12.9141;
export const DEFAULT_LONGITUDE = 74.856;

// ── Initial seeded reports (real Mangaluru locations) ──────────────────────
const INITIAL_REPORTS: HazardReport[] = [
  {
    id: "rep_001",
    category: "FLOOD_WATER",
    description: "Knee-deep flood water near MG Road junction. Traffic diverted towards KS Rao Road.",
    latitude: 12.9148, longitude: 74.8552,
    createdAt: Date.now() - 2 * 60 * 1000,
    verified: true, upvotes: 14, waterDepth: "2.4 ft", radiusMeters: 220,
  },
  {
    id: "rep_002",
    category: "WATERLOGGING",
    description: "Heavy waterlogging under Pumpwell Flyover. Two wheelers stranded.",
    latitude: 12.8715, longitude: 74.8698,
    createdAt: Date.now() - 7 * 60 * 1000,
    verified: true, upvotes: 28, waterDepth: "1.8 ft", radiusMeters: 180,
  },
  {
    id: "rep_003",
    category: "FALLEN_TREE",
    description: "Large banyan tree branch blocking left lane entirely near Ladyhill Circle.",
    latitude: 12.8942, longitude: 74.8385,
    createdAt: Date.now() - 10 * 60 * 1000,
    verified: false, upvotes: 5, radiusMeters: 50,
  },
  {
    id: "rep_004",
    category: "OPEN_WIRE",
    description: "Snapping live electric wire submerged in puddle outside Kadri Park gate.",
    latitude: 12.883, longitude: 74.858,
    createdAt: Date.now() - 15 * 60 * 1000,
    verified: true, upvotes: 42, radiusMeters: 80,
  },
  {
    id: "rep_005",
    category: "LANDSLIDE",
    description: "Soil slipping on mud embankment along Gurupura stretch. Precaution advised.",
    latitude: 12.932, longitude: 74.91,
    createdAt: Date.now() - 35 * 60 * 1000,
    verified: false, upvotes: 9, radiusMeters: 120,
  },
  {
    id: "rep_006",
    category: "ROADBLOCK",
    description: "Police barricades installed on Kottara Chowki underpass due to flash surge.",
    latitude: 12.9064, longitude: 74.839,
    createdAt: Date.now() - 45 * 60 * 1000,
    verified: true, upvotes: 19, radiusMeters: 90,
  },
];

// ── Reactive store ─────────────────────────────────────────────────────────
type Listener = (reports: HazardReport[]) => void;

class HazardStore {
  private _reports: HazardReport[] = [...INITIAL_REPORTS];
  private _listeners: Set<Listener> = new Set();

  get reports(): HazardReport[] {
    return this._reports;
  }

  subscribe(listener: Listener): () => void {
    this._listeners.add(listener);
    listener(this._reports); // immediate
    return () => this._listeners.delete(listener);
  }

  private _notify() {
    this._listeners.forEach((l) => l([...this._reports]));
  }

  addReport(params: {
    category: HazardCategory;
    description: string;
    latitude: number;
    longitude: number;
    waterDepth?: string;
    imageUrl?: string;
  }): HazardReport {
    const newReport: HazardReport = {
      id: "rep_" + Math.random().toString(36).slice(2, 10),
      category: params.category,
      description: params.description,
      latitude: params.latitude,
      longitude: params.longitude,
      createdAt: Date.now(),
      verified: false,
      upvotes: 1,
      waterDepth: params.waterDepth || undefined,
      imageUrl: params.imageUrl || undefined,
      radiusMeters:
        params.category === "FLOOD_WATER" || params.category === "WATERLOGGING"
          ? 160
          : 60,
    };
    this._reports = [newReport, ...this._reports];
    this._notify();
    return newReport;
  }

  upvoteReport(id: string) {
    this._reports = this._reports.map((r) =>
      r.id === id ? { ...r, upvotes: r.upvotes + 1 } : r
    );
    this._notify();
  }

  toggleVerification(id: string) {
    this._reports = this._reports.map((r) =>
      r.id === id ? { ...r, verified: !r.verified } : r
    );
    this._notify();
  }

  deleteReport(id: string) {
    this._reports = this._reports.filter((r) => r.id !== id);
    this._notify();
  }

  getDynamicAISummary(): string {
    const floodCount = this._reports.filter(
      (r) => r.category === "FLOOD_WATER" || r.category === "WATERLOGGING"
    ).length;
    const top = this._reports[0];
    if (top) {
      return `Active flood alerts: ${floodCount} areas waterlogged. High risk near ${HAZARD_CATEGORY_LABELS[top.category]}. Safe evacuation path open towards High Ground Shelter.`;
    }
    return "Situation normal in Mangaluru perimeter. Safe path open towards High Ground Shelter.";
  }
}

export const hazardStore = new HazardStore();

// ── Static data ─────────────────────────────────────────────────────────────
export const EMERGENCY_CONTACTS: EmergencyContact[] = [
  {
    name: "National Disaster Helpline",
    type: "Emergency Response",
    phone: "112",
    address: "Central Control Room, 24/7 Operations",
    distanceKm: 0,
    note: "Immediate dispatch for police, fire & medical",
  },
  {
    name: "Wenlock District Hospital",
    type: "HOSPITAL",
    phone: "+91 824 242 4555",
    address: "Hampankatta, Mangaluru, Karnataka 575001",
    distanceKm: 1.2,
    note: "Trauma center & 24/7 ICU ambulance bay",
  },
  {
    name: "KMC Hospital Ambedkar Circle",
    type: "HOSPITAL",
    phone: "+91 824 244 4590",
    address: "Dr. B R Ambedkar Circle, Balmatta, Mangaluru",
    distanceKm: 1.8,
    note: "Emergency rescue & critical water-borne injury care",
  },
  {
    name: "Mangalore North Police (Bunder)",
    type: "POLICE",
    phone: "+91 824 222 0524",
    address: "Bunder, Mangaluru, Karnataka 575001",
    distanceKm: 1.5,
    note: "Coastal & flood relief coordination unit",
  },
  {
    name: "Kadri Police Station",
    type: "POLICE",
    phone: "+91 824 222 0528",
    address: "Kadri Hills, Mangaluru, Karnataka 575002",
    distanceKm: 2.4,
    note: "High ground patrol squad",
  },
];

export const SAFE_SHELTERS: ShelterFacility[] = [
  {
    name: "Town Hall Mangaluru (High Ground)",
    elevationMeters: 42,
    capacity: 850,
    currentOccupancy: 210,
    address: "Nehru Maidan Rd, Hampankatta, Mangaluru",
    distanceKm: 0.9,
    supplies: ["Clean Water", "Hot Food", "First Aid", "Backup Generators"],
  },
  {
    name: "St. Aloysius High-Ground Community Center",
    elevationMeters: 58,
    capacity: 1200,
    currentOccupancy: 440,
    address: "Light House Hill Rd, Mangaluru 575003",
    distanceKm: 1.4,
    supplies: ["Medical Bay", "Baby Formula", "Dry Blankets", "Satellite Comms"],
  },
  {
    name: "Kadri Hills Disaster Relief Pavilion",
    elevationMeters: 65,
    capacity: 600,
    currentOccupancy: 130,
    address: "Near Kadri Temple Grounds, Mangaluru",
    distanceKm: 2.7,
    supplies: ["Drinking Water", "Sanitary Kits", "Ambulance Standby"],
  },
];
