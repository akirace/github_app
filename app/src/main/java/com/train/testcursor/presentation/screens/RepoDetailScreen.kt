package com.train.testcursor.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.train.testcursor.domain.model.ContentType
import com.train.testcursor.presentation.repo.RepoDetailState
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import com.train.testcursor.data.remote.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.nio.charset.Charset
import org.json.JSONObject
import androidx.compose.material3.AlertDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.train.testcursor.BuildConfig
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import com.train.testcursor.domain.model.GithubContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
	state: StateFlow<RepoDetailState>,
	onBack: () -> Unit,
	onNavigatePath: (String) -> Unit,
	onSelectBranch: (String) -> Unit
) {
	val uiState by state.collectAsState()
	var selectedPath = remember { mutableStateOf<String?>(null) }
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val fileText = remember { mutableStateOf<String?>(null) }
	val isLoadingFile = remember { mutableStateOf(false) }
	val isAskingAi = remember { mutableStateOf(false) }
	val aiAnswer = remember { mutableStateOf<String?>(null) }
	val showAiDialog = remember { mutableStateOf(false) }
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(text = uiState.detail?.fullName ?: uiState.repository ?: "Repository", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
				navigationIcon = {
					IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
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
			else -> {
				LazyColumn(Modifier.fillMaxSize().padding(innerPadding)) {
					item {
						HeaderSection(state = uiState, onBack = onBack, onSelectBranch = onSelectBranch)
					}
					item { Spacer(Modifier.height(8.dp)) }
					item {
						FilesHeader(path = uiState.path, onNavigatePath = onNavigatePath)
					}
					items(uiState.items, key = { it.path }) { item ->
						FileRow(item = item, onClick = { clicked ->
							if (clicked.type == ContentType.Dir) {
								onNavigatePath(clicked.path)
							} else if (clicked.path.isNotEmpty()) {
								selectedPath.value = clicked.path
								fileText.value = null
								isLoadingFile.value = true
							}
						})
					}
					item { Spacer(Modifier.height(12.dp)) }
					item {
						FooterSection(defaultBranch = uiState.detail?.defaultBranch)
					}
					item { Spacer(Modifier.height(24.dp)) }
				}
			}
		}
		if (selectedPath.value != null && uiState.owner != null && uiState.repository != null) {
			ModalBottomSheet(onDismissRequest = { selectedPath.value = null }, sheetState = sheetState) {
				val ref = uiState.selectedBranch ?: "HEAD"
				val rawUrl = "https://raw.githubusercontent.com/${uiState.owner}/${uiState.repository}/${ref}/${selectedPath.value}"
				LaunchedEffect(rawUrl) {
					if (selectedPath.value != null) {
						withContext(Dispatchers.IO) {
							try {
								val client = NetworkModule.apiService // placeholder to keep reference; we'll create OkHttp directly
							} catch (_: Throwable) { }
						}
					}
				}
				LaunchedEffect(selectedPath.value) {
					if (selectedPath.value != null) {
						withContext(Dispatchers.IO) {
							try {
								val client = OkHttpClient()
								val request = Request.Builder().url(rawUrl).build()
								client.newCall(request).execute().use { resp ->
									val body = resp.body?.bytes()
									val text = body?.toString(Charset.forName("UTF-8")) ?: ""
									withContext(Dispatchers.Main) {
										fileText.value = text
										isLoadingFile.value = false
									}
								}
							} catch (_: Throwable) {
								withContext(Dispatchers.Main) {
									fileText.value = "Failed to load file."
									isLoadingFile.value = false
								}
							}
						}
					}
				}
				Column(Modifier.fillMaxWidth().padding(12.dp)) {
					Text(selectedPath.value ?: "", style = MaterialTheme.typography.titleSmall)
					Spacer(Modifier.height(8.dp))
					Box(Modifier.fillMaxWidth().heightIn(min = 240.dp, max = 520.dp)) {
						if (isLoadingFile.value) {
							Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
						} else {
							OutlinedTextField(
								value = fileText.value ?: "",
								onValueChange = {},
								modifier = Modifier.fillMaxSize(),
								readOnly = true,
								textStyle = MaterialTheme.typography.bodySmall
							)
						}
					}
					Spacer(Modifier.height(8.dp))
					Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
						TextButton(
							enabled = !isAskingAi.value && !isLoadingFile.value && !fileText.value.isNullOrEmpty(),
							onClick = {
								val content = fileText.value ?: ""
								isAskingAi.value = true
								aiAnswer.value = null
								// Call Gemini API
								val apiKey = BuildConfig.GEMINI_API_KEY
								val endpoint =
									"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
								val promptText =
									"Provide a concise explanation and insights for the following code. Point out potential issues and improvements.\n\nFile: ${selectedPath.value}\n\n```\n${
										content.take(60000)
									}\n```"
								val json = """
								{
								  "contents": [
								    {
								      "parts": [
								        {"text": ${JSONObject.quote(promptText)}}
								      ]
								    }
								  ]
								}
								""".trimIndent()
								val mediaType = "application/json; charset=utf-8".toMediaType()
								val body = json.toRequestBody(mediaType)
								val req = Request.Builder().url(endpoint).post(body).build()
								val client = OkHttpClient()
								// Launch network call
								GlobalScope.launch(Dispatchers.IO) {
									try {
										client.newCall(req).execute().use { resp ->
											val respBody = resp.body?.string().orEmpty()
											val obj = JSONObject(respBody)
											val candidates = obj.optJSONArray("candidates")
											var answer = ""
											if (candidates != null && candidates.length() > 0) {
												val contentObj = candidates.getJSONObject(0)
													.optJSONObject("content")
												val parts = contentObj?.optJSONArray("parts")
												if (parts != null && parts.length() > 0) {
													answer =
														parts.getJSONObject(0).optString("text", "")
												}
											}
											withContext(Dispatchers.Main) {
												aiAnswer.value =
													answer.ifBlank { "No answer returned." }
												isAskingAi.value = false
												showAiDialog.value = true
											}
										}
									} catch (_: Throwable) {
										withContext(Dispatchers.Main) {
											aiAnswer.value = "Failed to get AI answer."
											isAskingAi.value = false
											showAiDialog.value = true
										}
									}
								}
							}
						) {
							Text(if (isAskingAi.value) "Asking..." else "ASK AI")
						}
						TextButton(onClick = { selectedPath.value = null }) { Text("Close") }
					}
				}
			}
		}
		if (showAiDialog.value) {
			AlertDialog(
				onDismissRequest = { showAiDialog.value = false },
				confirmButton = {
					TextButton(onClick = { showAiDialog.value = false }) { Text("Close") }
				},
				title = { Text("AI Answer", style = MaterialTheme.typography.titleMedium) },
				text = {
					Column(Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 520.dp).verticalScroll(rememberScrollState())) {
						Text(aiAnswer.value ?: "", style = MaterialTheme.typography.bodyMedium)
					}
				}
			)
		}
	}
}

@Composable
private fun HeaderSection(state: RepoDetailState, onBack: () -> Unit, onSelectBranch: (String) -> Unit) {
	val detail = state.detail
	Surface(
		tonalElevation = 2.dp,
		modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(MaterialTheme.shapes.medium)
	) {
		Column(Modifier.fillMaxWidth().padding(16.dp)) {
			Text(detail?.name ?: state.repository ?: "Repository", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
			if (!detail?.description.isNullOrEmpty()) {
				Spacer(Modifier.height(6.dp))
				Text(detail!!.description!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
			}
			Spacer(Modifier.height(10.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				AssistChip(
					onClick = {},
					label = { Text("★ ${detail?.stars ?: 0}") },
					leadingIcon = { Icon(Icons.Outlined.Star, contentDescription = null) },
					colors = AssistChipDefaults.assistChipColors()
				)
				AssistChip(
					onClick = {},
					label = { Text("Forks ${detail?.forks ?: 0}") },
					leadingIcon = { Icon(Icons.Outlined.CallSplit, contentDescription = null) }
				)
				if (!detail?.language.isNullOrEmpty()) {
					AssistChip(onClick = {}, label = { Text(detail!!.language!!) })
				}
			}
			Spacer(Modifier.height(10.dp))
			if (state.branches.isNotEmpty()) {
				BranchPicker(branches = state.branches, selected = state.selectedBranch ?: detail?.defaultBranch.orEmpty(), onSelect = onSelectBranch)
			}
			Spacer(Modifier.height(12.dp))
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				FilledTonalButton(onClick = onBack) {
					Icon(Icons.Filled.ArrowBack, contentDescription = null)
					Spacer(Modifier.width(6.dp))
					Text("Back to user")
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchPicker(branches: List<String>, selected: String, onSelect: (String) -> Unit) {
	var expanded = remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = !expanded.value }) {
		TextField(
			value = selected,
			onValueChange = {},
			readOnly = true,
			label = { Text("Branch") },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
			modifier = Modifier.menuAnchor().fillMaxWidth()
		)
		ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
			branches.forEach { name ->
				DropdownMenuItem(text = { Text(name) }, onClick = {
					expanded.value = false
					onSelect(name)
				})
			}
		}
	}
}

@Composable
private fun FilesHeader(path: String, onNavigatePath: (String) -> Unit) {
	Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
		Text("Files", style = MaterialTheme.typography.titleMedium)
		Spacer(Modifier.height(8.dp))
		Breadcrumbs(path = path, onNavigatePath = onNavigatePath)
	}
}

@Composable
private fun FileRow(
	item: GithubContent,
	onClick: (GithubContent) -> Unit
) {
	Surface(
		color = MaterialTheme.colorScheme.surfaceVariant,
		tonalElevation = 1.dp,
		modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clip(MaterialTheme.shapes.medium)
	) {
		Row(
			Modifier.fillMaxWidth().clickable { onClick(item) }.padding(horizontal = 12.dp, vertical = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(if (item.type == ContentType.Dir) Icons.Outlined.Folder else Icons.Outlined.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
			Spacer(Modifier.width(12.dp))
			Column(Modifier.weight(1f)) {
				Text(item.name, style = MaterialTheme.typography.titleSmall)
				if (item.type == ContentType.File && (item.size ?: 0) > 0) {
					Text("${item.size} bytes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
				}
			}
		}
	}
}

@Composable
private fun FooterSection(defaultBranch: String?) {
	if (!defaultBranch.isNullOrEmpty()) {
		Surface(
			tonalElevation = 1.dp,
			modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(MaterialTheme.shapes.medium)
		) {
			Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
				Icon(Icons.Outlined.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
				Spacer(Modifier.width(8.dp))
				Text("Default branch: $defaultBranch", style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Breadcrumbs(path: String, onNavigatePath: (String) -> Unit) {
	val segments = path.split('/').filter { it.isNotEmpty() }
	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		AssistChip(onClick = { onNavigatePath("") }, label = { Text("root") })
		var cumulative = ""
		for (seg in segments) {
			cumulative = if (cumulative.isEmpty()) seg else "$cumulative/$seg"
			AssistChip(onClick = { onNavigatePath(cumulative) }, label = { Text(seg) })
		}
	}
}


