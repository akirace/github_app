package com.train.testcursor.data.remote

import com.train.testcursor.data.remote.model.GithubUserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.train.testcursor.data.remote.model.GithubRepoDto
import com.train.testcursor.data.remote.model.SearchUsersResponseDto
import com.train.testcursor.data.remote.model.GithubContentDto
import com.train.testcursor.data.remote.model.GithubRepoDetailDto

interface GithubApiService {
	@GET("users")
	suspend fun getUsers(
		@Query("since") since: Int? = null,
		@Query("per_page") perPage: Int = 30
	): List<GithubUserDto>

	@GET("users/{username}")
	suspend fun getUserDetail(
		@Path("username") username: String
	): GithubUserDto

	@GET("users/{username}/repos")
	suspend fun getUserRepos(
		@Path("username") username: String,
		@Query("per_page") perPage: Int = 30
	): List<GithubRepoDto>

	@GET("repos/{owner}/{repo}/contents/{path}")
	suspend fun getRepoContents(
		@Path("owner") owner: String,
		@Path("repo") repo: String,
		@Path("path") path: String = ""
	): List<GithubContentDto>

	@GET("repos/{owner}/{repo}")
	suspend fun getRepoDetail(
		@Path("owner") owner: String,
		@Path("repo") repo: String
	): GithubRepoDetailDto

	@GET("users/{username}/followers")
	suspend fun getFollowers(
		@Path("username") username: String,
		@Query("per_page") perPage: Int = 30
	): List<GithubUserDto>

	@GET("users/{username}/following")
	suspend fun getFollowing(
		@Path("username") username: String,
		@Query("per_page") perPage: Int = 30
	): List<GithubUserDto>

	@GET("search/users")
	suspend fun searchUsers(
		@Query("q") query: String,
		@Query("per_page") perPage: Int = 30
	): SearchUsersResponseDto
}


