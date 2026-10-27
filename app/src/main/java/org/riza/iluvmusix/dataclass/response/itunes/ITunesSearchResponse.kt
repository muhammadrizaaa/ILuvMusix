package org.riza.iluvmusix.dataclass.response.itunes

data class ITunesSearchResponse(
    val results: List<ITunesTrackDto>
)

data class ITunesTrackDto(
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val previewUrl: String? // This is the 30-second .m4a audio file!
)
