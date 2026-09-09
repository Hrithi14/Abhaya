import { useState, useEffect } from "react";
import { hazardStore, HazardReport } from "../services/hazardStore";

/**
 * Hook to subscribe to hazard reports from the shared store.
 * Any screen using this hook automatically re-renders when reports change.
 */
export function useHazardReports(): HazardReport[] {
  const [reports, setReports] = useState<HazardReport[]>(hazardStore.reports);

  useEffect(() => {
    const unsubscribe = hazardStore.subscribe(setReports);
    return unsubscribe;
  }, []);

  return reports;
}
