let counter = 0;

export function generateEmergencyId(isDemo = false): string {
  counter += 1;
  const seq = String(counter).padStart(6, "0");
  if (isDemo) return `ER-DEMO-${String(counter).padStart(3, "0")}`;
  const year = new Date().getFullYear();
  return `ER-${year}-${seq}`;
}

export function generateHelpOfferId(): string {
  counter += 1;
  return `HO-${String(counter).padStart(4, "0")}`;
}

export function generateVolunteerId(): string {
  counter += 1;
  return `VOL-${String(counter).padStart(4, "0")}`;
}
