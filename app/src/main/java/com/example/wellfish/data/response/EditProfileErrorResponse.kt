package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class EditProfileErrorResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class EditProfileErrorData(

	@field:SerializedName("errors")
	val errors: Errors? = null
)

data class EditProfileErrors(

	@field:SerializedName("name")
	val name: List<String?>? = null
)
