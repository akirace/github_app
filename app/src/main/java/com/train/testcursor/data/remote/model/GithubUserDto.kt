package com.train.testcursor.data.remote.model

import com.squareup.moshi.Json

data class GithubUserDto(
	@Json(name = "login") val login: String,
	@Json(name = "id") val id: Int,
	@Json(name = "avatar_url") val avatarUrl: String?,
	@Json(name = "html_url") val htmlUrl: String?,
	@Json(name = "name") val name: String? = null,
	@Json(name = "blog") val blog: String? = null,
	@Json(name = "location") val location: String? = null,
	@Json(name = "followers") val followers: Int? = null,
	@Json(name = "following") val following: Int? = null,
	@Json(name = "public_repos") val publicRepos: Int? = null,
)



