package org.riza.iluvmusix.navigation

sealed class Screen(val route: String) {
    data object LoginScreen: Screen(route = "login_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object LandingScreen: Screen(route = "landing_screen")
}