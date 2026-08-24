package org.riza.iluvmusix.dataclass.response.main.content.track

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.riza.iluvmusix.dataclass.response.main.ArtistDto
import org.riza.iluvmusix.dataclass.response.main.ImageDto

@JsonClass(generateAdapter = true)
data class TrackDetailDto(
    val id: String,
    val name: String,
    val explicit: Boolean?,
    @Json(name = "duration_ms") val durationMs: Long?,
    @Json(name = "preview_url") val previewUrl: String?, // Remember: this is often null!
    val album: TrackAlbumDto?,
    val artists: List<ArtistDto>?
)
@JsonClass(generateAdapter = true)
data class TrackAlbumDto(
    val id: String,
    val name: String,
    @Json(name = "release_date") val releaseDate: String?,
    val images: List<ImageDto>?
)