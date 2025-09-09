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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.train.github.domain.model.GithubUser
import com.train.github.presentation.favorites.FavoritesState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
	state: StateFlow<FavoritesState>,
	onBack: () -> Unit,
	onUserClick: (GithubUser) -> Unit
) {
	val uiState by state.collectAsState()
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Favorites", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
		}
	) { innerPadding ->
		Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)) {
			Spacer(Modifier.height(12.dp))
			if (uiState.users.isEmpty()) {
				val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.train.github.R.raw.empty_ghost))
				Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
					LottieAnimation(composition = composition, iterations = Int.MAX_VALUE, modifier = Modifier.size(200.dp))
					Spacer(Modifier.height(12.dp))
					Text("No favorites yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
				}
			} else {
				LazyColumn(Modifier.fillMaxSize()) {
					items(uiState.users, key = { it.id }) { user ->
						Row(Modifier.fillMaxWidth().clickable { onUserClick(user) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
							Image(
								painter = rememberAsyncImagePainter(user.avatarUrl),
								contentDescription = null,
								modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, Color(0x14000000), CircleShape),
								contentScale = ContentScale.Crop
							)
							Spacer(Modifier.width(12.dp))
							Column(Modifier.weight(1f)) {
								Text(user.login, style = MaterialTheme.typography.titleMedium)
								user.name?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
							}
						}
						Divider()
					}
				}
			}
		}
	}
}


