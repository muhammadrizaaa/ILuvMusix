package org.riza.iluvmusix.network

import android.util.Base64
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.riza.iluvmusix.dataclass.response.auth.TokenResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

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

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface MainApiService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String = org.riza.iluvmusix.network.authHeader,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<TokenResponse>
}

object MainApi{
    val services: MainApiService by lazy {
        retrofit.create(MainApiService::class.java)
    }
}

enum class ApiStatus{
    NONE, LOADING, SUCCESS, ERR
}