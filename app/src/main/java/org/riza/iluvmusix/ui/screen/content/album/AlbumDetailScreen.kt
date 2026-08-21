package org.riza.iluvmusix.ui.screen.content.album

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import org.riza.iluvmusix.ui.component.main.content.album.AlbumListTrackPart
import org.riza.iluvmusix.ui.component.main.content.album.AlbumTitlePart
import org.riza.iluvmusix.ui.component.main.content.util.BackIconButton
import org.riza.iluvmusix.ui.component.state.err.ErrBox
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox

const val ALBUM_ID_KEY = "album_id_key"

@Composable
fun AlbumDetailScreen(
    navHostController: NavHostController,
    albumId: String?
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val auth = dataStore.userIdFlow.collectAsState(null)
    val token = auth.value?.accessToken

    val viewModel: AlbumDetailViewModel = viewModel()

    val albumDto by viewModel.albumDetailDto.collectAsState()
    val albumErrMsg by viewModel.albumDetailErrMsg.collectAsState()
    val albumStatus by viewModel.albumDetailStatus.collectAsState()

    LaunchedEffect(albumId, token) {
        if(albumId != null && token != null){
            viewModel.getAlbumById(
                id = albumId,
                token = "Bearer $token"
            )
        }
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold{  innerPadding->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when(albumStatus){
                    ApiStatus.LOADING, ApiStatus.NONE -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ){
                                BackIconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(top = 16.dp, start = 16.dp),
                                    onCLick = { navHostController.navigateUp() }
                                )
                                LoadingBox(modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .padding(top = 64.dp)
                                )
                            }
                        }
                    }
                    ApiStatus.SUCCESS -> {
                        item {
                            albumDto?.let {
                                val artist = it.artists?.joinToString(",") {artist-> artist.name}
                                AlbumTitlePart(
                                    title = it.name,
                                    imageUrl = it.images?.firstOrNull()?.url,
                                    artistsName = artist ?: "",
                                    isSaved = false,
                                    onSave = {},
                                    onBack = {navHostController.navigateUp()}
                                )
                            }
                        }
                        item {
                            albumDto?.tracks?.items?.let {
                                AlbumListTrackPart(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    data = it
                                )
                            }
                        }
                    }
                    ApiStatus.ERR -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ){
                                BackIconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(top = 16.dp, start = 16.dp),
                                    onCLick = { navHostController.navigateUp() }
                                )
                                ErrBox(
                                    modifier = Modifier.fillMaxWidth().padding(64.dp),
                                    errMsg = albumErrMsg
                                )
                            }
                        }
                    }
                }
                item {

                }
            }
        }
    }
}