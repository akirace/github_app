package com.train.testcursor.di

import android.content.Context
import androidx.room.Room
import com.train.testcursor.data.local.AppDatabase
import com.train.testcursor.data.remote.NetworkModule
import com.train.testcursor.data.repository.GithubRepositoryImpl
import com.train.testcursor.domain.repository.GithubRepository
import com.train.testcursor.domain.usecase.AddFavoriteUseCase
import com.train.testcursor.domain.usecase.GetUserDetailUseCase
import com.train.testcursor.domain.usecase.GetUsersUseCase
import com.train.testcursor.domain.usecase.GetUserReposUseCase
import com.train.testcursor.domain.usecase.GetFollowersUseCase
import com.train.testcursor.domain.usecase.GetFollowingUseCase
import com.train.testcursor.domain.usecase.SearchUsersUseCase
import com.train.testcursor.domain.usecase.IsFavoriteUseCase
import com.train.testcursor.domain.usecase.ObserveFavoritesUseCase
import com.train.testcursor.domain.usecase.RemoveFavoriteUseCase
import com.train.testcursor.domain.usecase.GetRepoContentsUseCase
import com.train.testcursor.domain.usecase.GetRepoDetailUseCase
import com.train.testcursor.domain.usecase.GetRepoBranchesUseCase

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


