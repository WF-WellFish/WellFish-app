package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ChangePasswordErrorResponse(

	@field:SerializedName("data")
	//val data: Data? = null,
	val data: ChangePasswordErrorData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)


data class ChangePasswordErrorData(

	@field:SerializedName("errors")
	//val errors: Errors? = null
	val errors: ChangePasswordErrors? = null
)

data class ChangePasswordErrors(

	@field:SerializedName("old_password")
	val oldPassword: List<String?>? = null,

	@field:SerializedName("new_password")
	val newPassword: List<String?>? = null
)


