package org.riza.iluvmusix.ui.screen.content.track

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.ErrResponse
import org.riza.iluvmusix.dataclass.response.main.content.track.TrackDetailDto
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class TrackDetailViewModel: ViewModel() {
    private val _trackDetailResponse: MutableStateFlow<TrackDetailDto?> = MutableStateFlow(null)
    val trackDetailResponse = _trackDetailResponse.asStateFlow()

    private val _trackDetailErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val trackDetailErrMsg = _trackDetailErrMsg.asStateFlow()

    val trackDetailStatus = MutableStateFlow(ApiStatus.NONE)

    fun getTrackById(
        id: String,
        token: String
    ){
        viewModelScope.launch {
            trackDetailStatus.value = ApiStatus.LOADING
            try {
                val result = MainApi.mainServices.getTrackById(
                    id = id,
                    token = token
                )
                if(result.isSuccessful){
                    _trackDetailResponse.value = result.body()
                    trackDetailStatus.value = ApiStatus.SUCCESS
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        ErrResponse::class.java
                    )
                    _trackDetailErrMsg.value = error.error.message
                    trackDetailStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("TRACK_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("TRACK_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _trackDetailErrMsg.value = "conn_err"
                    else -> _trackDetailErrMsg.value = e.message ?: "Unknown error"
                }

                trackDetailStatus.value = ApiStatus.NONE
            }
        }
    }
}