/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./app/**/*.{js,jsx,ts,tsx}", "./components/**/*.{js,jsx,ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        bg: "#0D1117",
        surface: "#161B22",
        surfaceVar: "#21262D",
        divider: "#30363D",
        textPrimary: "#E6EDF3",
        textSecondary: "#8B949E",
        emergency: "#D32F2F",
        emergencyDark: "#9A0007",
        critical: "#D32F2F",
        high: "#E65100",
        medium: "#F9A825",
        low: "#2E7D32",
        actionBlue: "#1565C0",
        actionGreen: "#2E7D32",
        gpsGreen: "#00E676",
        zoneRed: "#D32F2F",
        zoneYellow: "#F9A825",
        zoneGreen: "#2E7D32",
      },
    },
  },
  plugins: [],
};
