import type {
  EmergencyPriority,
  EmergencyStatus,
  MedicalEmergencyType,
  WaterDepth,
} from "../types/emergency";

export function calculateSosPriority(): EmergencyPriority {
  return "CRITICAL";
}

export function calculateWaterRescuePriority(params: {
  waterDepth: WaterDepth;
  waterRising?: boolean;
  peopleTrapped: boolean;
  injured: boolean;
  numberOfPeople: number;
  children: boolean;
  elderly: boolean;
  disabled: boolean;
}): EmergencyPriority {
  const { waterDepth, waterRising, peopleTrapped, injured, numberOfPeople, children, elderly, disabled } = params;

  if (peopleTrapped) return "CRITICAL";
  if (waterDepth === "ABOVE_CHEST") return "CRITICAL";
  if (waterRising && waterDepth === "CHEST") return "CRITICAL";
  if (waterRising && waterDepth === "WAIST" && (children || elderly || disabled)) return "CRITICAL";
  if (injured) return "CRITICAL";
  if (numberOfPeople >= 5 && waterDepth === "CHEST") return "CRITICAL";

  if (waterDepth === "CHEST") return "HIGH";
  if (waterRising) return "HIGH";
  if (numberOfPeople >= 3) return "HIGH";
  if (children || elderly || disabled) return "HIGH";
  if (waterDepth === "WAIST") return "HIGH";
  if (waterDepth === "KNEE" || waterDepth === "ANKLE") return "MEDIUM";

  return "HIGH";
}

export function calculateMedicalPriority(params: {
  medicalType: MedicalEmergencyType;
  personUnconscious: boolean;
  breathingProblem: boolean;
  severeBleeding: boolean;
  pregnancyRelated: boolean;
  injured: boolean;
  numberOfPeople: number;
  children: boolean;
  elderly: boolean;
  disabled: boolean;
}): EmergencyPriority {
  const { personUnconscious, breathingProblem, severeBleeding, medicalType, injured, numberOfPeople, children, elderly, disabled, pregnancyRelated } = params;

  if (personUnconscious) return "CRITICAL";
  if (breathingProblem) return "CRITICAL";
  if (severeBleeding) return "CRITICAL";
  if (medicalType === "UNCONSCIOUS" || medicalType === "BREATHING_PROBLEM" || medicalType === "SEVERE_BLEEDING") return "CRITICAL";
  if (numberOfPeople >= 3 && injured) return "CRITICAL";

  if (pregnancyRelated || medicalType === "PREGNANCY") return "HIGH";
  if (injured) return "HIGH";
  if (children || elderly || disabled) return "HIGH";
  if (numberOfPeople >= 3) return "HIGH";

  return "MEDIUM";
}

export function calculateBoatRescuePriority(params: {
  waterDepth: WaterDepth;
  waterRising?: boolean;
  peopleTrapped: boolean;
  medicalEmergency: boolean;
  numberOfPeople: number;
  children: boolean;
  elderly: boolean;
  disabled: boolean;
}): EmergencyPriority {
  const { peopleTrapped, medicalEmergency, waterDepth, waterRising, children, elderly, disabled } = params;

  if (peopleTrapped) return "CRITICAL";
  if (medicalEmergency) return "CRITICAL";
  if (waterDepth === "ABOVE_CHEST") return "CRITICAL";
  if (waterRising && (children || elderly || disabled)) return "CRITICAL";

  if (waterDepth === "CHEST") return "HIGH";
  if (waterRising) return "HIGH";
  if (children || elderly || disabled) return "HIGH";

  return "HIGH";
}

export function calculateShelterPriority(params: {
  numberOfPeople: number;
  children: boolean;
  elderly: boolean;
  disabled: boolean;
}): EmergencyPriority {
  const { children, elderly, disabled } = params;
  if (children || elderly || disabled) return "HIGH";
  return "MEDIUM";
}

export function isEscalationRequired(
  priority: EmergencyPriority,
  status: EmergencyStatus,
  createdAtMs: number,
  nowMs: number = Date.now()
): boolean {
  if (priority !== "CRITICAL") return false;
  if (status !== "ACTIVE" && status !== "ACKNOWLEDGED") return false;
  const elapsedMinutes = (nowMs - createdAtMs) / 60_000;
  return elapsedMinutes >= 30;
}
