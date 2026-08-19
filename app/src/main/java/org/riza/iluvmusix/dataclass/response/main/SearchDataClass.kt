package org.riza.iluvmusix.dataclass.response.main

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 1. Root Search Response
@JsonClass(generateAdapter = true)
data class SearchResponse(
    val tracks: PageResponse<TrackDto>?,
    val albums: PageResponse<AlbumDto>?,
    val artists: PageResponse<ArtistDto>?
)

// 2. Generic Pagination Wrapper
@JsonClass(generateAdapter = true)
data class PageResponse<T>(
    val items: List<T>?,
    val total: Int?,
    val limit: Int?,
    val offset: Int?
)

// 3. Track DTO
@JsonClass(generateAdapter = true)
data class TrackDto(
    val id: String,
    val name: String,
    val album: AlbumDto?,
    val artists: List<ArtistDto>?,
    @Json(name = "duration_ms") val durationMs: Long?,
    val explicit: Boolean?,
    @Json(name = "preview_url") val previewUrl: String?
)

// 4. Album DTO
@JsonClass(generateAdapter = true)
data class AlbumDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>?,
    val artists: List<ArtistDto>?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "total_tracks") val totalTracks: Int?
)

// 5. Artist DTO
@JsonClass(generateAdapter = true)
data class ArtistDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>?,
    val genres: List<String>?,
    val popularity: Int?
)

// 6. Image DTO (Shared across Albums & Artists)
@JsonClass(generateAdapter = true)
data class ImageDto(
    val url: String,
    val height: Int?,
    val width: Int?
)
