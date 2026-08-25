package org.riza.iluvmusix.network

import android.util.Base64
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.riza.iluvmusix.dataclass.response.auth.TokenResponse
import org.riza.iluvmusix.dataclass.response.main.ArtistDto
import org.riza.iluvmusix.dataclass.response.main.PageResponse
import org.riza.iluvmusix.dataclass.response.main.SearchResponse
import org.riza.iluvmusix.dataclass.response.main.content.album.AlbumDetailDto
import org.riza.iluvmusix.dataclass.response.main.content.album.SimpleAlbumDto
import org.riza.iluvmusix.dataclass.response.main.content.track.TrackDetailDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://accounts.spotify.com/"
const val BASE_URL_1 = "https://api.spotify.com/v1/"
const val CLIENT_ID = "dceffae25edc45dfa77ccbe71d128bbc"
const val CLIENT_SECRET = "e792a7dcbdb64e019b27b3430e198adf"
fun getBasicAuthHeader(clientId: String, clientSecret: String): String {
    val rawString = "$clientId:$clientSecret"
    val base64String = Base64.encodeToString(rawString.toByteArray(), Base64.NO_WRAP)
    return "Basic $base64String"
}
val authHeader = getBasicAuthHeader(CLIENT_ID, CLIENT_SECRET)
private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val authRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

private val mainRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_1)
    .client(client)
    .build()

interface AuthApiService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String = org.riza.iluvmusix.network.authHeader,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<TokenResponse>

}

interface MainApiService{
    @GET("search")
    suspend fun getSearch(
        @Header("Authorization") token: String,
        @Query("q") q: String,
        @Query("type") type: String = "album,artist,track"
    ): Response<SearchResponse>

    @GET("albums/{id}")
    suspend fun getAlbumById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<AlbumDetailDto>

    @GET("artists/{id}")
    suspend fun getArtistById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ArtistDto>

    @GET("artists/{id}/albums")
    suspend fun getAlbumByArtist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("include_groups") includeGroups: String = "album,single,compilation",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int
    ): Response<PageResponse<SimpleAlbumDto>>

    @GET("tracks/{id}")
    suspend fun getTrackById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TrackDetailDto>
}

object MainApi{
    val authServices: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    val mainServices: MainApiService by lazy {
        mainRetrofit.create(MainApiService::class.java)
    }
}

enum class ApiStatus{
    NONE, LOADING, SUCCESS, ERR
}