import { clampToThreeLines, generateAISituationSummary } from "../utils/hazardSummary";

const GEMINI_API_KEY =
  process.env.EXPO_PUBLIC_GEMINI_API_KEY || process.env.GEMINI_API_KEY;
const GEMINI_MODEL = "gemini-3.6-flash";
const GEMINI_ENDPOINT = `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent`;

/**
 * Summarizes the live reports. Uses Gemini when available, otherwise
 * a data-only local summary. Never invents hazards that are not in the feed.
 */
export async function generateLiveSummaryWithAI(reports = []) {
  const dataSummary = generateAISituationSummary(reports);

  if (!reports || reports.length === 0) {
    return dataSummary;
  }

  if (!GEMINI_API_KEY) {
    return dataSummary;
  }

  const reportLogs = reports
    .map((r, index) => {
      const status = r.verified ? "VERIFIED" : "UNVERIFIED";
      const votes = r.upvotes || r.votes || 0;
      const note = r.description || "No description";
      return `${index + 1}. Category: ${r.category || "Hazard"}; Status: ${status}; Upvotes: ${votes}; Note: "${note}"`;
    })
    .join("\n");

  const prompt = `You are the Abhaya disaster-ops assistant.
Summarize ONLY these citizen reports. Do not invent roads, shelters, or hazards that are not written below.

Reports:
${reportLogs}

Rules:
- Maximum 3 short lines.
- Use the categories, notes, verified status, and upvote counts from the data.
- No markdown, no quotes, no headings.`;

  try {
    const response = await fetch(`${GEMINI_ENDPOINT}?key=${GEMINI_API_KEY}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          maxOutputTokens: 120,
          temperature: 0.3,
        },
      }),
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data?.error?.message || `Gemini HTTP ${response.status}`);
    }

    const text = data?.candidates?.[0]?.content?.parts
      ?.map((part) => part.text)
      .filter(Boolean)
      .join("\n")
      .trim();

    return clampToThreeLines(text) || dataSummary;
  } catch (error) {
    console.warn("Gemini API unavailable, using live report summary:", error?.message);
    return dataSummary;
  }
}
