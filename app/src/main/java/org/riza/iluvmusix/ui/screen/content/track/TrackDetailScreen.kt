package org.riza.iluvmusix.ui.screen.content.track

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.navigation.Screen
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.ui.component.main.content.track.TrackDetailPart
import org.riza.iluvmusix.ui.component.main.content.util.BackIconButton
import org.riza.iluvmusix.ui.component.state.err.ErrBox
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox

const val TRACK_ID_KEY = "track_id_key"

@OptIn(ExperimentalMaterial3Api::class)
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

    var showModalArtists by remember { mutableStateOf(false) }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        album = trackDetail?.album,
                        title = trackDetail?.name,
                        isExplicit = trackDetail?.explicit,
                        durationMs = trackDetail?.durationMs,
                        artists = artistName,
                        onArtistClick = {
                            showModalArtists = true
                        },
                        onAlbumClick = { id, title ->
                            navHostController.navigate(Screen.AlbumDetailScreen.withId(id))
                        }
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
        if(showModalArtists){
            ModalBottomSheet(
                onDismissRequest = {
                    showModalArtists = false
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trackDetail?.artists?:emptyList()){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    onClick = {
                                        navHostController.navigate(
                                            Screen.ArtistDetailScreen.withId(
                                                it.id
                                            )
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.CenterStart
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier.size(48.dp).clip(CircleShape),
                                    model = it.images?.firstOrNull()?.url,
                                    contentDescription = null
                                )
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}