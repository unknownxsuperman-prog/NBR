package com.unknownxsuperman.glass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class BrowserViewModel : ViewModel() {
    val tabs: SnapshotStateList<TabState> = mutableStateListOf()
    var currentIndex by mutableStateOf(0)
        private set

    private var nextId = 1L

    init {
        addTab()
    }

    val currentTab: TabState?
        get() = tabs.getOrNull(currentIndex)

    fun addTab() {
        val tab = TabState(nextId++)
        tabs.add(tab)
        currentIndex = tabs.lastIndex
    }

    fun closeTab(index: Int) {
        if (index !in tabs.indices) return

        if (tabs.size <= 1) {
            // Never leave zero tabs — reset to one fresh new-tab instead.
            tabs[0].webView?.destroy()
            tabs.clear()
            addTab()
            return
        }

        tabs[index].webView?.destroy()
        tabs.removeAt(index)
        currentIndex = when {
            currentIndex >= tabs.size -> tabs.size - 1
            currentIndex > index -> currentIndex - 1
            else -> currentIndex
        }
    }

    fun selectTab(index: Int) {
        if (index in tabs.indices) currentIndex = index
    }

    fun goHome() {
        currentTab?.let { tab ->
            tab.isNewTab = true
            tab.url = ""
            tab.title = "New Tab"
        }
    }
}
