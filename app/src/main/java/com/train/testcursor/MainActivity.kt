package com.train.testcursor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.train.testcursor.ui.theme.TestcursorTheme
import com.train.testcursor.presentation.navigation.AppNav
import com.train.testcursor.ui.theme.ProvideAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvideAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNav(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ProvideAppTheme {
        // Preview not wired to Nav
    }
}