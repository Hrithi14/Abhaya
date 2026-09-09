/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          orange: "#F97316",
          darkNavy: "#0F172A",
          darkSlate: "#1E293B",
          amber: "#F59E0B",
          green: "#16A34A",
          emergencyRed: "#DC2626",
          bgLight: "#F8FAFC",
        },
      },
    },
  },
  plugins: [],
};
