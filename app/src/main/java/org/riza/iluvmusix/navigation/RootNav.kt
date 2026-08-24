package org.riza.iluvmusix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.riza.iluvmusix.ui.screen.auth.LandingScreen
import org.riza.iluvmusix.ui.screen.content.album.ALBUM_ID_KEY
import org.riza.iluvmusix.ui.screen.content.album.AlbumDetailScreen
import org.riza.iluvmusix.ui.screen.content.track.TRACK_ID_KEY
import org.riza.iluvmusix.ui.screen.content.track.TrackDetailScreen
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

        composable(
            route = Screen.AlbumDetailScreen.route,
            arguments = listOf(
                navArgument(ALBUM_ID_KEY) { NavType.StringType }
            )
        ) {
            AlbumDetailScreen(
                navHostController = navController,
                albumId = it.arguments?.getString(ALBUM_ID_KEY)
            )
        }

        composable(
            route = Screen.TrackDetailScreen.route,
            arguments = listOf(
                navArgument(TRACK_ID_KEY) { NavType.StringType }
            )
        ) {
            TrackDetailScreen(
                navHostController = navController,
                id = it.arguments?.getString(TRACK_ID_KEY)
            )
        }
    }
}