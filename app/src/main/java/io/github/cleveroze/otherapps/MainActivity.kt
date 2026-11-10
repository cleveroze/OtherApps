package io.github.cleveroze.otherapps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.cleveroze.otherapps.ui.OtherAppsApp
import io.github.cleveroze.otherapps.ui.SplashScreen
import io.github.cleveroze.otherapps.ui.theme.OtherAppsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OtherAppsTheme {
                var splash by rememberSaveable { mutableStateOf(true) }
                Crossfade(
                    targetState = splash,
                    animationSpec = tween(420),
                    label = "splash",
                ) { showSplash ->
                    if (showSplash) {
                        SplashScreen { splash = false }
                    } else {
                        val viewModel: MainViewModel = viewModel()
                        OtherAppsApp(viewModel)
                    }
                }
            }
        }
    }
}
