package com.train.testcursor.presentation.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.train.testcursor.domain.model.ContentType
import com.train.testcursor.domain.model.GithubContent
import com.train.testcursor.domain.model.GithubRepoDetail
import com.train.testcursor.domain.usecase.GetRepoContentsUseCase
import com.train.testcursor.domain.usecase.GetRepoDetailUseCase
import com.train.testcursor.domain.usecase.GetRepoBranchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoDetailViewModel(
	private val getContents: GetRepoContentsUseCase,
	private val getRepoDetail: GetRepoDetailUseCase,
	private val getRepoBranches: GetRepoBranchesUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(RepoDetailState())
	val state: StateFlow<RepoDetailState> = _state

	fun load(owner: String, repo: String, path: String = "", branch: String? = null) {
		viewModelScope.launch {
			_state.value = _state.value.copy(isLoading = true, error = null, owner = owner, repository = repo, path = path, selectedBranch = branch)
			try {
				val detail = getRepoDetail(owner, repo)
				val branches = getRepoBranches(owner, repo)
				val effectiveBranch = branch ?: detail.defaultBranch
				val items = getContents(owner, repo, path, ref = effectiveBranch)
				_state.value = _state.value.copy(
					isLoading = false,
					detail = detail,
					branches = branches.map { it.name },
					selectedBranch = effectiveBranch,
					items = items
				)
			} catch (t: Throwable) {
				_state.value = _state.value.copy(isLoading = false, error = t.message)
			}
		}
	}

	fun selectBranch(branch: String) {
		val owner = _state.value.owner ?: return
		val repo = _state.value.repository ?: return
		val path = _state.value.path
		load(owner, repo, path, branch)
	}
}


data class RepoDetailState(
	val isLoading: Boolean = false,
	val owner: String? = null,
	val repository: String? = null,
	val path: String = "",
	val detail: GithubRepoDetail? = null,
	val branches: List<String> = emptyList(),
	val selectedBranch: String? = null,
	val items: List<GithubContent> = emptyList(),
	val error: String? = null
)


