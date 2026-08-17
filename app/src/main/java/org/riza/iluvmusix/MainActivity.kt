package org.riza.iluvmusix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.riza.iluvmusix.navigation.RootNav
import org.riza.iluvmusix.ui.theme.ILuvMusixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ILuvMusixTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                RootNav()
            }
        }
    }
}