package com.unknownxsuperman.glass.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unknownxsuperman.glass.R
import com.unknownxsuperman.glass.ui.theme.AppColors

private data class QuickLink(val label: String, val url: String)

private val quickLinks = listOf(
    QuickLink("YouTube", "https://youtube.com"),
    QuickLink("GitHub", "https://github.com"),
    QuickLink("X", "https://x.com"),
    QuickLink("Reddit", "https://reddit.com"),
    QuickLink("Wikipedia", "https://wikipedia.org"),
)

private val overflowItems = listOf(
    "History", "Delete browsing data", "Site controls", "Downloads",
    "Bookmarks", "Recent tabs", "Share…", "Find in page", "Translate…",
    "Show Reading mode", "Desktop site", "Settings", "Help and feedback",
)

@Composable
fun NewTabScreen(onSearch: (String) -> Unit, onQuickLink: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // ---- top bar ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Browser", color = AppColors.TextPrimary, fontSize = 24.sp, fontFamily = FontFamily.Serif)

            Box {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { menuOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⋯", color = AppColors.TextSecondary, fontSize = 20.sp)
                }

                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    overflowItems.forEach { label ->
                        DropdownMenuItem(text = { Text(label) }, onClick = { menuOpen = false })
                    }
                    DropdownMenuItem(
                        text = { Text("Make Glass Browser default") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_brand),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = { menuOpen = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ---- search pill ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(AppColors.Surface1)
                .border(1.dp, AppColors.Border, RoundedCornerShape(100.dp))
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Search or type web address", color = AppColors.TextSecondary, fontSize = 14.sp) },
                textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
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

        Spacer(modifier = Modifier.height(26.dp))

        // ---- quicklinks ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickTile(label = "browser", isSelf = true) { }
            quickLinks.forEach { link ->
                QuickTile(label = link.label, isSelf = false) { onQuickLink(link.url) }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        ShortcutsCard()

        Spacer(modifier = Modifier.height(16.dp))

        PrivacyCard()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RowScope.QuickTile(label: String, isSelf: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isSelf) AppColors.BrandRed else AppColors.Surface1)
                .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isSelf) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_mono),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = AppColors.TextTertiary, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun ShortcutsCard() {
    val items = listOf("Bookmarks", "Downloads", "Reading list", "Extensions", "History", "Settings")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.Surface1)
            .border(1.dp, AppColors.Border, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Text(text = "Shortcuts", color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(14.dp))
        items.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth()) {
                pair.forEach { item ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AppColors.Surface3),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "•", color = AppColors.TextSecondary, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = item, color = AppColors.TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.Surface1)
            .border(1.dp, AppColors.Border, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Text(text = "Privacy", color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Trackers & ads blocked", color = AppColors.TextPrimary, fontSize = 13.sp)
        Text(text = "1,048 this week", color = AppColors.TextTertiary, fontSize = 11.sp)
    }
}
