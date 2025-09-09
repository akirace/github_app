package com.train.testcursor.presentation.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FilePreviewScreen(url: String, modifier: Modifier = Modifier) {
	val interop = rememberNestedScrollInteropConnection()
	AndroidView(
		modifier = modifier.nestedScroll(interop),
		factory = { context ->
			WebView(context).apply {
				settings.javaScriptEnabled = true
				webViewClient = WebViewClient()
				loadUrl(url)
			}
		}
	)
}


