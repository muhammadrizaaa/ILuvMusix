package org.riza.iluvmusix.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.main.SearchResponse
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel: ViewModel() {
    private val _searchResult: MutableStateFlow<SearchResponse?> = MutableStateFlow(SearchResponse(
        tracks = null,
        albums = null,
        artists = null
    ))
    val searchResult = _searchResult.asStateFlow()

    private val _searchResultErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchResultErrMsg = _searchResultErrMsg.asStateFlow()

    val searchResultStatus = MutableStateFlow(ApiStatus.NONE)

    fun getSearch(
        q: String,
        token: String
    ){
        viewModelScope.launch {
            searchResultStatus.value = ApiStatus.LOADING
            try {
                val result = MainApi.mainServices.getSearch(
                    q = q,
                    token = token
                )
                if(result.isSuccessful){
                    _searchResult.value = result.body()
                    searchResultStatus.value = ApiStatus.SUCCESS
                }
                else{
                    _searchResultErrMsg.value = "Err"
                    searchResultStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("SEARCH_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("SEARCH_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _searchResultErrMsg.value = "conn_err"
                    else -> _searchResultErrMsg.value = e.message ?: "Unknown error"
                }

                searchResultStatus.value = ApiStatus.ERR
            }
        }
    }
}