package org.riza.iluvmusix.ui.screen.content.artist

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.riza.iluvmusix.R
import org.riza.iluvmusix.datastore.AuthDataStore
import org.riza.iluvmusix.navigation.Screen
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.ui.component.main.content.artist.ArtistProfilePart
import org.riza.iluvmusix.ui.component.main.content.artist.DiscographyItem
import org.riza.iluvmusix.ui.component.main.content.util.BackIconButton
import org.riza.iluvmusix.ui.component.state.err.ErrBox
import org.riza.iluvmusix.ui.component.state.loading.LoadingBox

const val ARTIST_ID_KEY = "artist_id_key"

@Composable
fun ArtistDetailScreen(
    id: String?,
    navHostController: NavHostController
){
    val context = LocalContext.current
    val dataStore = AuthDataStore(context)
    val auth = dataStore.userIdFlow.collectAsState(null)
    val token = auth.value?.accessToken

    val viewmodel: ArtistDetailViewModel = viewModel()

    val artistDetailData by viewmodel.artistDetailResponse.collectAsState()
    val albumListData by viewmodel.albumListResponse.collectAsState()
    val artistDetailErrMsg by viewmodel.artistDetailErrMsg.collectAsState()
    val albumListErrMsg by viewmodel.albumListErrMsg.collectAsState()
    val artistDetailStatus by viewmodel.artistDetailStatus.collectAsState()
    val albumListStatus by viewmodel.albumListStatus.collectAsState()
    val isAlbumLoadingMore by viewmodel.isPagingLoading


    LaunchedEffect(token, id) {
        if(id != null && token != null){
            viewmodel.getArtist(
                token = "Bearer $token",
                id = id
            )
        }
    }

    LaunchedEffect(artistDetailStatus) {
        if(artistDetailStatus == ApiStatus.SUCCESS){
            id?.let {
                viewmodel.getAlbum(
                    token = "Bearer $token",
                    id = it
                )
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold{ innerPadding->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    BackIconButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 16.dp, start = 16.dp),
                        onCLick = { navHostController.navigateUp() }
                    )
                }
                item {
                    when(artistDetailStatus) {
                        ApiStatus.LOADING, ApiStatus.NONE -> {
                            LoadingBox(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                            )
                        }
                        ApiStatus.SUCCESS -> {
                            ArtistProfilePart(
                                name = artistDetailData?.name,
                                image = artistDetailData?.images?.firstOrNull()?.url
                            )
                        }
                        ApiStatus.ERR -> {
                            ErrBox(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                errMsg = artistDetailErrMsg
                            ) {
                                viewmodel.getArtist(
                                    token = "Bearer $token",
                                    id = id?:""
                                )
                            }
                        }
                    }
                }
                when(albumListStatus) {
                    ApiStatus.LOADING, ApiStatus.NONE -> {
                        item {
                            LoadingBox(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                            )
                        }
                    }
                    ApiStatus.SUCCESS -> {
                        item{
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                text = stringResource(R.string.discography),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        itemsIndexed(albumListData?.items?:emptyList()){ index, data->
                            DiscographyItem(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                data = data,
                                onClick = {
                                    navHostController.navigate(Screen.AlbumDetailScreen.withId(it))
                                }
                            )
                            if (index == (albumListData?.items?.size ?: 0) - 3) {
                                LaunchedEffect(key1 = index) {
                                    viewmodel.loadNextPage(token = "Bearer $token", artistId = id ?: "")
                                }
                            }
                        }
                        if(isAlbumLoadingMore){
                            item {
                                LoadingBox(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    ApiStatus.ERR -> {
                        item{
                            ErrBox(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                errMsg = albumListErrMsg
                            ) {
                                viewmodel.getAlbum(
                                    token = "Bearer $token",
                                    id = id?:""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}