package com.train.github.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.train.github.di.ServiceLocator
import com.train.github.presentation.screens.DetailScreen
import com.train.github.presentation.screens.FavoritesScreen
import com.train.github.presentation.screens.HomeScreen
import com.train.github.presentation.screens.SettingsScreen
import com.train.github.presentation.detail.DetailViewModel
import com.train.github.presentation.favorites.FavoritesViewModel
import com.train.github.presentation.home.HomeViewModel
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import android.net.Uri

import com.train.github.presentation.repo.RepoDetailViewModel
import com.train.github.presentation.screens.RepoDetailScreen

object Routes {
	const val HOME = "home"
	const val DETAIL = "detail/{username}/{id}"
	const val REPO_DETAIL = "repo/{owner}/{repo}"
	const val FAVORITES = "favorites"
	const val SETTINGS = "settings"
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
	val navController = rememberNavController()
	val context = androidx.compose.ui.platform.LocalContext.current
	val useCases = remember { ServiceLocator.provideUseCases(context) }
	val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet {
				Spacer(Modifier.height(12.dp))
				NavigationDrawerItem(
					icon = { Icon(Icons.Default.Home, contentDescription = null) },
					label = { Text("Home") },
					selected = currentRoute == Routes.HOME,
					onClick = {
						navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
						scope.launch { drawerState.close() }
					}
				)
				NavigationDrawerItem(
					icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
					label = { Text("Favorites") },
					selected = currentRoute == Routes.FAVORITES,
					onClick = {
						navController.navigate(Routes.FAVORITES)
						scope.launch { drawerState.close() }
					}
				)
				NavigationDrawerItem(
					icon = { Icon(Icons.Default.Settings, contentDescription = null) },
					label = { Text("Settings") },
					selected = currentRoute == Routes.SETTINGS,
					onClick = {
						navController.navigate(Routes.SETTINGS)
						scope.launch { drawerState.close() }
					}
				)
			}
		},
		modifier = modifier
	) {
		NavHost(navController = navController, startDestination = Routes.HOME, modifier = Modifier.fillMaxSize()) {
			composable(Routes.HOME) {
				val homeFactory = object : ViewModelProvider.Factory {
					override fun <T : ViewModel> create(modelClass: Class<T>): T {
						@Suppress("UNCHECKED_CAST")
						return HomeViewModel(useCases.getUsers, useCases.searchUsers) as T
					}
				}
				val vm = viewModel<HomeViewModel>(factory = homeFactory)
				HomeScreen(
					state = vm.state,
					onUserClick = { user ->
						scope.launch { drawerState.close() }
						navController.navigate("detail/${user.login}/${user.id}")
					},
					onFavoritesClick = {
						scope.launch { drawerState.close() }
						navController.navigate(Routes.FAVORITES)
					},
					onLoadMore = { vm.loadMore() },
					onQueryChanged = { q -> vm.search(q) },
					onOpenDrawer = { scope.launch { drawerState.open() } }
				)
			}
			composable(
				Routes.DETAIL,
				arguments = listOf(
					navArgument("username") { type = NavType.StringType },
					navArgument("id") { type = NavType.IntType }
				)
			) { backStackEntry ->
				val username = backStackEntry.arguments?.getString("username") ?: return@composable
				val id = backStackEntry.arguments?.getInt("id") ?: return@composable
				val detailFactory = object : ViewModelProvider.Factory {
					override fun <T : ViewModel> create(modelClass: Class<T>): T {
						@Suppress("UNCHECKED_CAST")
						return DetailViewModel(
							useCases.getUserDetail,
							useCases.getUserRepos,
							useCases.isFavorite,
							useCases.addFavorite,
							useCases.removeFavorite,
							useCases.getFollowers,
							useCases.getFollowing
						) as T
					}
				}
				val vm = viewModel<DetailViewModel>(factory = detailFactory)
				LaunchedEffect(username, id) {
					vm.load(username, id)
				}
				DetailScreen(
					state = vm.state,
					onBack = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
					onToggleFavorite = { vm.toggleFavorite() },
					onLoadFollowers = { vm.loadFollowers() },
					onLoadFollowing = { vm.loadFollowing() },
					onRepoClick = { url ->
						val parts = url.removePrefix("https://github.com/").split("/")
						if (parts.size >= 2) {
							val owner = parts[0]
							val repo = parts[1]
							navController.navigate("repo/$owner/$repo?user=${Uri.encode(username)}&uid=$id")
						}
					}
				)
			}
			composable(
				Routes.REPO_DETAIL + "?path={path}&branch={branch}&user={user}&uid={uid}",
				arguments = listOf(
					navArgument("owner") { type = NavType.StringType },
					navArgument("repo") { type = NavType.StringType },
					navArgument("path") { type = NavType.StringType; defaultValue = "" },
					navArgument("branch") { type = NavType.StringType; defaultValue = "" },
					navArgument("user") { type = NavType.StringType; defaultValue = "" },
					navArgument("uid") { type = NavType.IntType; defaultValue = -1 }
				)
			) { backStackEntry ->
				val owner = backStackEntry.arguments?.getString("owner") ?: return@composable
				val repo = backStackEntry.arguments?.getString("repo") ?: return@composable
				val path = backStackEntry.arguments?.getString("path") ?: ""
				val branchArg = backStackEntry.arguments?.getString("branch") ?: ""
				val userArg = backStackEntry.arguments?.getString("user") ?: ""
				val uidArg = backStackEntry.arguments?.getInt("uid") ?: -1
				val vm = viewModel<RepoDetailViewModel>(factory = object : ViewModelProvider.Factory {
					override fun <T : ViewModel> create(modelClass: Class<T>): T {
						@Suppress("UNCHECKED_CAST")
						return RepoDetailViewModel(useCases.getRepoContents, useCases.getRepoDetail, useCases.getRepoBranches) as T
					}
				})
				LaunchedEffect(owner, repo, path, branchArg) { vm.load(owner, repo, path, branch = branchArg.ifBlank { null }) }
				RepoDetailScreen(
					state = vm.state,
					onBack = {
						if (userArg.isNotBlank() && uidArg > 0) {
							navController.navigate("detail/${userArg}/${uidArg}")
						} else {
							navController.popBackStack()
						}
					},
					onNavigatePath = { newPath ->
						val b = vm.state.value.selectedBranch.orEmpty()
						navController.navigate("repo/$owner/$repo?path=${Uri.encode(newPath)}&branch=${Uri.encode(b)}&user=${Uri.encode(userArg)}&uid=$uidArg")
					},
					onSelectBranch = { branch ->
						vm.selectBranch(branch)
						navController.navigate("repo/$owner/$repo?path=${Uri.encode(path)}&branch=${Uri.encode(branch)}&user=${Uri.encode(userArg)}&uid=$uidArg")
					}
				)
			}
			composable(Routes.FAVORITES) {
				val favFactory = object : ViewModelProvider.Factory {
					override fun <T : ViewModel> create(modelClass: Class<T>): T {
						@Suppress("UNCHECKED_CAST")
						return FavoritesViewModel(useCases.observeFavorites) as T
					}
				}
				val vm = viewModel<FavoritesViewModel>(factory = favFactory)
				FavoritesScreen(state = vm.state, onBack = { navController.popBackStack() }, onUserClick = { user -> navController.navigate("detail/${user.login}/${user.id}") })
			}
			composable(Routes.SETTINGS) {
				SettingsScreen(onOpenDrawer = { /* no-op, drawer handled by parent */ })
			}
		}
	}
}


