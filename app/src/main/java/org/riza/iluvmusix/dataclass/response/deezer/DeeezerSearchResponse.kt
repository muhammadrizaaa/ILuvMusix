package org.riza.iluvmusix.dataclass.response.deezer

data class DeezerSearchResponse(
    val data: List<DeezerSearchData>
)

data class DeezerSearchData(
    val id: String,
    val title: String,
    val artist: DeezerArtistDataClass,
    val album: DeezerAlbumDataClass,
    val preview: String
)

data class DeezerArtistDataClass(
    val id: String,
    val name: String
)

data class DeezerAlbumDataClass(
    val id: String,
    val title: String
)