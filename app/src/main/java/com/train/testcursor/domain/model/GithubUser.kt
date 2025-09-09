package com.train.testcursor.domain.model

data class GithubUser(
	val id: Int,
	val login: String,
	val name: String?,
	val avatarUrl: String?,
	val htmlUrl: String?,
	val location: String?,
	val followers: Int?,
	val following: Int?,
	val publicRepos: Int?
)



