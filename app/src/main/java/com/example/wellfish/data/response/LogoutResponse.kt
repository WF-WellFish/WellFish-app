package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

//new1/3
data class LogoutResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(
	val any: Any? = null
)
