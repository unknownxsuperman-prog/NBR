package com.unknownxsuperman.glass.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unknownxsuperman.glass.BrowserViewModel
import com.unknownxsuperman.glass.ui.theme.AppColors
import java.net.URLEncoder

@Composable
fun BrowserApp(viewModel: BrowserViewModel = viewModel()) {
    val tab = viewModel.currentTab ?: return
    var addressText by remember(tab.id) { mutableStateOf(if (tab.isNewTab) "" else tab.url) }
    var showTabStrip by remember { mutableStateOf(false) }

    LaunchedEffect(tab.url, tab.isNewTab) {
        addressText = if (tab.isNewTab) "" else tab.url
    }

    // Android's system back button navigates the page history first,
    // instead of immediately closing the app.
    BackHandler(enabled = tab.canGoBack && !tab.isNewTab) {
        tab.webView?.goBack()
    }

    fun smartNavigate(raw: String) {
        val q = raw.trim()
        if (q.isEmpty()) return
        val looksLikeUrl = q.startsWith("http://") || q.startsWith("https://") ||
            (!q.contains(" ") && q.contains("."))
        val target = when {
            q.startsWith("http://") || q.startsWith("https://") -> q
            looksLikeUrl -> "https://$q"
            else -> "https://duckduckgo.com/?q=" + URLEncoder.encode(q, "UTF-8")
        }
        tab.isNewTab = false
        val wv = tab.webView
        if (wv != null) wv.loadUrl(target) else tab.url = target
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
    ) {
        // ---- toolbar pill ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIconButton(symbol = "←", enabled = tab.canGoBack) { tab.webView?.goBack() }
            NavIconButton(symbol = "→", enabled = tab.canGoForward) { tab.webView?.goForward() }
            NavIconButton(symbol = "↻", enabled = !tab.isNewTab) { tab.webView?.reload() }
            NavIconButton(symbol = "⌂", enabled = true) { viewModel.goHome() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(AppColors.Surface1)
                    .border(1.dp, AppColors.Border, RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                TextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Search or type web address", color = AppColors.TextSecondary, fontSize = 13.sp) },
                    textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 13.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { smartNavigate(addressText) }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AppColors.Accent,
                    )
                )
            }

            TabBadge(count = viewModel.tabs.size) { showTabStrip = !showTabStrip }
            Spacer(modifier = Modifier.width(6.dp))
            NavIconButton(symbol = "+", enabled = true) { viewModel.addTab() }
        }

        // ---- chip tab strip, toggled via the badge (hidden by default) ----
        if (showTabStrip) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(viewModel.tabs.size) { index ->
                    val t = viewModel.tabs[index]
                    val selected = index == viewModel.currentIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) AppColors.Surface3 else AppColors.Surface1)
                            .border(
                                1.dp,
                                if (selected) AppColors.BorderStrong else AppColors.Border,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectTab(index) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = t.title.take(16),
                            color = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "×",
                            color = AppColors.TextTertiary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { viewModel.closeTab(index) }
                        )
                    }
                }
            }
        }

        // ---- content: new tab screen or web view, with a mobile-style fade shift ----
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = tab.id to tab.isNewTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(160)) togetherWith fadeOut(animationSpec = tween(120))
                },
                label = "tab-shift"
            ) { (_, isNewTab) ->
                if (isNewTab) {
                    NewTabScreen(
                        onSearch = { smartNavigate(it) },
                        onQuickLink = { url -> smartNavigate(url) }
                    )
                } else {
                    WebViewHost(tab = tab, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = if (enabled) AppColors.TextSecondary else AppColors.Surface4,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun TabBadge(count: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.6.dp, AppColors.TextSecondary, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = count.toString(), color = AppColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
