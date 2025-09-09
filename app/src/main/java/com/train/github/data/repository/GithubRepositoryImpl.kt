package com.train.github.data.repository

import com.train.github.data.local.FavoriteUserDao
import com.train.github.data.mapper.toDomain
import com.train.github.data.mapper.toFavoriteEntity
import com.train.github.data.remote.GithubApiService
import com.train.github.domain.model.GithubUser
import com.train.github.domain.model.GithubRepo
import com.train.github.domain.repository.GithubRepository
import com.train.github.domain.model.GithubContent
import com.train.github.domain.model.GithubRepoDetail
import com.train.github.domain.model.GithubBranch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GithubRepositoryImpl(
	private val api: GithubApiService,
	private val favoriteDao: FavoriteUserDao
) : GithubRepository {

	override suspend fun getUsers(since: Int?, perPage: Int): List<GithubUser> {
		return api.getUsers(since = since, perPage = perPage).map { it.toDomain() }
	}

	override suspend fun getUserDetail(username: String): GithubUser {
		return api.getUserDetail(username).toDomain()
	}

	override suspend fun getUserRepos(username: String, perPage: Int): List<GithubRepo> {
		return api.getUserRepos(username = username, perPage = perPage).map { it.toDomain() }
	}

	override suspend fun getRepoContents(owner: String, repo: String, path: String, ref: String?): List<GithubContent> {
		return api.getRepoContents(owner = owner, repo = repo, path = if (path.isEmpty()) "" else path, ref = ref).map { it.toDomain() }
	}

	override suspend fun getRepoDetail(owner: String, repo: String): GithubRepoDetail {
		return api.getRepoDetail(owner, repo).toDomain()
	}

	override suspend fun getRepoBranches(owner: String, repo: String): List<GithubBranch> {
		return api.getRepoBranches(owner, repo).map { it.toDomain() }
	}

	override suspend fun getFollowers(username: String, perPage: Int): List<GithubUser> {
		return api.getFollowers(username = username, perPage = perPage).map { it.toDomain() }
	}

	override suspend fun getFollowing(username: String, perPage: Int): List<GithubUser> {
		return api.getFollowing(username = username, perPage = perPage).map { it.toDomain() }
	}

	override suspend fun searchUsers(query: String, perPage: Int): List<GithubUser> {
		return api.searchUsers(query = query, perPage = perPage).items.map { it.toDomain() }
	}

	override fun observeFavorites(): Flow<List<GithubUser>> =
		favoriteDao.observeFavorites().map { list ->
			list.map { entity ->
				GithubUser(
					id = entity.id,
					login = entity.login,
					name = null,
					avatarUrl = entity.avatarUrl,
					htmlUrl = null,
					location = null,
					followers = null,
					following = null,
					publicRepos = null
				)
			}
		}

	override fun isFavorite(userId: Int): Flow<Boolean> = favoriteDao.isFavorite(userId)

	override suspend fun addFavorite(user: GithubUser) {
		favoriteDao.insert(user.toFavoriteEntity())
	}

	override suspend fun removeFavorite(user: GithubUser) {
		favoriteDao.delete(user.toFavoriteEntity())
	}
}


