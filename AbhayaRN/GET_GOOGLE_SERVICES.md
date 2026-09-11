# How to get google-services.json

1. Go to https://console.firebase.google.com
2. Open your project "abhaya-b4364"
3. Click the gear icon ⚙️ → Project settings
4. Under "Your apps" → click the Android icon (🤖) to add an Android app
5. Android package name: com.pbrlm.abhaya
6. Click "Register app"
7. Click "Download google-services.json"
8. Place that file here: AbhayaRN/google-services.json  (same folder as this file)

# How to get your Gemini API key

1. Go to https://aistudio.google.com/app/apikey
2. Click "Create API key"
3. Copy the key
4. Open: src/services/geminiValidate.ts
5. Replace PASTE_YOUR_GEMINI_KEY_HERE with your actual key
