package com.train.github.data.mapper

import com.train.github.data.local.FavoriteUserEntity
import com.train.github.data.remote.model.GithubUserDto
import com.train.github.data.remote.model.GithubRepoDto
import com.train.github.data.remote.model.GithubContentDto
import com.train.github.data.remote.model.GithubRepoDetailDto
import com.train.github.data.remote.model.GithubBranchDto
import com.train.github.domain.model.GithubUser
import com.train.github.domain.model.GithubRepo
import com.train.github.domain.model.GithubContent
import com.train.github.domain.model.ContentType
import com.train.github.domain.model.GithubRepoDetail
import com.train.github.domain.model.GithubBranch

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

fun GithubBranchDto.toDomain(): GithubBranch = GithubBranch(
	name = name
)


