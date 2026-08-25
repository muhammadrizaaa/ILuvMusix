package org.riza.iluvmusix.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.riza.iluvmusix.R
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.navigation.Screen
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.ui.component.button.CustomTextButton

@Composable
fun LandingScreen(
    navHostController: NavHostController
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val viewModel: AuthViewModel = viewModel()
    val tokenStatus = viewModel.tokenStatus.collectAsState()
    val tokenErrMsg = viewModel.tokenErrMsg.collectAsState()



    Scaffold { innerPadding->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.i_luv_musix_icon),
                    tint = null,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                CustomTextButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    text = "Get Started",
                    isActive = tokenStatus.value != ApiStatus.LOADING,
                    isLoading = tokenStatus.value == ApiStatus.LOADING
                ) {
                    viewModel.getToken(dataStore)
                }
                LaunchedEffect(tokenStatus.value) {
                    if(tokenStatus.value == ApiStatus.SUCCESS){
                        navHostController.navigate(Screen.HomeScreen.route){
                            popUpTo(Screen.LandingScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                }
                if(tokenStatus.value == ApiStatus.ERR && tokenErrMsg.value != null){
                    Text(
                        text = tokenErrMsg.value!!,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}