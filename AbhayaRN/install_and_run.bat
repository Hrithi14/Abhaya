@echo off
cd /d C:\Users\KAVANA\Desktop\Abhaya\AbhayaRN
echo ============================================
echo  ABHAYA React Native - Installing packages
echo ============================================
echo.

npm install --legacy-peer-deps

if %ERRORLEVEL% neq 0 (
    echo.
    echo Trying with --force...
    npm install --force
)

if %ERRORLEVEL% neq 0 (
    echo Install failed. Check internet connection.
    pause
    exit /b 1
)

echo.
echo ============================================
echo  Done! Starting Expo...
echo ============================================
echo.
echo 1. Install "Expo Go" from Play Store on your phone
echo 2. Connect phone and PC to the SAME WiFi
echo 3. Scan the QR code that appears below
echo.

npx expo start --clear
pause
