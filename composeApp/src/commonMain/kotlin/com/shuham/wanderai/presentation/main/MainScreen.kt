package com.shuham.wanderai.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shuham.wanderai.navigation.Home
import com.shuham.wanderai.navigation.Itinerary
import com.shuham.wanderai.navigation.Profile
import com.shuham.wanderai.navigation.Trips
import com.shuham.wanderai.presentation.home.HomeRoute
import com.shuham.wanderai.presentation.loading.LoadingScreen
import com.shuham.wanderai.presentation.profile.ProfileScreen
import com.shuham.wanderai.presentation.trips.TripsScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val items = listOf(
        NavigationItem("Home", Home, Icons.Default.Home),
        NavigationItem("Trips", Trips, Icons.Default.DateRange),
        NavigationItem("Profile", Profile, Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Check if we are on a top-level tab
            val isTopLevel = items.any { item ->
                 currentDestination?.hierarchy?.any { 
                     it.route == item.route::class.qualifiedName 
                 } == true
            }

            if (isTopLevel) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                             it.route == item.route::class.qualifiedName 
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Home> {
                HomeRoute(
                    onNavigateToItinerary = { request ->
                        navController.navigate(Itinerary(request))
                    }
                )
            }
            
            composable<Trips> { TripsScreen() }
            
            composable<Profile> { ProfileScreen() }
            
            // --- Full Screen Flows ---

//            composable<Loading> { backStackEntry ->
//                val route = backStackEntry.toRoute<Loading>()
//                LoadingScreen(
//                    request = route.request,
//                    onTripGenerated = { response ->
//                        navController.navigate(Itinerary(response)) {
//                             // Remove Loading from backstack
//                             popUpTo(Home) { inclusive = false }
//                        }
//                    },
//                    onNavigateBack = { navController.popBackStack() }
//                )
//            }
            
            composable<Itinerary> { backStackEntry ->
                val route = backStackEntry.toRoute<Itinerary>()
                // Placeholder for Itinerary
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text("Itinerary for ${route.tripId} Generated!")
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val route: Any,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
