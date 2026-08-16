package org.riza.iluvmusix.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.riza.iluvmusix.datastore.AuthDataStore

@Composable
fun HomeScreen(
    navHostController: NavHostController
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val token = dataStore.userIdFlow.collectAsState(null)
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            if(token.value != null){
                Text(
                    text = "Token: ${token.value?.accessToken}",
                    fontSize = 12.sp
                )
                Text(
                    text = "Type: ${token.value?.tokenType}",
                    fontSize = 12.sp
                )
            }
        }
    }
}