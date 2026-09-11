/**
 * AuthContext — simple, no-hang, Expo Go compatible.
 * "ready"  = not logged in (show login screen)
 * "guest"  = guest mode
 * User     = Firebase signed-in user
 */
import React, { createContext, useContext, useEffect, useState } from "react";
import { User } from "firebase/auth";

type AuthState = "ready" | "guest" | User;

interface AuthContextType {
  user:    AuthState;
  isGuest: boolean;
  setGuest: () => void;
  setUser:  (u: User) => void;
  logout:   () => void;
}

const AuthContext = createContext<AuthContextType>({
  user:     "ready",
  isGuest:  false,
  setGuest: () => {},
  setUser:  () => {},
  logout:   () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<AuthState>("ready");

  useEffect(() => {
    // Check silently if user is already signed in
    let active = true;
    (async () => {
      try {
        const { onAuthStateChanged } = await import("firebase/auth");
        const { auth } = await import("../services/firebase");
        onAuthStateChanged(auth, (firebaseUser) => {
          if (!active) return;
          if (firebaseUser) setUserState(firebaseUser);
          // no user = stay "ready", login screen handles it
        });
      } catch {
        // Firebase not available — stay on login
      }
    })();
    return () => { active = false; };
  }, []);

  const setGuest = ()        => setUserState("guest");
  const setUser  = (u: User) => setUserState(u);
  const logout   = async ()  => {
    try {
      const { signOut } = await import("firebase/auth");
      const { auth }    = await import("../services/firebase");
      await signOut(auth);
    } catch {}
    setUserState("ready");
  };

  return (
    <AuthContext.Provider value={{
      user,
      isGuest:  user === "guest",
      setGuest,
      setUser,
      logout,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
