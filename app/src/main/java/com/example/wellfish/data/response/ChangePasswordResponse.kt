package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ChangePasswordData(
	val any: Any? = null
)
