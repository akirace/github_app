package com.train.testcursor.data.remote.model

import com.squareup.moshi.Json

data class GithubContentDto(
	@Json(name = "name") val name: String,
	@Json(name = "path") val path: String,
	@Json(name = "type") val type: String,
	@Json(name = "size") val size: Int?,
	@Json(name = "download_url") val downloadUrl: String?
)


