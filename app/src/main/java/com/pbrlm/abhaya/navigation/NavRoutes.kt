package com.pbrlm.abhaya.navigation

/**
 * All navigation destinations in the Emergency Portal.
 * Structured so teammates can add LiveMap, ReportHazard, Admin routes
 * without touching emergency-specific routes.
 */
sealed class NavRoutes(val route: String) {

    // ── Bottom Nav ──
    object EmergencyHome   : NavRoutes("emergency_home")
    object MyRequests      : NavRoutes("my_requests")
    object CommunityHelp   : NavRoutes("community_help")
    object History         : NavRoutes("history")

    // ── Emergency forms ──
    object SosConfirm      : NavRoutes("sos_confirm")
    object WaterRescue     : NavRoutes("water_rescue")
    object MedicalEmergency: NavRoutes("medical_emergency")
    object BoatRescue      : NavRoutes("boat_rescue")
    object ShelterRequest  : NavRoutes("shelter_request")

    // ── Detail screens ──
    object EmergencyDetail : NavRoutes("emergency_detail/{emergencyId}") {
        fun createRoute(id: String) = "emergency_detail/$id"
    }
    object OfferHelp       : NavRoutes("offer_help/{emergencyId}") {
        fun createRoute(id: String) = "offer_help/$id"
    }

    // ── Integration screens ──
    object EvacuateSafety  : NavRoutes("evacuate_safety")

    // ── Placeholder routes for teammate modules (not built here) ──
    // LiveMap, ReportHazard, AdminPanel — teammates add these
}
