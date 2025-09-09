package com.train.github.domain.usecase

import com.train.github.domain.model.GithubUser
import com.train.github.domain.model.GithubRepo
import com.train.github.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(since: Int? = null, perPage: Int = 30): List<GithubUser> =
		repository.getUsers(since, perPage)
}

class GetUserDetailUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(username: String): GithubUser = repository.getUserDetail(username)
}

class GetUserReposUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(username: String, perPage: Int = 30): List<GithubRepo> = repository.getUserRepos(username, perPage)
}

class GetFollowersUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(username: String, perPage: Int = 30): List<GithubUser> = repository.getFollowers(username, perPage)
}

class GetFollowingUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(username: String, perPage: Int = 30): List<GithubUser> = repository.getFollowing(username, perPage)
}

class SearchUsersUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(query: String, perPage: Int = 30): List<GithubUser> = repository.searchUsers(query, perPage)
}

class ObserveFavoritesUseCase(private val repository: GithubRepository) {
	operator fun invoke(): Flow<List<GithubUser>> = repository.observeFavorites()
}

class IsFavoriteUseCase(private val repository: GithubRepository) {
	operator fun invoke(userId: Int): Flow<Boolean> = repository.isFavorite(userId)
}

class AddFavoriteUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(user: GithubUser) = repository.addFavorite(user)
}

class RemoveFavoriteUseCase(private val repository: GithubRepository) {
	suspend operator fun invoke(user: GithubUser) = repository.removeFavorite(user)
}


