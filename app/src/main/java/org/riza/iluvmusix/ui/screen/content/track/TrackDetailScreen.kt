package org.riza.iluvmusix.ui.screen.content.track

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.ui.component.main.content.track.TrackDetailPart
import org.riza.iluvmusix.ui.component.main.content.util.BackIconButton
import org.riza.iluvmusix.ui.component.state.err.ErrBox
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox

const val TRACK_ID_KEY = "track_id_key"

@Composable
fun TrackDetailScreen(
    id: String?,
    navHostController: NavHostController
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val auth = dataStore.userIdFlow.collectAsState(null)
    val token = auth.value?.accessToken

    val viewModel: TrackDetailViewModel = viewModel()

    val trackDetail by viewModel.trackDetailResponse.collectAsState()
    val trackErrMsg by viewModel.trackDetailErrMsg.collectAsState()
    val trackStatus by viewModel.trackDetailStatus.collectAsState()

    val artistName = trackDetail?.artists?.joinToString(",") {it.name}

    LaunchedEffect(id, token) {
        if(id != null && token != null){
            viewModel.getTrackById(
                id = id,
                token = "Bearer $token"
            )
        }
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when(trackStatus){
                ApiStatus.LOADING, ApiStatus.NONE -> {
                    LoadingBox(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                ApiStatus.SUCCESS -> {
                    TrackDetailPart(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(vertical = 32.dp, horizontal = 16.dp),
                        album = trackDetail?.album,
                        title = trackDetail?.name,
                        isExplicit = trackDetail?.explicit,
                        durationMs = trackDetail?.durationMs,
                        artists = artistName
                    )
                }
                ApiStatus.ERR -> {
                    ErrBox(
                        modifier = Modifier.fillMaxSize(),
                        errMsg = trackErrMsg,
                        onRetry = {
                            if(id != null && token != null){
                                viewModel.getTrackById(
                                    id = id,
                                    token = "Bearer $token"
                                )
                            }
                        }
                    )
                }
            }
            BackIconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 16.dp),
                onCLick = { navHostController.navigateUp() }
            )
        }
    }
}