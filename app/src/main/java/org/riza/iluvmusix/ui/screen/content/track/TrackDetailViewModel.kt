package org.riza.iluvmusix.ui.screen.content.track

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.riza.iluvmusix.dataclass.response.DeezerErrorResponse
import org.riza.iluvmusix.dataclass.response.ErrResponse
import org.riza.iluvmusix.dataclass.response.ITunesErrorResponse
import org.riza.iluvmusix.dataclass.response.deezer.DeezerSearchResponse
import org.riza.iluvmusix.dataclass.response.itunes.ITunesSearchResponse
import org.riza.iluvmusix.dataclass.response.main.content.track.TrackDetailDto
import org.riza.iluvmusix.network.ApiStatus
import org.riza.iluvmusix.network.DeezerApi
import org.riza.iluvmusix.network.ITunesApi
import org.riza.iluvmusix.network.MainApi
import retrofit2.HttpException
import java.io.IOException

class TrackDetailViewModel: ViewModel() {
    private val _trackDetailResponse: MutableStateFlow<TrackDetailDto?> = MutableStateFlow(null)
    val trackDetailResponse = _trackDetailResponse.asStateFlow()

    private val _iTunesSearchTrackResponse: MutableStateFlow<ITunesSearchResponse?> = MutableStateFlow(null)
    val iTunesSearchTrackResponse = _iTunesSearchTrackResponse.asStateFlow()
    private val _deezerSearchTrackResponse: MutableStateFlow<DeezerSearchResponse?> = MutableStateFlow(null)
    val deezerSearchTrackResponse = _deezerSearchTrackResponse.asStateFlow()

    private val _trackDetailErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val trackDetailErrMsg = _trackDetailErrMsg.asStateFlow()
    private val _iTunesSearchTrackErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val iTunesSearchTrackErrMsg = _iTunesSearchTrackErrMsg.asStateFlow()
    private val _deezerSearchTrackErrMsg: MutableStateFlow<String?> = MutableStateFlow(null)
    val deezerSearchTrackErrMsg = _deezerSearchTrackErrMsg.asStateFlow()

    val trackDetailStatus = MutableStateFlow(ApiStatus.NONE)
    val iTunesSearchTrackStatus = MutableStateFlow(ApiStatus.NONE)
    val deezerSearchTrackStatus = MutableStateFlow(ApiStatus.NONE)

