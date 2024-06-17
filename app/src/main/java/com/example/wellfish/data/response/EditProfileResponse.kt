package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class EditProfileResponse(
	@field:SerializedName("data")
	val data: EditProfileData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class EditProfileData(
	@field:SerializedName("user")
	val user: EditProfileUser? = null
)

data class EditProfileUser(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("profile_picture")
	val profilePicture: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
