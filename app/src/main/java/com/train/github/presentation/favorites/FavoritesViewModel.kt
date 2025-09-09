package com.train.github.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.train.github.domain.model.GithubUser
import com.train.github.domain.usecase.ObserveFavoritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
	private val observeFavorites: ObserveFavoritesUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(FavoritesState())
	val state: StateFlow<FavoritesState> = _state

	init {
		viewModelScope.launch {
			observeFavorites().collectLatest { list ->
				_state.value = _state.value.copy(users = list)
			}
		}
	}
}

data class FavoritesState(
	val users: List<GithubUser> = emptyList()
)



