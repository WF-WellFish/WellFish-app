package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
	@field:SerializedName("data")
	//val data: List<Any>,
	val data: DataRegister? = null, //original

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataRegister(
	@field:SerializedName("errors")
	val errors: Errors? = null
)

data class Errors(
	@field:SerializedName("username")
	val username: List<String?>? = null
)
