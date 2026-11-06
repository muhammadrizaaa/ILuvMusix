package org.riza.iluvmusix.ui.screen.content.track

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.helper.rememberPaletteImageRequest
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
    val iTunesSearch by viewModel.iTunesSearchTrackResponse.collectAsState()
    val deezerSearch by viewModel.deezerSearchTrackResponse.collectAsState()

    val trackErrMsg by viewModel.trackDetailErrMsg.collectAsState()
    val iTunesSearchErrMsg by viewModel.iTunesSearchTrackErrMsg.collectAsState()
    val deezerSearchErrMsg by viewModel.deezerSearchTrackErrMsg.collectAsState()

    val trackStatus by viewModel.trackDetailStatus.collectAsState()
    val iTunesSearchStatus by viewModel.iTunesSearchTrackStatus.collectAsState()
    val deezerSearchStatus by viewModel.deezerSearchTrackStatus.collectAsState()

    val artistName = trackDetail?.artists?.joinToString(", ") {it.name}
    val fallbackColor = MaterialTheme.colorScheme.primary
    var glowColor by remember { mutableStateOf(fallbackColor) }
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_pulse")

// Pulses smoothly back and forth between 0.7f and 1.0f over 3 seconds
    val firstAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse // Plays forward, then backwards
        ),
        label = "firstAlpha"
    )

// Pulses between 0.3f and 0.6f over a slightly different duration (4 seconds)
// to create an organic, unsynchronized feel
    val secondAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondAlpha"
    )

    val isLightColor = glowColor.luminance() > 0.5f
    val view = LocalView.current

    // Dynamically flip the status bar icons to Dark/Light based on the album color
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Ensure the status bar background itself is totally transparent
                @Suppress("DEPRECATION")
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                // Flip icons to dark if the color is bright, and light if the color is dark
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightColor
            }
        }
    }

    // The massive block is now just one clean line!
    val cover = trackDetail?.album?.images?.firstOrNull()?.url
    val imageRequest = rememberPaletteImageRequest(
        imageUrl = cover,
        context = context,
        onColorExtracted = { customColor -> glowColor = customColor }
    )

    var showModalArtists by remember { mutableStateOf(false) }
    LaunchedEffect(id, token) {
        if(id != null && token != null){
            viewModel.getTrackById(
                id = id,
                token = "Bearer $token"
            )
        }
    }

    //LOGGER
    LaunchedEffect(iTunesSearch) {
        Log.d("ITUNES_RETURN", "data: $iTunesSearch \n\n errMsg: $iTunesSearchErrMsg \n\n status: $iTunesSearchStatus")
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize().background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = firstAlpha),
                            glowColor.copy(alpha = secondAlpha),
                            Color.Transparent
                        )
                    )
                )
        ) {
            when(trackStatus){
                ApiStatus.LOADING, ApiStatus.NONE -> {
                    LoadingBox(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                ApiStatus.SUCCESS -> {
                    LaunchedEffect(trackDetail) {
                        trackDetail?.let {
                            val artists = trackDetail?.artists?.firstOrNull()?.name
                            viewModel.getItunesSearch(it.name, artists?:"", it.album?.name?:"")
                        }
                    }
                    LaunchedEffect(iTunesSearch) {
                        if(iTunesSearch?.results?.firstOrNull()?.previewUrl == null){
                            trackDetail?.let {
                                val artists = trackDetail?.artists?.firstOrNull()?.name
                                viewModel.getDeezerSearch(it.name, artists?:"")
                            }
                        }
                    }
                    LaunchedEffect(iTunesSearchErrMsg, iTunesSearchStatus) {
                        iTunesSearchErrMsg?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                    LaunchedEffect(deezerSearchErrMsg, deezerSearchStatus) {
                        deezerSearchErrMsg?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
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
                        },
                        urlPreview = iTunesSearch?.results?.firstOrNull()?.previewUrl?: deezerSearch?.data?.firstOrNull()?.preview,
                        previewIsLoading = iTunesSearchStatus == ApiStatus.LOADING || deezerSearchStatus == ApiStatus.LOADING,
                        imageRequest = imageRequest,
                        bgColor = glowColor,
                        isLightColor = isLightColor
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
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