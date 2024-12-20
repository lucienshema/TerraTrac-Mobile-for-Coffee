package org.technoserve.cafetrac.ui.screens.privacy

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cafetrac.database.helpers.PreferencesManager


@Composable
fun PrivacyPolicyScreen(
    url: String,
    onAgree: () -> Unit // Callback for when the user agrees to the terms
) {

    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    var isAtBottom by remember { mutableStateOf(false) }
    var isAgreeEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // WebView for Privacy Policy
        Box(modifier = Modifier.weight(1f)) {
            PrivacyPolicyWebView(url = url, onScrollAtBottom = {
                isAtBottom = true
                isAgreeEnabled = true
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Agree" button, enabled when user scrolls to the bottom
        Button(
            onClick = {
                preferencesManager.hasAgreedToTerms = true
                onAgree()
            },
            enabled = isAgreeEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agree")
        }
    }
}

@Composable
fun PrivacyPolicyWebView(url: String, onScrollAtBottom: () -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Add a scroll listener to detect when the user has reached the bottom
                        view?.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                            if (!view.canScrollVertically(1)) {
                                // User has scrolled to the bottom of the WebView
                                onScrollAtBottom()
                            }
                        }
                    }
                }
                loadUrl(url)
            }
        }
    )
}
