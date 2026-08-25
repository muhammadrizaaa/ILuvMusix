package org.riza.iluvmusix.ui.screen.content.artist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.ErrResponse
import org.riza.iluvmusix.dataclass.response.main.ArtistDto
import org.riza.iluvmusix.dataclass.response.main.PageResponse
import org.riza.iluvmusix.dataclass.response.main.content.album.SimpleAlbumDto
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class ArtistDetailViewModel: ViewModel() {
    private val _artistDetailResponse: MutableStateFlow<ArtistDto?> = MutableStateFlow(null)
    val artistDetailResponse = _artistDetailResponse.asStateFlow()
    private val _albumListResponse: MutableStateFlow<PageResponse<SimpleAlbumDto>?> = MutableStateFlow(null)
    val albumListResponse = _albumListResponse.asStateFlow()

    private val _artistDetailErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val artistDetailErrMsg = _artistDetailErrMsg.asStateFlow()
    private val _albumListErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val albumListErrMsg = _albumListErrMsg.asStateFlow()

    val artistDetailStatus = MutableStateFlow(ApiStatus.NONE)
    val albumListStatus = MutableStateFlow(ApiStatus.NONE)

    fun getArtist(
        token: String,
        id: String
    ){
        viewModelScope.launch {
            artistDetailStatus.value = ApiStatus.LOADING
            try {
                val result = MainApi.mainServices.getArtistById(
                    id = id,
                    token = token
                )
                if(result.isSuccessful){
                    _artistDetailResponse.value = result.body()
                    artistDetailStatus.value = ApiStatus.SUCCESS
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        ErrResponse::class.java
                    )
                    _artistDetailErrMsg.value = error.error.message
                    artistDetailStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("ARTIST_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("ARTIST_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _artistDetailErrMsg.value = "conn_err"
                    else -> _artistDetailErrMsg.value = e.message ?: "Unknown error"
                }

                artistDetailStatus.value = ApiStatus.NONE
            }
        }
    }

    fun getAlbum(
        token: String,
        id: String
    ){
        viewModelScope.launch {
            albumListStatus.value = ApiStatus.LOADING
            try {
                val result = MainApi.mainServices.getAlbumByArtist(
                    id = id,
                    token = token
                )
                if(result.isSuccessful){
                    _albumListResponse.value = result.body()
                    albumListStatus.value = ApiStatus.SUCCESS
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        ErrResponse::class.java
                    )
                    _albumListErrMsg.value = error.error.message
                    albumListStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("ALBUM_LIST_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("ALBUM_LIST_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _albumListErrMsg.value = "conn_err"
                    else -> _albumListErrMsg.value = e.message ?: "Unknown error"
                }

                albumListStatus.value = ApiStatus.NONE
            }
        }
    }
}