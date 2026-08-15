package org.riza.iluvmusix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RootNav(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ){
        composable(route = Screen.LoginScreen.route){

        }
        composable(route = Screen.HomeScreen.route){

        }
    }
}