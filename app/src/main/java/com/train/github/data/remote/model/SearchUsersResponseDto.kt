package com.train.github.data.remote.model

import com.squareup.moshi.Json

data class SearchUsersResponseDto(
	@Json(name = "total_count") val totalCount: Int,
	@Json(name = "items") val items: List<GithubUserDto>
)



