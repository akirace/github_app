package com.train.testcursor.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.train.testcursor.domain.model.GithubUser
import com.train.testcursor.presentation.home.HomeState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	state: StateFlow<HomeState>,
	onUserClick: (GithubUser) -> Unit,
	onFavoritesClick: () -> Unit,
	onLoadMore: () -> Unit = {},
	onQueryChanged: (String) -> Unit = {},
	onOpenDrawer: () -> Unit = {}
) {
	val uiState by state.collectAsState()
	val (query, setQuery) = remember { mutableStateOf("") }
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "GitHub Users",
						style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
					)
				},
				navigationIcon = {
					IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
				},
				actions = {
					IconButton(onClick = onFavoritesClick) {
						Icon(
							imageVector = Icons.Default.Favorite,
							contentDescription = "Favorites",
							tint = MaterialTheme.colorScheme.primary
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
					navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
					actionIconContentColor = MaterialTheme.colorScheme.onSurface
				)
			)
		}
	) { innerPadding ->
		Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)) {
			OutlinedTextField(
				value = query,
				onValueChange = {
					setQuery(it)
					onQueryChanged(it)
				},
				singleLine = true,
				placeholder = { Text("Search users") },
				leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
				trailingIcon = {
					if (query.isNotBlank()) {
						IconButton(onClick = { setQuery(""); onQueryChanged("") }) {
							Icon(Icons.Default.Close, contentDescription = "Clear")
						}
					}
				},
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(12.dp),
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
				keyboardActions = KeyboardActions(onSearch = { onQueryChanged(query) }),
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = MaterialTheme.colorScheme.surface,
					focusedContainerColor = MaterialTheme.colorScheme.surface,
					unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
					focusedIndicatorColor = MaterialTheme.colorScheme.primary
				)
			)
			Spacer(Modifier.height(8.dp))
			when {
				uiState.isLoading -> {
					Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
						CircularProgressIndicator()
					}
				}
				uiState.error != null -> {
					Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
						Text("Error: ${uiState.error}", modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error)
					}
				}
				else -> {
					LazyColumn(Modifier.fillMaxSize()) {
						items(uiState.users, key = { it.id }) { user ->
							UserCard(user = user, onClick = { onUserClick(user) })
							Spacer(Modifier.height(10.dp))
						}
						item {
							when {
								uiState.isLoadingMore -> Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
								!uiState.endReached -> LaunchedLoadMore(onLoadMore)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun UserCard(user: GithubUser, onClick: () -> Unit) {
	ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
		Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
			Image(
				painter = rememberAsyncImagePainter(user.avatarUrl),
				contentDescription = null,
				modifier = Modifier.size(56.dp).clip(CircleShape).border(1.dp, Color(0x14000000), CircleShape),
				contentScale = ContentScale.Crop
			)
			Spacer(Modifier.width(12.dp))
			Column(Modifier.weight(1f)) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Transparent)
					Spacer(Modifier.width(6.dp))
					Text(user.login, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
				}
				user.name?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
			}
			Spacer(Modifier.width(8.dp))
			Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
		}
	}
}

@Composable
private fun LaunchedLoadMore(onLoadMore: () -> Unit) {
	androidx.compose.runtime.LaunchedEffect(Unit) { onLoadMore() }
}


