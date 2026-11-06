package org.riza.iluvmusix.dataclass.response

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

data class ErrResponse(
    val error: ErrData
)

data class ErrData(
    val status: Int? = null,
    val message: String = ""
)

data class ITunesErrorResponse(
    val errorMessage: String?
)

@Keep
@JsonClass(generateAdapter = true)
data class DeezerErrorResponse(
    val error: DeezerErrorDetail? = null
)

@Keep
@JsonClass(generateAdapter = true)
data class DeezerErrorDetail(
    val type: String? = null,
    val message: String? = null,
    val code: Int? = null
)
