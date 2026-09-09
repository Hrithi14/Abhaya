function clampToThreeLines(text) {
  if (!text) return "";
  return String(text)
    .split(/\r?\n/)
    .map((line) => line.replace(/\s+/g, " ").trim())
    .filter(Boolean)
    .slice(0, 3)
    .join("\n");
}

function shortNote(report) {
  const raw = (report.description || report.category || "Hazard").trim();
  if (raw.length <= 72) return raw;
  return `${raw.slice(0, 69).trim()}...`;
}

/**
 * Builds a live situation summary only from the reports currently on the map.
 * Never invents places or shelters that are not in the data. Max 3 lines.
 */
export function generateAISituationSummary(reports = []) {
  const active = Array.isArray(reports) ? reports.filter(Boolean) : [];

  if (active.length === 0) {
    return clampToThreeLines(
      "No active hazard reports in the live feed.\nCurrent data shows a clear area.\nNew citizen reports will appear here automatically."
    );
  }

  const categories = [...new Set(active.map((r) => r.category || "Hazard"))];
  const verified = active.filter((r) => r.verified);
  const ranked = [...active].sort((a, b) => {
    const risk = (r) => {
      const cat = (r.category || "").toUpperCase();
      if (cat.includes("WIRE") || cat.includes("FIRE") || cat.includes("FLOOD")) return 3;
      if (cat.includes("WATER") || cat.includes("LANDSLIDE") || cat.includes("ROAD")) return 2;
      return 1;
    };
    return risk(b) - risk(a) || (b.upvotes || 0) - (a.upvotes || 0);
  });

  const line1 = `${active.length} live report${active.length === 1 ? "" : "s"}: ${categories.join(", ")}.`;

  const primary = ranked[0];
  const verifiedTag = primary.verified ? "Verified" : "Unverified";
  const votes = primary.upvotes ? ` · ${primary.upvotes} upvotes` : "";
  const line2 = `${verifiedTag}${votes}: ${shortNote(primary)}`;

  let line3;
  if (ranked[1]) {
    const second = ranked[1];
    line3 = `Also: ${shortNote(second)}${verified.length ? ` · ${verified.length} verified` : ""}.`;
  } else if (verified.length) {
    line3 = `${verified.length} of ${active.length} report${active.length === 1 ? "" : "s"} verified by community.`;
  } else {
    line3 = "All current reports are still unverified — proceed with caution.";
  }

  return clampToThreeLines([line1, line2, line3].join("\n"));
}

export { clampToThreeLines };
