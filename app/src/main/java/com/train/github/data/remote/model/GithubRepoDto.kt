package com.train.github.data.remote.model

import com.squareup.moshi.Json

data class GithubRepoDto(
	@Json(name = "id") val id: Int,
	@Json(name = "name") val name: String,
	@Json(name = "description") val description: String?,
	@Json(name = "stargazers_count") val stars: Int?,
	@Json(name = "language") val language: String?,
	@Json(name = "html_url") val htmlUrl: String
)



