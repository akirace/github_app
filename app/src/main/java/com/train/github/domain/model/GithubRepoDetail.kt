package com.train.github.domain.model

data class GithubRepoDetail(
	val id: Int,
	val name: String,
	val fullName: String,
	val description: String?,
	val stars: Int?,
	val forks: Int?,
	val openIssues: Int?,
	val language: String?,
	val defaultBranch: String?,
	val updatedAt: String?,
	val htmlUrl: String
)


