package com.train.github.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.train.github.ui.theme.AppThemeMode
import com.train.github.ui.theme.LocalAppThemeController
import com.train.github.ui.theme.ColorOrangePrimary
import com.train.github.ui.theme.ColorOrangeSecondary
import com.train.github.ui.theme.ColorOrangeTertiary
import com.train.github.ui.theme.ColorTealPrimary
import com.train.github.ui.theme.ColorTealSecondary
import com.train.github.ui.theme.ColorTealTertiary
import com.train.github.ui.theme.Pink40
import com.train.github.ui.theme.Pink80
import com.train.github.ui.theme.Purple40
import com.train.github.ui.theme.Purple80
import com.train.github.ui.theme.PurpleGrey40
import com.train.github.ui.theme.PurpleGrey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onOpenDrawer: () -> Unit) {
	val controller = LocalAppThemeController.current
	var selected by remember { mutableStateOf(controller.mode) }
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
				navigationIcon = {
					IconButton(onClick = onOpenDrawer) {
						Icon(Icons.Default.Menu, contentDescription = "Menu")
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface
				)
			)
		}
	) { innerPadding ->
		Column(
			Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text("Theme", style = MaterialTheme.typography.titleMedium)
			ThemeCard(
				label = "System",
				desc = "Follows your device setting",
				swatches = listOf(Purple40, PurpleGrey40, Pink40),
				selected = selected == AppThemeMode.System,
				onClick = { selected = AppThemeMode.System; controller.setMode(AppThemeMode.System) }
			)
			ThemeCard(
				label = "Dynamic",
				desc = "Material You (Android 12+)",
				swatches = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary),
				selected = selected == AppThemeMode.Dynamic,
				onClick = { selected = AppThemeMode.Dynamic; controller.setMode(AppThemeMode.Dynamic) }
			)
			ThemeCard(
				label = "Light",
				desc = "Bright classic palette",
				swatches = listOf(Purple40, PurpleGrey40, Pink40),
				selected = selected == AppThemeMode.Light,
				onClick = { selected = AppThemeMode.Light; controller.setMode(AppThemeMode.Light) }
			)
			ThemeCard(
				label = "Dark",
				desc = "Dimmed for low light",
				swatches = listOf(Purple80, PurpleGrey80, Pink80),
				selected = selected == AppThemeMode.Dark,
				onClick = { selected = AppThemeMode.Dark; controller.setMode(AppThemeMode.Dark) }
			)
			ThemeCard(
				label = "Teal",
				desc = "Cool and calm",
				swatches = listOf(ColorTealPrimary, ColorTealSecondary, ColorTealTertiary),
				selected = selected == AppThemeMode.Teal,
				onClick = { selected = AppThemeMode.Teal; controller.setMode(AppThemeMode.Teal) }
			)
			ThemeCard(
				label = "Orange",
				desc = "Warm and lively",
				swatches = listOf(ColorOrangePrimary, ColorOrangeSecondary, ColorOrangeTertiary),
				selected = selected == AppThemeMode.Orange,
				onClick = { selected = AppThemeMode.Orange; controller.setMode(AppThemeMode.Orange) }
			)
		}
	}
}

@Composable
private fun ThemeCard(
	label: String,
	desc: String,
	swatches: List<Color>,
	selected: Boolean,
	onClick: () -> Unit
) {
	ElevatedCard(
		modifier = Modifier.fillMaxWidth().clickable { onClick() },
		shape = RoundedCornerShape(12.dp)
	) {
		Row(
			Modifier.fillMaxWidth().padding(14.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
					for (c in swatches.take(3)) {
						Spacer(
							modifier = Modifier.size(18.dp).clip(CircleShape).background(c)
						)
					}
				}
				Column {
					Text(label, style = MaterialTheme.typography.titleMedium)
					Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
				}
			}
			if (selected) {
				Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
			}
		}
	}
}


