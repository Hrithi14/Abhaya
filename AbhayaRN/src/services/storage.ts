import AsyncStorage from "@react-native-async-storage/async-storage";
import type { EmergencyRequest, HelpOffer } from "../types/emergency";

const KEYS = {
  EMERGENCIES: "abhaya_emergencies",
  HELP_OFFERS: "abhaya_help_offers",
};

// ── Emergency Requests ─────────────────────────────────────────────────────

export async function saveEmergency(request: EmergencyRequest): Promise<void> {
  const all = await getAllEmergencies();
  const updated = [request, ...all.filter((r) => r.emergencyRequestId !== request.emergencyRequestId)];
  await AsyncStorage.setItem(KEYS.EMERGENCIES, JSON.stringify(updated));
}

export async function getAllEmergencies(): Promise<EmergencyRequest[]> {
  const raw = await AsyncStorage.getItem(KEYS.EMERGENCIES);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as EmergencyRequest[];
  } catch {
    return [];
  }
}

export async function getEmergencyById(id: string): Promise<EmergencyRequest | null> {
  const all = await getAllEmergencies();
  return all.find((r) => r.emergencyRequestId === id) ?? null;
}

export async function updateEmergencyStatus(
  id: string,
  status: EmergencyRequest["status"]
): Promise<void> {
  const all = await getAllEmergencies();
  const updated = all.map((r) =>
    r.emergencyRequestId === id ? { ...r, status } : r
  );
  await AsyncStorage.setItem(KEYS.EMERGENCIES, JSON.stringify(updated));
}

export async function getActiveEmergencies(userId: string): Promise<EmergencyRequest[]> {
  const all = await getAllEmergencies();
  const terminalStatuses = ["RESOLVED", "CANCELLED", "FALSE_REPORT"];
  return all.filter(
    (r) => r.userId === userId && !terminalStatuses.includes(r.status)
  );
}

export async function getUserEmergencies(userId: string): Promise<EmergencyRequest[]> {
  const all = await getAllEmergencies();
  return all.filter((r) => r.userId === userId);
}

export async function getCommunityEmergencies(): Promise<EmergencyRequest[]> {
  const all = await getAllEmergencies();
  const terminalStatuses = ["RESOLVED", "CANCELLED", "FALSE_REPORT"];
  return all.filter((r) => !r.isDemoData && !terminalStatuses.includes(r.status));
}

// ── Help Offers ────────────────────────────────────────────────────────────

export async function saveHelpOffer(offer: HelpOffer): Promise<void> {
  const all = await getAllHelpOffers();
  const updated = [offer, ...all];
  await AsyncStorage.setItem(KEYS.HELP_OFFERS, JSON.stringify(updated));
}

export async function getAllHelpOffers(): Promise<HelpOffer[]> {
  const raw = await AsyncStorage.getItem(KEYS.HELP_OFFERS);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as HelpOffer[];
  } catch {
    return [];
  }
}

// ── Demo Data ──────────────────────────────────────────────────────────────

export async function seedDemoData(userId: string): Promise<void> {
  const existing = await getAllEmergencies();
  if (existing.some((r) => r.isDemoData)) return; // already seeded

  const demoData: EmergencyRequest[] = [
    {
      emergencyRequestId: "ER-DEMO-001",
      userId,
      emergencyType: "WATER_RESCUE",
      latitude: 12.9123,
      longitude: 74.8512,
      gpsAccuracy: 5,
      timestamp: Date.now() - 45 * 60 * 1000,
      numberOfPeople: 4,
      children: true,
      elderly: false,
      disabled: false,
      peopleTrapped: true,
      injured: false,
      medicalRequired: false,
      waterDepth: "CHEST",
      waterRising: true,
      personUnconscious: false,
      breathingProblem: false,
      severeBleeding: false,
      pregnancyRelated: false,
      description: "Family trapped on 1st floor. Water rising fast.",
      contactNumber: "9876543210",
      priority: "CRITICAL",
      status: "ACTIVE",
      escalationRequired: true,
      isDemoData: true,
    },
    {
      emergencyRequestId: "ER-DEMO-002",
      userId,
      emergencyType: "MEDICAL",
      latitude: 12.9135,
      longitude: 74.8498,
      gpsAccuracy: 8,
      timestamp: Date.now() - 20 * 60 * 1000,
      numberOfPeople: 1,
      children: false,
      elderly: true,
      disabled: false,
      peopleTrapped: false,
      injured: true,
      medicalRequired: true,
      medicalEmergencyType: "INJURY",
      personUnconscious: false,
      breathingProblem: false,
      severeBleeding: false,
      pregnancyRelated: false,
      description: "Elderly person injured during evacuation.",
      contactNumber: "9876500001",
      priority: "HIGH",
      status: "ACKNOWLEDGED",
      escalationRequired: false,
      isDemoData: true,
    },
    {
      emergencyRequestId: "ER-DEMO-003",
      userId,
      emergencyType: "SHELTER",
      latitude: 12.91,
      longitude: 74.855,
      gpsAccuracy: 10,
      timestamp: Date.now() - 3 * 60 * 60 * 1000,
      numberOfPeople: 6,
      children: true,
      elderly: false,
      disabled: false,
      peopleTrapped: false,
      injured: false,
      medicalRequired: false,
      personUnconscious: false,
      breathingProblem: false,
      severeBleeding: false,
      pregnancyRelated: false,
      description: "Family of 6 displaced, need temporary shelter.",
      contactNumber: "9800001234",
      priority: "MEDIUM",
      status: "RESOLVED",
      escalationRequired: false,
      isDemoData: true,
    },
  ];

  const updated = [...demoData, ...existing];
  await AsyncStorage.setItem(KEYS.EMERGENCIES, JSON.stringify(updated));
}
