package com.example.wellfish.data.response

import com.google.gson.annotations.SerializedName

data class ClassificationHistoryDetailResponse(

	@field:SerializedName("data")
	//val data: Data? = null,
	val data: HistoryDetailData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HistoryDetailData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("food")
	val food: String? = null
)
