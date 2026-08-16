package org.riza.iluvmusix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.riza.iluvmusix.ui.screen.auth.LandingScreen
import org.riza.iluvmusix.ui.screen.home.HomeScreen

@Composable
fun RootNav(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.LandingScreen.route
    ){
        composable(route = Screen.LoginScreen.route){

        }
        composable(route = Screen.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route = Screen.LandingScreen.route){
            LandingScreen(navController)
        }
    }
}