package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ClassificationFishErrorResponse(
	@field:SerializedName("data")
	val data: ClassificationData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ClassificationData(
	@field:SerializedName("errors")
	val errors: ClassificationErrors? = null
)

data class ClassificationErrors(
	@field:SerializedName("image")
	val image: List<String?>? = null
)
