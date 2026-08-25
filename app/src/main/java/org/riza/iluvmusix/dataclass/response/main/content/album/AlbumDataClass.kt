package org.riza.iluvmusix.dataclass.response.main.content.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.riza.iluvmusix.dataclass.response.main.ImageDto
import org.riza.iluvmusix.dataclass.response.main.PageResponse

// 1. Root Album Details DTO
@JsonClass(generateAdapter = true)
data class AlbumDetailDto(
    val id: String,
    val name: String,
    @Json(name = "album_type") val albumType: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "total_tracks") val totalTracks: Int?,
    val images: List<ImageDto>?,
    val artists: List<SimpleArtistDto>?,
    val tracks: PageResponse<AlbumTrackDto>?,
    val label: String?
)

// 2. Simplified Track DTO (Tracks inside an album response omit redundant album fields)
@JsonClass(generateAdapter = true)
data class AlbumTrackDto(
    val id: String,
    val name: String,
    @Json(name = "track_number") val trackNumber: Int?,
    @Json(name = "disc_number") val discNumber: Int?,
    @Json(name = "duration_ms") val durationMs: Long?,
    val explicit: Boolean?,
    @Json(name = "preview_url") val previewUrl: String?,
    val artists: List<SimpleArtistDto>?
)

// 4. Artist DTO
@JsonClass(generateAdapter = true)
data class SimpleArtistDto(
    val id: String,
    val name: String
)

// 1. The DTO for the items inside the list
@JsonClass(generateAdapter = true)
data class SimpleAlbumDto(
    val id: String,
    val name: String,
    @Json(name = "album_type") val albumType: String?,
    @Json(name = "album_group") val albumGroup: String?, // Useful for sorting artist discographies (e.g., "appears_on", "single")
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "total_tracks") val totalTracks: Int?,
    val images: List<ImageDto>?,
    val artists: List<SimpleArtistDto>?
)
