package com.train.github.data.remote.model

import com.squareup.moshi.Json

data class GithubBranchDto(
	@Json(name = "name") val name: String
)



