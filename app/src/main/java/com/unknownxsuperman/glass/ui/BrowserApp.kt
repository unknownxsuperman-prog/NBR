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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unknownxsuperman.glass.BrowserViewModel
import com.unknownxsuperman.glass.ui.theme.AppColors
import java.net.URLEncoder

private val overflowItems = listOf(
    "New tab", "New incognito tab", "History", "Delete browsing data",
    "Downloads", "Bookmarks", "Recent tabs", "Share…", "Find in page",
    "Translate…", "Desktop site", "Settings", "Help and feedback",
)

@Composable
fun BrowserApp(viewModel: BrowserViewModel = viewModel()) {
    val tab = viewModel.currentTab ?: return
    var addressText by remember(tab.id) { mutableStateOf(if (tab.isNewTab) "" else tab.url) }
    var showTabStrip by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }

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

    if (tab.isNewTab) {
        NewTabScreen(
            onSearch = { smartNavigate(it) },
            onQuickLink = { url -> smartNavigate(url) }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Bg)
        ) {
            // ---- content: web view, with a mobile-style fade shift ----
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = tab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(160)) togetherWith fadeOut(animationSpec = tween(120))
                    },
                    label = "tab-shift"
                ) { animatedTab ->
                    WebViewHost(tab = animatedTab, modifier = Modifier.fillMaxSize())
                }
            }

            // ---- chip tab strip, toggled via the badge (hidden by default) ----
            if (showTabStrip) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.tabs.size) { index ->
                        val t = viewModel.tabs[index]
                        val selected = index == viewModel.currentIndex
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selected) AppColors.Surface3 else AppColors.Surface1.copy(alpha = 0.9f))
                                .border(
                                    1.dp,
                                    if (selected) AppColors.BrandRed.copy(alpha = 0.6f) else AppColors.Border,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectTab(index) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.BrandRed)
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                            }
                            Text(
                                text = t.title.take(16),
                                color = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close tab",
                                tint = AppColors.TextTertiary,
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable { viewModel.closeTab(index) }
                            )
                        }
                    }
                }
            }

            // ---- glass toolbar, anchored to the bottom like modern mobile browsers ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(28.dp), clip = false)
                    .clip(RoundedCornerShape(28.dp))
                    .background(AppColors.Surface1.copy(alpha = 0.92f))
                    .border(1.dp, AppColors.Border, RoundedCornerShape(28.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavIconButton(icon = Icons.Filled.Home, enabled = true) {
                    viewModel.goHome()
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(AppColors.Surface2)
                        .border(1.dp, AppColors.Border, RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    TextField(
                        value = addressText,
                        onValueChange = { addressText = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
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

                Spacer(modifier = Modifier.width(4.dp))
                TabBadge(count = viewModel.tabs.size) { showTabStrip = !showTabStrip }
                Spacer(modifier = Modifier.width(4.dp))

                Box {
                    NavIconButton(icon = Icons.Filled.MoreVert, enabled = true) { menuOpen = true }

                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        overflowItems.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    menuOpen = false
                                    if (label == "New tab") viewModel.addTab()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) AppColors.TextSecondary else AppColors.Surface4,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun TabBadge(count: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.Surface2)
            .border(1.4.dp, AppColors.BorderStrong, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = AppColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
