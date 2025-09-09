package com.train.github.domain.repository

import com.train.github.domain.model.GithubUser
import com.train.github.domain.model.GithubRepo
import com.train.github.domain.model.GithubContent
import com.train.github.domain.model.GithubRepoDetail
import com.train.github.domain.model.GithubBranch
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
	suspend fun getUsers(since: Int? = null, perPage: Int = 30): List<GithubUser>
	suspend fun getUserDetail(username: String): GithubUser
	suspend fun getUserRepos(username: String, perPage: Int = 30): List<GithubRepo>
	suspend fun getRepoContents(owner: String, repo: String, path: String = "", ref: String? = null): List<GithubContent>
	suspend fun getRepoDetail(owner: String, repo: String): GithubRepoDetail
	suspend fun getRepoBranches(owner: String, repo: String): List<GithubBranch>
	suspend fun getFollowers(username: String, perPage: Int = 30): List<GithubUser>
	suspend fun getFollowing(username: String, perPage: Int = 30): List<GithubUser>
	suspend fun searchUsers(query: String, perPage: Int = 30): List<GithubUser>

	fun observeFavorites(): Flow<List<GithubUser>>
	fun isFavorite(userId: Int): Flow<Boolean>
	suspend fun addFavorite(user: GithubUser)
	suspend fun removeFavorite(user: GithubUser)
}


