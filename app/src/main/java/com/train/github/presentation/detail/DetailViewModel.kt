package com.train.github.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.train.github.domain.model.GithubUser
import com.train.github.domain.model.GithubRepo
import com.train.github.domain.usecase.AddFavoriteUseCase
import com.train.github.domain.usecase.GetUserDetailUseCase
import com.train.github.domain.usecase.GetUserReposUseCase
import com.train.github.domain.usecase.GetFollowersUseCase
import com.train.github.domain.usecase.GetFollowingUseCase
import com.train.github.domain.usecase.IsFavoriteUseCase
import com.train.github.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailViewModel(
	private val getUserDetail: GetUserDetailUseCase,
	private val getUserRepos: GetUserReposUseCase,
	private val isFavorite: IsFavoriteUseCase,
	private val addFavorite: AddFavoriteUseCase,
	private val removeFavorite: RemoveFavoriteUseCase,
	private val getFollowers: GetFollowersUseCase,
	private val getFollowing: GetFollowingUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(DetailState())
	val state: StateFlow<DetailState> = _state

	fun load(username: String, id: Int) {
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoading = true, error = null, username = username)
			launch {
				isFavorite(id).collectLatest { fav ->
					_state.value = _state.value.copy(isFavorite = fav)
				}
			}
			try {
				val user = getUserDetail(username)
				val repos = getUserRepos(username)
				_state.value = _state.value.copy(isLoading = false, user = user, repos = repos)
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoading = false, error = t.message)
			}
		}
	}

	fun loadFollowers() {
		val uname = _state.value.username ?: return
		viewModelScope.launch {
			try {
				val list = getFollowers(uname)
				_state.value = _state.value.copy(followers = list)
			} catch (_: Throwable) { }
		}
	}

	fun loadFollowing() {
		val uname = _state.value.username ?: return
		viewModelScope.launch {
			try {
				val list = getFollowing(uname)
				_state.value = _state.value.copy(following = list)
			} catch (_: Throwable) { }
		}
	}

	fun toggleFavorite() {
		val current = _state.value.user ?: return
		viewModelScope.launch {
			if (_state.value.isFavorite) removeFavorite(current) else addFavorite(current)
		}
	}
}

data class DetailState(
	val isLoading: Boolean = false,
	val user: GithubUser? = null,
	val repos: List<GithubRepo> = emptyList(),
	val followers: List<GithubUser> = emptyList(),
	val following: List<GithubUser> = emptyList(),
	val isFavorite: Boolean = false,
	val username: String? = null,
	val error: String? = null
)


