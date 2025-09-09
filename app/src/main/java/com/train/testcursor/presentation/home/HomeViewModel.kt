package com.train.testcursor.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.train.testcursor.domain.model.GithubUser
import com.train.testcursor.domain.usecase.GetUsersUseCase
import com.train.testcursor.domain.usecase.SearchUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
	private val getUsers: GetUsersUseCase,
	private val searchUsers: SearchUsersUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(HomeState())
	val state: StateFlow<HomeState> = _state

	private var nextSince: Int? = null
	private var isLoadingMoreInternal: Boolean = false

	init { load() }

	fun load() {
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoading = true, error = null, isSearching = false)
			try {
				val users = getUsers(since = null)
				nextSince = users.lastOrNull()?.id
				_state.value = _state.value.copy(isLoading = false, users = users, endReached = users.isEmpty())
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoading = false, error = t.message)
			}
		}
	}

	fun loadMore() {
		if (isLoadingMoreInternal || _state.value.endReached || _state.value.isSearching) return
		isLoadingMoreInternal = true
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoadingMore = true)
			try {
				val more = getUsers(since = nextSince)
				val combined = _state.value.users + more
				nextSince = more.lastOrNull()?.id ?: nextSince
				_state.value = _state.value.copy(
					users = combined,
					isLoadingMore = false,
					endReached = more.isEmpty()
				)
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoadingMore = false, error = t.message)
			} finally {
				isLoadingMoreInternal = false
			}
		}
	}

	fun search(query: String) {
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoading = true, error = null, isSearching = query.isNotBlank(), query = query)
			try {
				val results = if (query.isBlank()) getUsers(since = null) else searchUsers(query)
				nextSince = results.lastOrNull()?.id
				_state.value = _state.value.copy(isLoading = false, users = results, endReached = results.isEmpty())
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoading = false, error = t.message)
			}
		}
	}
}

data class HomeState(
	val isLoading: Boolean = false,
	val users: List<GithubUser> = emptyList(),
	val isLoadingMore: Boolean = false,
	val endReached: Boolean = false,
	val isSearching: Boolean = false,
	val query: String = "",
	val error: String? = null
)


