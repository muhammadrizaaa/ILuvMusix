package org.riza.iluvmusix.dataclass.response

data class ErrResponse(
    val error: ErrData
)

data class ErrData(
    val status: Int? = null,
    val message: String = ""
)
