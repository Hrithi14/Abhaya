/**
 * Gemini Vision — photo validation + AI situation summary.
 */

const GEMINI_API_KEY = "Gemini Api key";
const ENDPOINT = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`;

// ── Types ─────────────────────────────────────────────────────────────────
export interface ReportForSummary {
  category: string;
  description: string;
  verified: boolean;
  upvotes: number;
  latitude: number;
  longitude: number;
  waterDepth?: string;
}

// ── Photo validation ───────────────────────────────────────────────────────
export async function validateDisasterPhoto(
  localUri: string
): Promise<{ valid: boolean; reason?: string }> {
  try {
    const response = await fetch(localUri);
    const blob     = await response.blob();
    const base64   = await blobToBase64(blob);

    const body = {
      contents: [
        {
          parts: [
            { inlineData: { mimeType: "image/jpeg", data: base64 } },
            {
              text: `You are a disaster photo validator for a flood emergency app.
Look at this image and answer ONLY with a JSON object in this exact format:
{"valid": true/false, "reason": "short reason"}

Mark valid=true ONLY if the image clearly shows:
- Flood water, waterlogging, submerged roads or buildings
- Fallen trees, landslides, road blockages
- Fire or smoke from a disaster
- Damaged infrastructure (broken bridges, collapsed walls)
- Any other natural disaster or road hazard scene

Mark valid=false if the image shows:
- A person or group of people (face/body visible)
- Electronic devices (phone, laptop, TV)
- Indoor scenes unrelated to disaster
- Animals, food, or unrelated objects
- Blurry, black, or blank images

Respond ONLY with the JSON. No other text.`,
            },
          ],
        },
      ],
    };

    const res  = await fetch(ENDPOINT, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    const json = await res.json();
    const text = json?.candidates?.[0]?.content?.parts?.[0]?.text ?? "";
    const match = text.match(/\{[\s\S]*\}/);
    if (!match) return { valid: true };
    const result = JSON.parse(match[0]);
    return { valid: !!result.valid, reason: result.reason ?? "" };
  } catch {
    return { valid: true };
  }
}

// ── AI Situation Summary — uses actual report data ─────────────────────────
export async function getGeminiSituationSummary(
  floodCount: number,
  totalReports: number,
  topCategory: string,
  reports?: ReportForSummary[]
): Promise<string> {
  try {
    let reportContext = "";

    if (reports && reports.length > 0) {
      // Build a structured list of actual reports for Gemini to analyse
      const top = reports.slice(0, 8); // cap at 8 to keep prompt short
      reportContext = top.map((r, i) => {
        const verified = r.verified ? "✓ verified" : "unverified";
        const depth    = r.waterDepth ? `, depth: ${r.waterDepth}` : "";
        return `${i + 1}. [${r.category.replace(/_/g, " ")}] ${r.description}${depth} — ${r.upvotes} upvotes, ${verified}`;
      }).join("\n");
    }

    const prompt = reports && reports.length > 0
      ? `You are a disaster response AI for Mangaluru, Karnataka, India.
Below are ${totalReports} live hazard reports from the field. Write a SHORT 2-sentence situation summary for citizens and first responders.
Be specific — mention the most critical hazard type, affected areas if known, and what action citizens should take.

Live reports:
${reportContext}

Stats: ${floodCount} flood/waterlogging reports out of ${totalReports} total.

Write ONLY the 2-sentence summary. No headers, no bullets, no extra text.`

      : `You are a disaster situation summariser for Mangaluru, India.
Stats: ${totalReports} hazard reports active, ${floodCount} are flood/waterlogging. Most reported: ${topCategory}.
Write a 2-sentence factual summary for citizens. Max 40 words. No headers.`;

    const body = {
      contents: [{ parts: [{ text: prompt }] }],
    };

    const res  = await fetch(ENDPOINT, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    const json    = await res.json();
    const summary = json?.candidates?.[0]?.content?.parts?.[0]?.text?.trim();
    if (summary) return summary;
    throw new Error("empty response");

  } catch {
    // Fallback — rule-based summary when Gemini is unavailable
    if (reports && reports.length > 0) {
      const floods  = reports.filter(r => r.category === "FLOOD_WATER" || r.category === "WATERLOGGING");
      const fires   = reports.filter(r => r.category === "FIRE");
      const wires   = reports.filter(r => r.category === "OPEN_WIRE");
      const parts: string[] = [];
      if (floods.length)  parts.push(`${floods.length} flood/waterlogging zone${floods.length > 1 ? "s" : ""}`);
      if (fires.length)   parts.push(`${fires.length} fire alert${fires.length > 1 ? "s" : ""}`);
      if (wires.length)   parts.push(`${wires.length} open wire hazard${wires.length > 1 ? "s" : ""}`);
      const rest = totalReports - floods.length - fires.length - wires.length;
      if (rest > 0) parts.push(`${rest} other hazard${rest > 1 ? "s" : ""}`);
      return `${totalReports} active hazard reports: ${parts.join(", ")}. Stay alert, avoid flooded roads, and move to high ground if water is rising.`;
    }
    return `Active flood alerts: ${floodCount} areas waterlogged out of ${totalReports} total reports. High risk near ${topCategory}. Stay on high ground.`;
  }
}

// ── Helper ─────────────────────────────────────────────────────────────────
function blobToBase64(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const result = reader.result as string;
      resolve(result.split(",")[1]);
    };
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}
