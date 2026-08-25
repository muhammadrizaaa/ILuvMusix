package org.riza.iluvmusix.ui.screen.content.album

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.ErrResponse
import org.riza.iluvmusix.dataclass.response.main.content.album.AlbumDetailDto
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class AlbumDetailViewModel: ViewModel() {
    private val _albumDetailDto: MutableStateFlow<AlbumDetailDto?> = MutableStateFlow(null)
    val albumDetailDto = _albumDetailDto.asStateFlow()

    private val _albumDetailErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val albumDetailErrMsg = _albumDetailErrMsg.asStateFlow()

    val albumDetailStatus = MutableStateFlow(ApiStatus.NONE)

    fun getAlbumById(
        id: String,
        token: String
    ){
        viewModelScope.launch {
            albumDetailStatus.value = ApiStatus.LOADING
            try {
                val result = MainApi.mainServices.getAlbumById(
                    id = id,
                    token = token
                )
                if(result.isSuccessful){
                    _albumDetailDto.value = result.body()
                    albumDetailStatus.value = ApiStatus.SUCCESS
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        ErrResponse::class.java
                    )
                    _albumDetailErrMsg.value = error.error.message
                    albumDetailStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("SEARCH_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("SEARCH_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _albumDetailErrMsg.value = "conn_err"
                    else -> _albumDetailErrMsg.value = e.message ?: "Unknown error"
                }

                albumDetailStatus.value = ApiStatus.ERR
            }
        }
    }
}