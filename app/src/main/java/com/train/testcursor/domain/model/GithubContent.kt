package com.train.testcursor.domain.model

data class GithubContent(
	val name: String,
	val path: String,
	val type: ContentType,
	val size: Int?,
	val downloadUrl: String?
)

enum class ContentType { File, Dir, Unknown }


