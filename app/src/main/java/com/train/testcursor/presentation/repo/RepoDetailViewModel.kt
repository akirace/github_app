package com.train.testcursor.presentation.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.train.testcursor.domain.model.ContentType
import com.train.testcursor.domain.model.GithubContent
import com.train.testcursor.domain.model.GithubRepoDetail
import com.train.testcursor.domain.usecase.GetRepoContentsUseCase
import com.train.testcursor.domain.usecase.GetRepoDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoDetailViewModel(
	private val getContents: GetRepoContentsUseCase,
	private val getRepoDetail: GetRepoDetailUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(RepoDetailState())
	val state: StateFlow<RepoDetailState> = _state

	fun load(owner: String, repo: String, path: String = "") {
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoading = true, error = null, owner = owner, repository = repo, path = path)
			try {
				val detail = getRepoDetail(owner, repo)
				val items = getContents(owner, repo, path)
				_state.value = _state.value.copy(isLoading = false, detail = detail, items = items)
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoading = false, error = t.message)
			}
		}
	}
}

data class RepoDetailState(
	val isLoading: Boolean = false,
	val owner: String? = null,
	val repository: String? = null,
	val path: String = "",
	val detail: GithubRepoDetail? = null,
	val items: List<GithubContent> = emptyList(),
	val error: String? = null
)


