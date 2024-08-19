package us.cosplayapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import us.cosplayapp.ui.screen.cons.ConScreen
import us.cosplayapp.ui.screen.cosplay.CosplayScreen
import us.cosplayapp.ui.screen.cosplayDetails.CosplayDetails
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
            CosplayScreen(
                onNavigateToDetailsScreen = { character ->
                navController.navigate("cosplayDetails/$character")
            },)
        }
        composable("cosplayDetails/{character}",
            arguments = listOf(
                navArgument("character") { type = NavType.StringType }
            )) {
            val character = it.arguments?.getString("character")
            if (character != null) {
                CosplayDetails(character)
            }
        }
    }
}