package com.train.testcursor.data.mapper

import com.train.testcursor.data.local.FavoriteUserEntity
import com.train.testcursor.data.remote.model.GithubUserDto
import com.train.testcursor.data.remote.model.GithubRepoDto
import com.train.testcursor.data.remote.model.GithubContentDto
import com.train.testcursor.data.remote.model.GithubRepoDetailDto
import com.train.testcursor.domain.model.GithubUser
import com.train.testcursor.domain.model.GithubRepo
import com.train.testcursor.domain.model.GithubContent
import com.train.testcursor.domain.model.ContentType
import com.train.testcursor.domain.model.GithubRepoDetail

fun GithubUserDto.toDomain(): GithubUser = GithubUser(
	id = id,
	login = login,
	name = name,
	avatarUrl = avatarUrl,
	htmlUrl = htmlUrl,
	location = location,
	followers = followers,
	following = following,
	publicRepos = publicRepos
)

fun GithubUser.toFavoriteEntity(): FavoriteUserEntity = FavoriteUserEntity(
	id = id,
	login = login,
	avatarUrl = avatarUrl.orEmpty()
)

fun GithubRepoDto.toDomain(): GithubRepo = GithubRepo(
	id = id,
	name = name,
	description = description,
	stars = stars,
	language = language,
	htmlUrl = htmlUrl
)

fun GithubContentDto.toDomain(): GithubContent = GithubContent(
	name = name,
	path = path,
	type = when (type) {
		"file" -> ContentType.File
		"dir" -> ContentType.Dir
		else -> ContentType.Unknown
	},
	size = size,
	downloadUrl = downloadUrl
)

fun GithubRepoDetailDto.toDomain(): GithubRepoDetail = GithubRepoDetail(
	id = id,
	name = name,
	fullName = fullName,
	description = description,
	stars = stars,
	forks = forks,
	openIssues = openIssues,
	language = language,
	defaultBranch = defaultBranch,
	updatedAt = updatedAt,
	htmlUrl = htmlUrl
)