    fun getTrackById(
        id: String,
        token: String
    ){
        viewModelScope.launch {
            _trackDetailErrMsg.value = null
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

                trackDetailStatus.value = ApiStatus.ERR
            }
        }
    }
    fun getItunesSearch(title: String, artist: String, collection: String){
        viewModelScope.launch {
            _iTunesSearchTrackErrMsg.value = null
            iTunesSearchTrackStatus.value = ApiStatus.LOADING
            try {
                val result = ITunesApi.iTunesService.getSongPreview(
                    searchQuery = "$artist $title",
                )
                if(result.isSuccessful){
                    val results = result.body()?.results
                    if (!results.isNullOrEmpty()) {
                        // Filter the results locally to find the EXACT track name match
                        val exactMatchWithCollection = results.find { track ->
                            val cleanApiTitle = cleanTitle(track.trackName?:"")
                            val cleanLocalTitle = cleanTitle(title)

                            val normApiTitle = cleanApiTitle.normalize()
                            val normLocalTitle = cleanLocalTitle.normalize()

                            val isTrackMatch = normApiTitle == normLocalTitle ||
                                    normApiTitle.contains(normLocalTitle) ||
                                    normLocalTitle.contains(normApiTitle)

                            val cleanApiArtist = track.artistName ?: ""
                            val isArtistMatch = cleanApiArtist.contains(artist, ignoreCase = true) ||
                                    artist.contains(cleanApiArtist, ignoreCase = true)
                            val cleanApiCollection = track.collectionName ?: ""
                            val isCollectionMatch = cleanApiCollection.contains(collection, ignoreCase = true) ||
                                    collection.contains(cleanApiCollection, ignoreCase = true)

                            Log.d("ITUNES", "Compare Title: '$cleanApiTitle' vs '$cleanLocalTitle' -> Match: $isTrackMatch")
                            Log.d("ITUNES", "Compare Artist: '$cleanApiArtist' vs '$artist' -> Match: $isArtistMatch")

                            isTrackMatch && isArtistMatch && isCollectionMatch
                        }
                        val exactMatch = results.find { track ->
                            val cleanApiTitle = cleanTitle(track.trackName?:"")
                            val cleanLocalTitle = cleanTitle(title)

                            // Normalize both strings to ignore commas, periods, and spacing differences
                            val normApiTitle = cleanApiTitle.normalize()
                            val normLocalTitle = cleanLocalTitle.normalize()

                            val isTrackMatch = normApiTitle == normLocalTitle ||
                                    normApiTitle.contains(normLocalTitle) ||
                                    normLocalTitle.contains(normApiTitle)

                            val cleanApiArtist = track.artistName ?: ""
                            val normApiArtist = cleanApiArtist.normalize()
                            val normLocalArtist = artist.normalize()

                            val isArtistMatch = normApiArtist.contains(normLocalArtist) ||
                                    normLocalArtist.contains(normApiArtist)

                            Log.d("ITUNES", "Compare Title: '$normApiTitle' vs '$normLocalTitle' -> Match: $isTrackMatch")
                            Log.d("ITUNES", "Compare Artist: '$normApiArtist' vs '$normLocalArtist' -> Match: $isArtistMatch")

                            isTrackMatch && isArtistMatch
                        }

                        // 2. Safe resolution
                        val previewUrl = exactMatchWithCollection?.previewUrl?: exactMatch?.previewUrl

                        if (previewUrl != null) {
                            Log.d("ITUNES", "Found exact match preview: $exactMatch $previewUrl")
                            // playAudio(previewUrl)
                            _iTunesSearchTrackResponse.value = ITunesSearchResponse(
                                results = listOf(
                                    exactMatchWithCollection?: exactMatch !!
                                )
                            )
                            iTunesSearchTrackStatus.value = ApiStatus.SUCCESS
                        } else {
                            Log.e("ITUNES", "Song found, but no audio preview is available.")
                            _iTunesSearchTrackResponse.value = null
                            iTunesSearchTrackStatus.value = ApiStatus.ERR
                        }
                    }
                    else{
                        Log.e("ITUNES", "no song found.")
                        _iTunesSearchTrackResponse.value = null
                        iTunesSearchTrackStatus.value = ApiStatus.ERR
                    }
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        ITunesErrorResponse::class.java
                    )
                    _iTunesSearchTrackErrMsg.value = error.errorMessage
                    iTunesSearchTrackStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("TRACK_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("TRACK_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _iTunesSearchTrackErrMsg.value = "conn_err"
                    else -> _iTunesSearchTrackErrMsg.value = e.message ?: "Unknown error"
                }

                iTunesSearchTrackStatus.value = ApiStatus.ERR
            }
        }
    }
    fun getDeezerSearch(title: String, artist: String){
        viewModelScope.launch {
            _deezerSearchTrackErrMsg.value = null
            deezerSearchTrackStatus.value = ApiStatus.LOADING
            try {
                val result = DeezerApi.deezerService.getSongPreview(
                    searchQuery = "$artist $title",
                )
                if(result.isSuccessful){
                    val results = result.body()?.data
                    if (!results.isNullOrEmpty()) {
                        // Filter the results locally to find the EXACT track name match
                        val exactMatch = results.find { track ->
                            val cleanApiTitle = cleanTitle(track.title)
                            val cleanLocalTitle = cleanTitle(title)

                            // Normalize both strings to ignore commas, periods, and spacing differences
                            val normApiTitle = cleanApiTitle.normalize()
                            val normLocalTitle = cleanLocalTitle.normalize()

                            val isTrackMatch = normApiTitle == normLocalTitle ||
                                    normApiTitle.contains(normLocalTitle) ||
                                    normLocalTitle.contains(normApiTitle)

                            val cleanApiArtist = track.artist.name
                            val normApiArtist = cleanApiArtist.normalize()
                            val normLocalArtist = artist.normalize()

                            val isArtistMatch = normApiArtist.contains(normLocalArtist) ||
                                    normLocalArtist.contains(normApiArtist)

                            Log.d("DEEZER", "Compare Title: '$normApiTitle' vs '$normLocalTitle' -> Match: $isTrackMatch")
                            Log.d("DEEZER", "Compare Artist: '$normApiArtist' vs '$normLocalArtist' -> Match: $isArtistMatch")

                            isTrackMatch && isArtistMatch
                        }

                        // 2. Safe resolution
                        val previewUrl = exactMatch?.preview

                        if (previewUrl != null) {
                            Log.d("DEEZER", previewUrl)
                            // playAudio(previewUrl)
                            _deezerSearchTrackResponse.value = DeezerSearchResponse(
                                data = listOf(
                                    exactMatch
                                )
                            )
                            deezerSearchTrackStatus.value = ApiStatus.SUCCESS
                        } else {
                            Log.e("DEEZER", "Song found, but no audio preview is available.")
                            _deezerSearchTrackResponse.value = null
                            deezerSearchTrackStatus.value = ApiStatus.ERR
                        }
                    }
                    else{
                        Log.e("DEEZER", "no song found.")
                        _deezerSearchTrackResponse.value = null
                        deezerSearchTrackStatus.value = ApiStatus.ERR
                    }
                }
                else {
                    val error = Gson().fromJson(
                        result.errorBody()?.string(),
                        DeezerErrorResponse::class.java
                    )
                    Log.e("DEEZER_ERR", "$error")
                    error.error?.code?.let {
                        _deezerSearchTrackErrMsg.value = it.toString()
                    }
                    deezerSearchTrackStatus.value = ApiStatus.ERR
                }
            }
            catch (e: Exception) {
                Log.e("TRACK_EXCEPTION", "Exception occurred", e)

                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    Log.e("TRACK_RESPONSE_ERR_RAW", errorJson ?: "No error body")
                }

                when (e) {
                    is IOException -> _deezerSearchTrackErrMsg.value = "conn_err"
                    else -> _deezerSearchTrackErrMsg.value = e.message ?: "Unknown error"
                }
                deezerSearchTrackStatus.value = ApiStatus.ERR
            }
        }
    }
    fun cleanTitle(input: String): String {
        val tagRegex = Regex("(?i)\\s*[\\[(](feat|ft|remix|version|deluxe|live|demo|edit|remastered).*?[])]")
        val cleaned = input.replace(tagRegex, "").trim()

        // Fallback: If stripping left us empty, revert to original string
        return cleaned.ifEmpty { input }
    }
    fun String.normalize(): String {
        return this.lowercase()
            .replace(Regex("[^a-z0-9]"), "") // Keeps only letters and numbers
    }
}
