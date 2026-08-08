package com.unknownxsuperman.glass

import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * One browser tab. `webView` is created lazily by WebViewHost the first
 * time this tab is shown, and reused on subsequent tab switches so
 * navigation history / scroll position survive.
 */
class TabState(val id: Long) {
    var title by mutableStateOf("New Tab")
    var url by mutableStateOf("")
    var isNewTab by mutableStateOf(true)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    var isSecure by mutableStateOf(false)

    var webView: WebView? = null
}
