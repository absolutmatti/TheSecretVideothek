package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.StealthEmergencyDialog
import com.example.ui.components.StealthSplashScreen
import com.example.ui.screens.TVCatalogScreen
import com.example.ui.screens.TVFavoritesScreen
import com.example.ui.screens.TVPlayerScreen
import com.example.ui.screens.TVSearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureBlack
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.TVScreen

/**
 * Haupteinstiegspunkt für die getarnte Android TV Videothek-App ("Zurücksetzen").
 * Optimiert für Google TV (Android 14) und Philips 65OLED850 Fernbedienungssteuerung.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                TVAppRoot(viewModel = viewModel)
            }
        }
    }

    /**
     * Fängt TV-Fernbedienungstasten für Schnelltarnung (Panik-Taste) ab.
     * Taste MENU oder GUIDE aktiviert sofort den Tarnmodus.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_GUIDE, KeyEvent.KEYCODE_INFO -> {
                viewModel.triggerPanicMode()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

@Composable
fun TVAppRoot(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val focusedVideo by viewModel.focusedVideo.collectAsState()
    val favoriteVideos by viewModel.favoriteVideos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "TVScreenTransition"
        ) { screen ->
            when (screen) {
                is TVScreen.StealthSplash -> {
                    StealthSplashScreen(
                        onUnlocked = { viewModel.onSplashFinished() }
                    )
                }

                is TVScreen.Catalog -> {
                    TVCatalogScreen(
                        categories = categories,
                        focusedVideo = focusedVideo,
                        currentScreen = screen,
                        onVideoSelected = { viewModel.playVideo(it) },
                        onVideoFocused = { viewModel.onVideoFocused(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onNavigate = { viewModel.navigateTo(it) },
                        onRefresh = { viewModel.refreshCatalog() },
                        onPanic = { viewModel.triggerPanicMode() }
                    )
                }

                is TVScreen.Player -> {
                    TVPlayerScreen(
                        video = screen.video,
                        onBackClicked = { viewModel.closePlayer() }
                    )
                }

                is TVScreen.Search -> {
                    TVSearchScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        onQueryChanged = { viewModel.updateSearchQuery(it) },
                        onVideoSelected = { viewModel.playVideo(it) },
                        onVideoFocused = { viewModel.onVideoFocused(it) },
                        onBack = { viewModel.navigateTo(TVScreen.Catalog) }
                    )
                }

                is TVScreen.Favorites -> {
                    TVFavoritesScreen(
                        favoriteVideos = favoriteVideos,
                        onVideoSelected = { viewModel.playVideo(it) },
                        onVideoFocused = { viewModel.onVideoFocused(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onBack = { viewModel.navigateTo(TVScreen.Catalog) }
                    )
                }

                is TVScreen.PanicReset -> {
                    StealthEmergencyDialog(
                        onDismissPanic = { viewModel.exitPanicMode() }
                    )
                }
            }
        }
    }
}
