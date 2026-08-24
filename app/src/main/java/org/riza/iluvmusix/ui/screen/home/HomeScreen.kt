package org.riza.iluvmusix.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.riza.iluvmusix.R
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.navigation.Screen
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.ui.component.main.search.SearchResult
import org.riza.iluvmusix.ui.component.state.err.ErrBox
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox
import org.riza.iluvmusix.ui.component.textfield.SearchTextField
import org.riza.iluvmusix.ui.component.topbar.CustomTopBar

@Composable
fun HomeScreen(
    navHostController: NavHostController
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val auth = dataStore.userIdFlow.collectAsState(null)
    val token = auth.value?.accessToken

    val viewModel: HomeViewModel = viewModel()
    val searchResult by viewModel.searchResult.collectAsState()
    val searchResultErrMsg by viewModel.searchResultErrMsg.collectAsState()
    val searchResultStatus by viewModel.searchResultStatus.collectAsState()

    var q by remember { mutableStateOf("") }

    LaunchedEffect(q) {
        if(q.length > 2){
            delay(200)
            viewModel.getSearch(
                q = q,
                token = "Bearer $token"
            )
        }
    }
    Scaffold(
        topBar = {
            CustomTopBar(
                titleText = stringResource(R.string.app_name),
                titleIcon = painterResource(R.drawable.i_luv_musix_icon),
                navIcon = null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                value = q,
                onValChange = {
                    q = it
                }
            )
            when(searchResultStatus){
                ApiStatus.LOADING -> LoadingBox(
                    modifier = Modifier.fillMaxSize()
                )
                ApiStatus.SUCCESS, ApiStatus.NONE -> {
                    SearchResult(
                        modifier = Modifier,
                        data = searchResult,
                        onAlbumCLick = {
                            navHostController.navigate(Screen.AlbumDetailScreen.withId(it))
                        },
                        onTrackClick = {
                            navHostController.navigate(Screen.TrackDetailScreen.withId(it))
                        },
                        onArtistClick = {},
                    )
                }
                ApiStatus.ERR -> ErrBox(
                    modifier = Modifier.fillMaxSize(),
                    errMsg = searchResultErrMsg,
                    onRetry = {
                        viewModel.getSearch(
                            q = q,
                            token = "Bearer $token"
                        )
                    }
                )
            }
        }
    }
}