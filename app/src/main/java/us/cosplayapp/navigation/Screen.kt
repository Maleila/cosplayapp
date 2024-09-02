package us.cosplayapp.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Cosplay : Screen("cosplay")
    object Cons : Screen("cons")
    object Misc : Screen("misc")
    object CosplayDetails: Screen("cosplayDetails")
    object ConDetails: Screen("conDetails")
}