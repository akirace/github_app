package com.train.github.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.train.github.domain.model.GithubRepo
import com.train.github.presentation.detail.DetailState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
	state: StateFlow<DetailState>,
	onBack: () -> Unit,
	onToggleFavorite: () -> Unit,
	onLoadFollowers: () -> Unit,
	onLoadFollowing: () -> Unit,
	onRepoClick: (String) -> Unit
) {
	val uiState by state.collectAsState()
	var sheetType: SheetType? by remember { mutableStateOf(null) }
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("User Detail", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
					navigationIconContentColor = MaterialTheme.colorScheme.onSurface
				)
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				text = { Text(if (uiState.isFavorite) "Remove Favorite" else "Add Favorite") },
				icon = { Icon(if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null) },
				onClick = onToggleFavorite
			)
		}
	) { innerPadding ->
		when {
			uiState.isLoading -> {
				Column(Modifier.fillMaxSize().padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
					CircularProgressIndicator()
				}
			}
			uiState.error != null -> {
				Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(16.dp)) {
					Text("Error: ${uiState.error}", modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error)
				}
			}
			uiState.user != null -> {
				LazyColumn(Modifier.fillMaxSize().padding(innerPadding)) {
					item {
						Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
							Image(
								painter = rememberAsyncImagePainter(uiState.user!!.avatarUrl),
								contentDescription = null,
								modifier = Modifier.size(96.dp).clip(CircleShape),
								contentScale = ContentScale.Crop
							)
							Spacer(Modifier.height(8.dp))
							Text(uiState.user!!.login, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
							uiState.user!!.name?.let { Text(it, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
							Spacer(Modifier.height(8.dp))
							Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
								AssistChip(onClick = { sheetType = SheetType.Followers }, label = { Text("${uiState.user!!.followers ?: 0} followers") }, colors = AssistChipDefaults.assistChipColors())
								AssistChip(onClick = { sheetType = SheetType.Following }, label = { Text("${uiState.user!!.following ?: 0} following") })
							}
						}
					}
					item { Spacer(Modifier.height(8.dp)) }
					item {
						Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
							Text("Repositories", style = MaterialTheme.typography.titleMedium)
							Spacer(Modifier.width(8.dp))
							AssistChip(onClick = {}, label = { Text("${uiState.user!!.publicRepos ?: uiState.repos.size}") })
						}
					}
					items(uiState.repos, key = { it.id }) { repo ->
						RepoCard(repo) { onRepoClick(repo.htmlUrl) }
					}
					item { Spacer(Modifier.height(80.dp)) }
				}
			}
		}
	}
	if (sheetType != null) {
		LaunchedEffect(sheetType) {
			if (sheetType == SheetType.Followers) onLoadFollowers() else onLoadFollowing()
		}
		ModalBottomSheet(onDismissRequest = { sheetType = null }, sheetState = sheetState) {
			if (sheetType == SheetType.Followers) {
				FollowersFollowingList(title = "Followers", users = uiState.followers)
			} else if (sheetType == SheetType.Following) {
				FollowersFollowingList(title = "Following", users = uiState.following)
			}
			Spacer(Modifier.height(8.dp))
			TextButton(onClick = { sheetType = null }) { Text("Close") }
			Spacer(Modifier.height(8.dp))
		}
	}
}

enum class SheetType { Followers, Following }

@Composable
private fun RepoCard(repo: GithubRepo, onClick: () -> Unit) {
	ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() }) {
		Column(Modifier.fillMaxWidth().padding(12.dp)) {
			Text(repo.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
			repo.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
			Spacer(Modifier.height(6.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				if (repo.language != null) Text(repo.language, style = MaterialTheme.typography.bodySmall)
				if ((repo.stars ?: 0) > 0) Text("★ ${repo.stars}", style = MaterialTheme.typography.bodySmall)
			}
		}
	}
}

@Composable
private fun FollowersFollowingList(title: String, users: List<com.train.github.domain.model.GithubUser>) {
	Column(Modifier.fillMaxWidth().padding(16.dp)) {
		Text(title, style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(8.dp))
		if (users.isEmpty()) {
			Text("No data", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
		} else {
			LazyColumn(Modifier.fillMaxWidth()) {
				items(users, key = { it.id }) { u ->
					Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
						Image(
							painter = rememberAsyncImagePainter(u.avatarUrl),
							contentDescription = null,
							modifier = Modifier.size(40.dp).clip(CircleShape),
							contentScale = ContentScale.Crop
						)
						Spacer(Modifier.width(12.dp))
						Column(Modifier.weight(1f)) {
							Text(u.login, style = MaterialTheme.typography.titleSmall)
							u.name?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
						}
					}
				}
			}
		}
	}
}


