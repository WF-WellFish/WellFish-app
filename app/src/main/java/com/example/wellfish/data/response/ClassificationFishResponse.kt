package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ClassificationFishResponse(

	@field:SerializedName("data")
	//val data: Data? = null,
	val data: ClassificationFishData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ClassificationFishData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("food")
	val food: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null
)
