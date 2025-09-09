package com.train.testcursor.domain.repository

import com.train.testcursor.domain.model.GithubUser
import com.train.testcursor.domain.model.GithubRepo
import com.train.testcursor.domain.model.GithubContent
import com.train.testcursor.domain.model.GithubRepoDetail
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
	suspend fun getUsers(since: Int? = null, perPage: Int = 30): List<GithubUser>
	suspend fun getUserDetail(username: String): GithubUser
	suspend fun getUserRepos(username: String, perPage: Int = 30): List<GithubRepo>
	suspend fun getRepoContents(owner: String, repo: String, path: String = ""): List<GithubContent>
	suspend fun getRepoDetail(owner: String, repo: String): GithubRepoDetail
	suspend fun getFollowers(username: String, perPage: Int = 30): List<GithubUser>
	suspend fun getFollowing(username: String, perPage: Int = 30): List<GithubUser>
	suspend fun searchUsers(query: String, perPage: Int = 30): List<GithubUser>

	fun observeFavorites(): Flow<List<GithubUser>>
	fun isFavorite(userId: Int): Flow<Boolean>
	suspend fun addFavorite(user: GithubUser)
	suspend fun removeFavorite(user: GithubUser)
}


