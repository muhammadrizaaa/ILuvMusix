package org.riza.iluvmusix.ui.screen.content.artist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.delay
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

    // In your ViewModel:
    var isPagingLoading = mutableStateOf(false)
        private set

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

    fun getAlbum(token: String, id: String, offset: Int = 0, isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isLoadMore) {
                isPagingLoading.value = true
            } else {
                albumListStatus.value = ApiStatus.LOADING
            }

            var currentTry = 0
            val maxTries = 3
            var isSuccess = false

            // Loop until successful OR we run out of retries
            while (currentTry < maxTries && !isSuccess) {
                try {
                    val result = MainApi.mainServices.getAlbumByArtist(
                        id = id,
                        token = token,
                        offset = offset,
                        limit = 10
                    )

                    if (result.isSuccessful && result.body() != null) {
                        isSuccess = true // Break the loop!

                        val newResponse = result.body()!!
                        if (isLoadMore) {
                            val currentList = _albumListResponse.value?.items.orEmpty()
                            _albumListResponse.value = newResponse.copy(items = currentList + newResponse.items.orEmpty())
                        } else {
                            _albumListResponse.value = newResponse
                        }
                        albumListStatus.value = ApiStatus.SUCCESS

                    } else {
                        // API Error (e.g., 404, 500)
                        if (!isLoadMore) {
                            albumListStatus.value = ApiStatus.ERR
                            isSuccess = true // Break loop for initial load errors
                        } else {
                            // It's a pagination error. Wait and retry.
                            currentTry++
                            if (currentTry < maxTries) delay(2000) // Wait 2 seconds before next try
                        }
                    }
                } catch (e: Exception) {
                    // Network Exception (e.g., No Internet)
                    if (!isLoadMore) {
                        Log.e("SEARCH_EXCEPTION", "Exception occurred", e)

                        if (e is HttpException) {
                            val errorJson = e.response()?.errorBody()?.string()
                            Log.e("SEARCH_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                        }

                        when (e) {
                            is IOException -> _artistDetailErrMsg.value = "conn_err"
                            else -> _artistDetailErrMsg.value = e.message ?: "Unknown error"
                        }
                        artistDetailStatus.value = ApiStatus.ERR
                        isSuccess = true // Break loop for initial load errors
                    } else {
                        // Pagination network error: Wait and retry
                        currentTry++
                        if (currentTry < maxTries) delay(2000)
                    }
                }
            } // end of while loop

            // Finally block logic: Turn off the pagination spinner once everything is done
            if (isLoadMore) {
                isPagingLoading.value = false
            }
        }
    }

    fun loadNextPage(token: String, artistId: String) {
        val currentItemsCount = _albumListResponse.value?.items?.size ?: 0
        val totalAvailable = _albumListResponse.value?.total ?: 0

        // Only load more if we haven't hit the total limit yet
        if (currentItemsCount < totalAvailable) {
            getAlbum(
                token = token,
                id = artistId,
                offset = currentItemsCount, // The current count acts as the next offset!
                isLoadMore = true
            )
        }
    }
}