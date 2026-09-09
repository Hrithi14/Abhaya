package com.pbrlm.abhaya.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pbrlm.abhaya.navigation.NavRoutes
import com.pbrlm.abhaya.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home",      Icons.Default.Home,    NavRoutes.EmergencyHome.route),
    BottomNavItem("My SOS",    Icons.Default.Warning, NavRoutes.MyRequests.route),
    BottomNavItem("Community", Icons.Default.Group,   NavRoutes.CommunityHelp.route),
    BottomNavItem("History",   Icons.Default.History, NavRoutes.History.route),
)

@Composable
fun AbhayaBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(NavRoutes.EmergencyHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = EmergencyRed,
                    selectedTextColor   = EmergencyRed,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor      = EmergencyRed.copy(alpha = 0.12f)
                )
            )
        }
    }
}
