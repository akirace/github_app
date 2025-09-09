package com.train.github.data.remote.model

import com.squareup.moshi.Json

data class GithubRepoDetailDto(
	@Json(name = "id") val id: Int,
	@Json(name = "name") val name: String,
	@Json(name = "full_name") val fullName: String,
	@Json(name = "description") val description: String?,
	@Json(name = "stargazers_count") val stars: Int?,
	@Json(name = "forks_count") val forks: Int?,
	@Json(name = "open_issues_count") val openIssues: Int?,
	@Json(name = "language") val language: String?,
	@Json(name = "default_branch") val defaultBranch: String?,
	@Json(name = "updated_at") val updatedAt: String?,
	@Json(name = "html_url") val htmlUrl: String
)


