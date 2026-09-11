/**
 * Login screen
 * - Email + Password (Sign In / Register) via Firebase Auth
 * - Continue without Login (guest)
 * - User details stored in Firestore on first register
 */
import React, { useState } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  ActivityIndicator, StyleSheet, Alert,
  KeyboardAvoidingView, Platform, ScrollView,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  updateProfile,
} from "firebase/auth";
import { doc, setDoc, serverTimestamp } from "firebase/firestore";
import { auth, db } from "../src/services/firebase";
import { useAuth } from "../src/context/AuthContext";
import { C } from "../src/theme/colors";

export default function LoginScreen() {
  const router              = useRouter();
  const { setGuest, setUser } = useAuth();

  const [tab, setTab]           = useState<"signin" | "register">("signin");
  const [name, setName]         = useState("");
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading]   = useState(false);
  const [showPass, setShowPass] = useState(false);

  // ── Sign In ──────────────────────────────────────────────────────────────
  const handleSignIn = async () => {
    if (!email.trim() || !password.trim()) {
      Alert.alert("Missing fields", "Please enter your email and password.");
      return;
    }
    setLoading(true);
    try {
      const cred = await signInWithEmailAndPassword(auth, email.trim(), password);
      setUser(cred.user);
      router.replace("/");
    } catch (err: any) {
      const msg = friendlyError(err.code);
      Alert.alert("Sign in failed", msg);
    } finally {
      setLoading(false);
    }
  };

  // ── Register ─────────────────────────────────────────────────────────────
  const handleRegister = async () => {
    if (!name.trim() || !email.trim() || !password.trim()) {
      Alert.alert("Missing fields", "Please fill in all fields.");
      return;
    }
    if (password.length < 6) {
      Alert.alert("Weak password", "Password must be at least 6 characters.");
      return;
    }
    setLoading(true);
    try {
      const cred = await createUserWithEmailAndPassword(auth, email.trim(), password);

      // Save display name
      await updateProfile(cred.user, { displayName: name.trim() });

      // Store user details in Firestore
      await setDoc(doc(db, "users", cred.user.uid), {
        uid:       cred.user.uid,
        name:      name.trim(),
        email:     email.trim(),
        createdAt: serverTimestamp(),
        role:      "citizen",
      });

      setUser(cred.user);
      router.replace("/");
    } catch (err: any) {
      const msg = friendlyError(err.code);
      Alert.alert("Registration failed", msg);
    } finally {
      setLoading(false);
    }
  };

  // ── Guest ────────────────────────────────────────────────────────────────
  const handleGuest = () => {
    setGuest();
    router.replace("/");
  };

  return (
    <SafeAreaView style={s.screen}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
        <ScrollView
          contentContainerStyle={s.scroll}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          {/* Logo */}
          <View style={s.logoWrap}>
            <View style={s.logoCircle}>
              <Text style={s.logoEmoji}>🛡️</Text>
            </View>
            <Text style={s.appName}>ABHAYA</Text>
            <Text style={s.tagline}>Disaster Relief & Emergency Portal</Text>
            <Text style={s.region}>Mangaluru · Karnataka · PBRLM</Text>
          </View>

          {/* Tab switcher */}
          <View style={s.tabRow}>
            <TouchableOpacity
              style={[s.tabBtn, tab === "signin" && s.tabBtnActive]}
              onPress={() => setTab("signin")}
            >
              <Text style={[s.tabText, tab === "signin" && s.tabTextActive]}>
                Sign In
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[s.tabBtn, tab === "register" && s.tabBtnActive]}
              onPress={() => setTab("register")}
            >
              <Text style={[s.tabText, tab === "register" && s.tabTextActive]}>
                Register
              </Text>
            </TouchableOpacity>
          </View>

          {/* Form */}
          <View style={s.form}>

            {/* Name — only on register */}
            {tab === "register" && (
              <View style={s.inputWrap}>
                <Text style={s.inputLabel}>Full Name</Text>
                <TextInput
                  style={s.input}
                  placeholder="Enter your full name"
                  placeholderTextColor="#94A3B8"
                  value={name}
                  onChangeText={setName}
                  autoCapitalize="words"
                />
              </View>
            )}

            {/* Email */}
            <View style={s.inputWrap}>
              <Text style={s.inputLabel}>Email</Text>
              <TextInput
                style={s.input}
                placeholder="Enter your email"
                placeholderTextColor="#94A3B8"
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            {/* Password */}
            <View style={s.inputWrap}>
              <Text style={s.inputLabel}>Password</Text>
              <View style={s.passwordRow}>
                <TextInput
                  style={[s.input, { flex: 1, marginBottom: 0 }]}
                  placeholder={tab === "register" ? "Min 6 characters" : "Enter password"}
                  placeholderTextColor="#94A3B8"
                  value={password}
                  onChangeText={setPassword}
                  secureTextEntry={!showPass}
                  autoCapitalize="none"
                />
                <TouchableOpacity
                  onPress={() => setShowPass((v) => !v)}
                  style={s.eyeBtn}
                >
                  <Text style={s.eyeText}>{showPass ? "🙈" : "👁️"}</Text>
                </TouchableOpacity>
              </View>
            </View>

            {/* Primary action button */}
            <TouchableOpacity
              onPress={tab === "signin" ? handleSignIn : handleRegister}
              disabled={loading}
              style={[s.primaryBtn, loading && { opacity: 0.7 }]}
              activeOpacity={0.85}
            >
              {loading ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={s.primaryBtnText}>
                  {tab === "signin" ? "SIGN IN" : "CREATE ACCOUNT"}
                </Text>
              )}
            </TouchableOpacity>

            {/* Divider */}
            <View style={s.dividerRow}>
              <View style={s.dividerLine} />
              <Text style={s.dividerText}>OR</Text>
              <View style={s.dividerLine} />
            </View>

            {/* Guest button */}
            <TouchableOpacity
              onPress={handleGuest}
              style={s.guestBtn}
              activeOpacity={0.85}
            >
              <Text style={s.guestBtnText}>👤  Continue without Login</Text>
              <Text style={s.guestBtnSub}>Access all emergency features as guest</Text>
            </TouchableOpacity>
          </View>

          <Text style={s.note}>
            Your location is only used to send help to you.{"\n"}We never share your data.
          </Text>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

