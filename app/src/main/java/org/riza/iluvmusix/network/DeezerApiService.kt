package org.riza.iluvmusix.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.riza.iluvmusix.BuildConfig
import org.riza.iluvmusix.dataclass.response.deezer.DeezerSearchResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL_DEEZER = "https://api.deezer.com/"

private val logging = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY // Full details in Debug
    } else {
        HttpLoggingInterceptor.Level.NONE // Completely silent in Release
    }
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val deezerRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_DEEZER)
    .client(client)
    .build()

interface DeezerApiService {
    @GET("search")
    suspend fun getSongPreview(
        @Query("q") searchQuery: String,
    ): Response<DeezerSearchResponse>
}

object DeezerApi{
    val deezerService: DeezerApiService by lazy {
        deezerRetrofit.create(DeezerApiService::class.java)
    }
}