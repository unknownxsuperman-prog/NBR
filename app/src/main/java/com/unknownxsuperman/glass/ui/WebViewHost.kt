package com.unknownxsuperman.glass.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.unknownxsuperman.glass.TabState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewHost(tab: TabState, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val existing = tab.webView
            if (existing != null) {
                // Re-parenting an existing WebView preserves its history,
                // scroll position, and any in-flight page state instead of
                // reloading from scratch every time you switch back to
                // this tab.
                (existing.parent as? ViewGroup)?.removeView(existing)
                return@AndroidView existing
            }

            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        tab.canGoBack = view.canGoBack()
                        tab.canGoForward = view.canGoForward()
                        tab.url = url ?: tab.url
                        tab.isSecure = url?.startsWith("https://") == true
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onReceivedTitle(view: WebView, title: String?) {
                        tab.title = if (title.isNullOrBlank()) "New Tab" else title
                    }
                }

                tab.webView = this
                if (tab.url.isNotBlank()) loadUrl(tab.url)
            }
        }
    )
}
