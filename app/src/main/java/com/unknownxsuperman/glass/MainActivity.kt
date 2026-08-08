package com.unknownxsuperman.glass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unknownxsuperman.glass.ui.BrowserApp
import com.unknownxsuperman.glass.ui.theme.GlassBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlassBrowserTheme {
                BrowserApp()
            }
        }
    }
}
