package com.train.github.di

import android.content.Context
import androidx.room.Room
import com.train.github.data.local.AppDatabase
import com.train.github.data.remote.NetworkModule
import com.train.github.data.repository.GithubRepositoryImpl
import com.train.github.domain.repository.GithubRepository
import com.train.github.domain.usecase.AddFavoriteUseCase
import com.train.github.domain.usecase.GetUserDetailUseCase
import com.train.github.domain.usecase.GetUsersUseCase
import com.train.github.domain.usecase.GetUserReposUseCase
import com.train.github.domain.usecase.GetFollowersUseCase
import com.train.github.domain.usecase.GetFollowingUseCase
import com.train.github.domain.usecase.SearchUsersUseCase
import com.train.github.domain.usecase.IsFavoriteUseCase
import com.train.github.domain.usecase.ObserveFavoritesUseCase
import com.train.github.domain.usecase.RemoveFavoriteUseCase
import com.train.github.domain.usecase.GetRepoContentsUseCase
import com.train.github.domain.usecase.GetRepoDetailUseCase
import com.train.github.domain.usecase.GetRepoBranchesUseCase

object ServiceLocator {
	@Volatile
	private var database: AppDatabase? = null

	private fun provideDatabase(context: Context): AppDatabase {
		return database ?: synchronized(this) {
			Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				"app-db"
			).fallbackToDestructiveMigration().build().also { database = it }
		}
	}

	private fun provideRepository(context: Context): GithubRepository {
		val db = provideDatabase(context)
		return GithubRepositoryImpl(
			api = NetworkModule.apiService,
			favoriteDao = db.favoriteUserDao()
		)
	}

	data class UseCases(
		val getUsers: GetUsersUseCase,
		val getUserDetail: GetUserDetailUseCase,
		val getUserRepos: GetUserReposUseCase,
		val getFollowers: GetFollowersUseCase,
		val getFollowing: GetFollowingUseCase,
		val searchUsers: SearchUsersUseCase,
		val observeFavorites: ObserveFavoritesUseCase,
		val isFavorite: IsFavoriteUseCase,
		val addFavorite: AddFavoriteUseCase,
		val removeFavorite: RemoveFavoriteUseCase,
		val getRepoContents: GetRepoContentsUseCase,
		val getRepoDetail: GetRepoDetailUseCase,
		val getRepoBranches: GetRepoBranchesUseCase
	)

	fun provideUseCases(context: Context): UseCases {
		val repo = provideRepository(context)
		return UseCases(
			getUsers = GetUsersUseCase(repo),
			getUserDetail = GetUserDetailUseCase(repo),
			getUserRepos = GetUserReposUseCase(repo),
			getFollowers = GetFollowersUseCase(repo),
			getFollowing = GetFollowingUseCase(repo),
			searchUsers = SearchUsersUseCase(repo),
			observeFavorites = ObserveFavoritesUseCase(repo),
			isFavorite = IsFavoriteUseCase(repo),
			addFavorite = AddFavoriteUseCase(repo),
			removeFavorite = RemoveFavoriteUseCase(repo),
			getRepoContents = GetRepoContentsUseCase(repo),
			getRepoDetail = GetRepoDetailUseCase(repo),
			getRepoBranches = GetRepoBranchesUseCase(repo)
		)
	}
}


