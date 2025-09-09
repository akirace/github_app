package com.train.testcursor.domain.usecase

import com.train.testcursor.domain.model.GithubContent
import com.train.testcursor.domain.model.GithubRepoDetail
import com.train.testcursor.domain.repository.GithubRepository

class GetRepoContentsUseCase(private val repo: GithubRepository) {
	suspend operator fun invoke(owner: String, repository: String, path: String = "", ref: String? = null): List<GithubContent> =
		repo.getRepoContents(owner, repository, path, ref)
}

class GetRepoDetailUseCase(private val repo: GithubRepository) {
	suspend operator fun invoke(owner: String, repository: String): GithubRepoDetail =
		repo.getRepoDetail(owner, repository)
}

class GetRepoBranchesUseCase(private val repo: GithubRepository) {
	suspend operator fun invoke(owner: String, repository: String) = repo.getRepoBranches(owner, repository)
}


