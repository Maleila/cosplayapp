package us.cosplayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import us.cosplayapp.ui.screen.cons.ConScreen
import us.cosplayapp.ui.screen.cosplay.CosplayScreen
import us.cosplayapp.ui.screen.home.HomeScreen

@Composable
fun NavGraph (
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            HomeScreen(
                onNavigateToCosplayScreen = {
                    navController.navigate(Screen.Cosplay.route)
                },
                onNavigateToConScreen = {
                    navController.navigate(Screen.Cons.route)
                }
            )
        }
        composable(Screen.Cons.route) {
            ConScreen()
        }
        composable(Screen.Cosplay.route) {
            CosplayScreen()
        }
    }
}