// ── Firebase error → human readable ──────────────────────────────────────
function friendlyError(code: string): string {
  switch (code) {
    case "auth/user-not-found":       return "No account found with this email.";
    case "auth/wrong-password":       return "Incorrect password. Please try again.";
    case "auth/invalid-email":        return "Please enter a valid email address.";
    case "auth/email-already-in-use": return "This email is already registered. Try signing in.";
    case "auth/weak-password":        return "Password must be at least 6 characters.";
    case "auth/too-many-requests":      return "Too many attempts. Please wait a moment.";
    case "auth/network-request-failed": return "No internet connection. Check your WiFi.";
    case "auth/invalid-credential":     return "Incorrect email or password.";
    case "auth/operation-not-allowed":  return "Email sign-in is not enabled yet.\n\nGo to Firebase Console → Authentication → Sign-in method → Email/Password → Enable it.";
    default:                            return "Something went wrong. Please try again.";
  }
}

const s = StyleSheet.create({
  screen:       { flex: 1, backgroundColor: "#0F172A" },
  scroll:       { flexGrow: 1, paddingHorizontal: 24, paddingTop: 40, paddingBottom: 32 },

  // Logo
  logoWrap:     { alignItems: "center", marginBottom: 32 },
  logoCircle:   { width: 88, height: 88, borderRadius: 44, backgroundColor: "#1E293B", alignItems: "center", justifyContent: "center", borderWidth: 2, borderColor: C.orange, marginBottom: 14 },
  logoEmoji:    { fontSize: 44 },
  appName:      { color: "#fff", fontSize: 32, fontWeight: "900", letterSpacing: 4 },
  tagline:      { color: "rgba(255,255,255,0.6)", fontSize: 13, marginTop: 5, textAlign: "center" },
  region:       { color: C.orange, fontSize: 11, fontWeight: "700", marginTop: 3, letterSpacing: 0.8 },

  // Tabs
  tabRow:       { flexDirection: "row", backgroundColor: "#1E293B", borderRadius: 10, padding: 4, marginBottom: 24 },
  tabBtn:       { flex: 1, paddingVertical: 10, borderRadius: 8, alignItems: "center" },
  tabBtnActive: { backgroundColor: C.orange },
  tabText:      { color: "rgba(255,255,255,0.5)", fontSize: 14, fontWeight: "700" },
  tabTextActive:{ color: "#fff" },

  // Form
  form:         { gap: 0 },
  inputWrap:    { marginBottom: 16 },
  inputLabel:   { color: "rgba(255,255,255,0.7)", fontSize: 12, fontWeight: "600", marginBottom: 6, letterSpacing: 0.4 },
  input:        { backgroundColor: "#1E293B", borderRadius: 10, borderWidth: 1.5, borderColor: "#334155", paddingHorizontal: 14, paddingVertical: 13, color: "#fff", fontSize: 14 },
  passwordRow:  { flexDirection: "row", alignItems: "center", gap: 8 },
  eyeBtn:       { backgroundColor: "#1E293B", borderRadius: 10, borderWidth: 1.5, borderColor: "#334155", padding: 12 },
  eyeText:      { fontSize: 16 },

  // Primary
  primaryBtn:   { backgroundColor: C.orange, borderRadius: 12, padding: 16, alignItems: "center", marginTop: 8, marginBottom: 16 },
  primaryBtnText:{ color: "#fff", fontSize: 15, fontWeight: "900", letterSpacing: 0.5 },

  // Divider
  dividerRow:   { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 16 },
  dividerLine:  { flex: 1, height: 1, backgroundColor: "#334155" },
  dividerText:  { color: "#64748B", fontSize: 12, fontWeight: "600" },

  // Guest
  guestBtn:     { backgroundColor: "#1E293B", borderRadius: 12, padding: 16, alignItems: "center", borderWidth: 1.5, borderColor: "#334155" },
  guestBtnText: { color: "#E2E8F0", fontSize: 15, fontWeight: "700" },
  guestBtnSub:  { color: "#64748B", fontSize: 11, marginTop: 3 },

  note:         { color: "rgba(255,255,255,0.28)", fontSize: 11, textAlign: "center", lineHeight: 17, marginTop: 24 },
});
