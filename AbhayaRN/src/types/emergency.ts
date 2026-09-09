export type EmergencyType =
  | "SOS"
  | "WATER_RESCUE"
  | "MEDICAL"
  | "BOAT_RESCUE"
  | "SHELTER"
  | "OTHER";

export type EmergencyStatus =
  | "ACTIVE"
  | "ACKNOWLEDGED"
  | "RESCUE_IN_PROGRESS"
  | "RESOLVED"
  | "CANCELLED"
  | "FALSE_REPORT";

export type EmergencyPriority = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";

export type WaterDepth =
  | "ANKLE"
  | "KNEE"
  | "WAIST"
  | "CHEST"
  | "ABOVE_CHEST"
  | "UNKNOWN";

export type MedicalEmergencyType =
  | "INJURY"
  | "UNCONSCIOUS"
  | "BREATHING_PROBLEM"
  | "SEVERE_BLEEDING"
  | "PREGNANCY"
  | "OTHER";

export type HelpType =
  | "BOAT"
  | "OFF_ROAD_VEHICLE"
  | "FOOD"
  | "DRINKING_WATER"
  | "SHELTER"
  | "ROPE"
  | "FIRST_AID"
  | "TRANSPORT"
  | "OTHER";

export type HelpOfferStatus =
  | "OFFERED"
  | "ACCEPTED"
  | "IN_PROGRESS"
  | "COMPLETED"
  | "CANCELLED";

export interface EmergencyRequest {
  emergencyRequestId: string;
  userId: string;
  hazardId?: string;
  clusterId?: string;
  zoneId?: string;
  emergencyType: EmergencyType;
  latitude: number;
  longitude: number;
  gpsAccuracy: number;
  timestamp: number; // epoch ms
  numberOfPeople: number;
  children: boolean;
  elderly: boolean;
  disabled: boolean;
  peopleTrapped: boolean;
  injured: boolean;
  medicalRequired: boolean;
  waterDepth?: WaterDepth;
  waterLevel?: string;
  waterRising?: boolean;
  medicalEmergencyType?: MedicalEmergencyType;
  personUnconscious: boolean;
  breathingProblem: boolean;
  severeBleeding: boolean;
  pregnancyRelated: boolean;
  description: string;
  buildingFloor?: string;
  contactNumber: string;
  photoUri?: string;
  priority: EmergencyPriority;
  status: EmergencyStatus;
  assignedResponderId?: string;
  responderContact?: string;
  responseTime?: number;
  resolutionTime?: number;
  responderNotes?: string;
  escalationRequired: boolean;
  isDemoData: boolean;
}

export interface HelpOffer {
  helpOfferId: string;
  emergencyRequestId: string;
  userId: string;
  helpType: HelpType;
  message: string;
  timestamp: number;
  status: HelpOfferStatus;
}

export interface Volunteer {
  volunteerId: string;
  userId: string;
  name: string;
  contactNumber: string;
  latitude: number;
  longitude: number;
  safeZoneStatus: boolean;
  availableToHelp: boolean;
  helpType: HelpType;
  capacity: number;
  notes: string;
}

// Display helpers
export const EMERGENCY_TYPE_LABELS: Record<EmergencyType, string> = {
  SOS: "SOS",
  WATER_RESCUE: "Trapped by Water",
  MEDICAL: "Medical Emergency",
  BOAT_RESCUE: "Boat Rescue",
  SHELTER: "Shelter Request",
  OTHER: "Other",
};

export const EMERGENCY_TYPE_EMOJI: Record<EmergencyType, string> = {
  SOS: "🚨",
  WATER_RESCUE: "🌊",
  MEDICAL: "🏥",
  BOAT_RESCUE: "🚤",
  SHELTER: "🏠",
  OTHER: "⚠️",
};

export const STATUS_LABELS: Record<EmergencyStatus, string> = {
  ACTIVE: "Active",
  ACKNOWLEDGED: "Acknowledged",
  RESCUE_IN_PROGRESS: "Rescue in Progress",
  RESOLVED: "Resolved",
  CANCELLED: "Cancelled",
  FALSE_REPORT: "False Report",
};

export const PRIORITY_COLORS: Record<EmergencyPriority, string> = {
  CRITICAL: "#D32F2F",
  HIGH: "#E65100",
  MEDIUM: "#F9A825",
  LOW: "#2E7D32",
};

export const WATER_DEPTH_LABELS: Record<WaterDepth, string> = {
  ANKLE: "Ankle",
  KNEE: "Knee",
  WAIST: "Waist",
  CHEST: "Chest",
  ABOVE_CHEST: "Above Chest",
  UNKNOWN: "Unknown",
};

export const MEDICAL_TYPE_LABELS: Record<MedicalEmergencyType, string> = {
  INJURY: "Injury",
  UNCONSCIOUS: "Unconscious",
  BREATHING_PROBLEM: "Breathing Problem",
  SEVERE_BLEEDING: "Severe Bleeding",
  PREGNANCY: "Pregnancy Related",
  OTHER: "Other",
};

export const HELP_TYPE_LABELS: Record<HelpType, string> = {
  BOAT: "🚤 Boat",
  OFF_ROAD_VEHICLE: "🚙 Off-Road Vehicle",
  FOOD: "🍲 Food",
  DRINKING_WATER: "💧 Drinking Water",
  SHELTER: "🏠 Shelter",
  ROPE: "🪢 Rope",
  FIRST_AID: "🩺 First Aid",
  TRANSPORT: "🚗 Transport",
  OTHER: "🤝 Other",
};
