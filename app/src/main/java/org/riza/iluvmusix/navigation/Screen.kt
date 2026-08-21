package org.riza.iluvmusix.navigation

import org.riza.iluvmusix.ui.screen.content.album.ALBUM_ID_KEY

sealed class Screen(val route: String) {
    data object LoginScreen: Screen(route = "login_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object LandingScreen: Screen(route = "landing_screen")

    data object AlbumDetailScreen: Screen("album_detail_screen/{$ALBUM_ID_KEY}"){
        fun withId(type: String) = "album_detail_screen/$type"
    }
}