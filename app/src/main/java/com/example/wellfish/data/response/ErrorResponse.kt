package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field: SerializedName("message")
    val message: String
)

