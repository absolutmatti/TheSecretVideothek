package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.StealthEmergencyDialog
import com.example.ui.components.StealthSplashScreen
import com.example.ui.screens.TVActressDetailScreen
import com.example.ui.screens.TVCatalogScreen
import com.example.ui.screens.TVFavoritesScreen
import com.example.ui.screens.TVSearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureBlack
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.TVScreen

/**
 * Haupteinstiegspunkt für die getarnte Android TV Videothek-App ("Zurücksetzen").
 * Listet die Videotitel der Darstellerinnen auf und leitet beim Klick direkt zur Website weiter.
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

/**
 * Öffnet die Video-URL direkt im TV-Browser (z.B. Chrome, TV Bro, Puffin etc.).
 */
fun openWebUrl(context: Context, url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Konnte Website nicht öffnen: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun TVAppRoot(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
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
                        currentScreen = screen,
                        onActressSelected = { category ->
                            viewModel.openActress(category)
                        },
                        onNavigate = { viewModel.navigateTo(it) },
                        onRefresh = { viewModel.refreshCatalog() },
                        onPanic = { viewModel.triggerPanicMode() }
                    )
                }

                is TVScreen.ActressDetail -> {
                    TVActressDetailScreen(
                        category = screen.category,
                        favoriteIds = favorites,
                        onVideoSelected = { video ->
                            openWebUrl(context, video.url)
                        },
                        onToggleFavorite = { videoId ->
                            viewModel.toggleFavorite(videoId)
                        },
                        onBack = { viewModel.closeActressDetail() }
                    )
                }

                is TVScreen.Search -> {
                    TVSearchScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        onQueryChanged = { viewModel.updateSearchQuery(it) },
                        onVideoSelected = { video ->
                            openWebUrl(context, video.url)
                        },
                        onVideoFocused = { viewModel.onVideoFocused(it) },
                        onBack = { viewModel.navigateTo(TVScreen.Catalog) }
                    )
                }

                is TVScreen.Favorites -> {
                    TVFavoritesScreen(
                        favoriteVideos = favoriteVideos,
                        onVideoSelected = { video ->
                            openWebUrl(context, video.url)
                        },
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
