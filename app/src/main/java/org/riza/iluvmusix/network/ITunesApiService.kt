package org.riza.iluvmusix.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.riza.iluvmusix.dataclass.response.itunes.ITunesSearchResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL_I_TUNES = "https://itunes.apple.com/"
private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val iTunesRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_I_TUNES)
    .client(client)
    .build()
interface ITunesApiService {
    @GET("search")
    suspend fun getSongPreview(
        @Query("term") searchQuery: String,
        @Query("entity") entity: String = "song", // Forces it to only look for music tracks
        @Query("limit") limit: Int = 1            // We only need the top result
    ): Response<ITunesSearchResponse>
}

object ITunesApi{
    val iTunesService: ITunesApiService by lazy {
        iTunesRetrofit.create(ITunesApiService::class.java)
    }
}