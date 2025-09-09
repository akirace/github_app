package com.train.github.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode {
	System,
	Dynamic,
	Light,
	Dark,
	Teal,
	Orange
}

data class AppThemeController(
	val mode: AppThemeMode,
	val setMode: (AppThemeMode) -> Unit
)

val LocalAppThemeController = staticCompositionLocalOf<AppThemeController> {
	AppThemeController(AppThemeMode.System) {}
}

private val TealColorSchemeLight = lightColorScheme(
	primary = ColorTealPrimary,
	secondary = ColorTealSecondary,
	tertiary = ColorTealTertiary
)

private val TealColorSchemeDark = darkColorScheme(
	primary = ColorTealPrimary,
	secondary = ColorTealSecondary,
	tertiary = ColorTealTertiary
)

private val OrangeColorSchemeLight = lightColorScheme(
	primary = ColorOrangePrimary,
	secondary = ColorOrangeSecondary,
	tertiary = ColorOrangeTertiary
)

private val OrangeColorSchemeDark = darkColorScheme(
	primary = ColorOrangePrimary,
	secondary = ColorOrangeSecondary,
	tertiary = ColorOrangeTertiary
)

@Composable
fun ProvideAppTheme(content: @Composable () -> Unit) {
	val (mode, setMode) = remember { mutableStateOf(AppThemeMode.System) }
	val controller = remember(mode) { AppThemeController(mode, setMode) }
	val context = LocalContext.current

	val colorScheme = when (mode) {
		AppThemeMode.System -> {
			val isDark = androidx.compose.foundation.isSystemInDarkTheme()
			if (isDark) DarkColorScheme else LightColorScheme
		}
		AppThemeMode.Dynamic -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				val isDark = androidx.compose.foundation.isSystemInDarkTheme()
				if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
			} else {
				LightColorScheme
			}
		}
		AppThemeMode.Light -> LightColorScheme
		AppThemeMode.Dark -> DarkColorScheme
		AppThemeMode.Teal -> if (androidx.compose.foundation.isSystemInDarkTheme()) TealColorSchemeDark else TealColorSchemeLight
		AppThemeMode.Orange -> if (androidx.compose.foundation.isSystemInDarkTheme()) OrangeColorSchemeDark else OrangeColorSchemeLight
	}

	CompositionLocalProvider(LocalAppThemeController provides controller) {
		MaterialTheme(colorScheme = colorScheme, typography = Typography) {
			content()
		}
	}
}



