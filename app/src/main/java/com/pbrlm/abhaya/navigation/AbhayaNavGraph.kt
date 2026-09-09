package com.pbrlm.abhaya.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pbrlm.abhaya.ui.screens.boatrescue.BoatRescueScreen
import com.pbrlm.abhaya.ui.screens.communityhelp.CommunityHelpScreen
import com.pbrlm.abhaya.ui.screens.communityhelp.OfferHelpScreen
import com.pbrlm.abhaya.ui.screens.detail.EmergencyDetailScreen
import com.pbrlm.abhaya.ui.screens.evacuate.EvacuateSafetyScreen
import com.pbrlm.abhaya.ui.screens.history.EmergencyHistoryScreen
import com.pbrlm.abhaya.ui.screens.home.EmergencyHomeScreen
import com.pbrlm.abhaya.ui.screens.medical.MedicalEmergencyScreen
import com.pbrlm.abhaya.ui.screens.myrequests.MyRequestsScreen
import com.pbrlm.abhaya.ui.screens.shelter.ShelterRequestScreen
import com.pbrlm.abhaya.ui.screens.waterrescue.WaterRescueScreen

@Composable
fun AbhayaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.EmergencyHome.route
    ) {
        composable(NavRoutes.EmergencyHome.route) {
            EmergencyHomeScreen(navController = navController)
        }
        composable(NavRoutes.MyRequests.route) {
            MyRequestsScreen(navController = navController)
        }
        composable(NavRoutes.CommunityHelp.route) {
            CommunityHelpScreen(navController = navController)
        }
        composable(NavRoutes.History.route) {
            EmergencyHistoryScreen(navController = navController)
        }
        composable(NavRoutes.WaterRescue.route) {
            WaterRescueScreen(navController = navController)
        }
        composable(NavRoutes.MedicalEmergency.route) {
            MedicalEmergencyScreen(navController = navController)
        }
        composable(NavRoutes.BoatRescue.route) {
            BoatRescueScreen(navController = navController)
        }
        composable(NavRoutes.ShelterRequest.route) {
            ShelterRequestScreen(navController = navController)
        }
        composable(
            route = NavRoutes.EmergencyDetail.route,
            arguments = listOf(navArgument("emergencyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("emergencyId") ?: ""
            EmergencyDetailScreen(emergencyId = id, navController = navController)
        }
        composable(
            route = NavRoutes.OfferHelp.route,
            arguments = listOf(navArgument("emergencyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("emergencyId") ?: ""
            OfferHelpScreen(emergencyId = id, navController = navController)
        }
        composable(NavRoutes.EvacuateSafety.route) {
            EvacuateSafetyScreen(navController = navController)
        }
    }
}
