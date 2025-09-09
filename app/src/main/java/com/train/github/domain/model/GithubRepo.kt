package com.train.github.domain.model

data class GithubRepo(
	val id: Int,
	val name: String,
	val description: String?,
	val stars: Int?,
	val language: String?,
	val htmlUrl: String
)



