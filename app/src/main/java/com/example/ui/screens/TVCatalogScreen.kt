package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.ui.components.HeroMetadataSection
import com.example.ui.components.TVCategoryRow
import com.example.ui.components.TVNavigationSidebar
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PureBlack
import com.example.ui.viewmodel.TVScreen

/**
 * Hauptbildschirm (Spatial Video-Katalog) für Google TV.
 * Bietet dynamisch unscharfe Hintergründe bei D-Pad-Fokus,
 * Z-Achsen-Glassmorphism und flüssige Reihennavigation.
 */
@Composable
fun TVCatalogScreen(
    categories: List<ActressCategory>,
    focusedVideo: VideoItem?,
    currentScreen: TVScreen,
    onVideoSelected: (VideoItem) -> Unit,
    onVideoFocused: (VideoItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onNavigate: (TVScreen) -> Unit,
    onRefresh: () -> Unit,
    onPanic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalListState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("tv_catalog_screen")
    ) {
        // 1. Dynamischer Umgebungs-Hintergrund mit Weichzeichner (Blur-Effekt)
        // Reagiert auf das aktuell per D-Pad fokussierte Video
        Crossfade(
            targetState = focusedVideo,
            animationSpec = tween(durationMillis = 600),
            label = "BackdropBlurCrossfade",
            modifier = Modifier.fillMaxSize()
        ) { video ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (!video?.posterUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = video?.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(radius = 50.dp)
                    )
                } else {
                    // OLED-optimierter sphärischer Farbverlauf
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x3300F2FE),
                                        Color(0x221E1B4B),
                                        OledBlack
                                    )
                                )
                            )
                    )
                }

                // Vignette & Abdunklung für optimalen Kontrast und OLED-Schutz
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x99000000),
                                    Color(0xCC05070B),
                                    PureBlack
                                )
                            )
                        )
                )
            }
        }

        // 2. Haupt-Inhalt mit Seitenleiste und vertikalen Video-Kategorien
        Row(modifier = Modifier.fillMaxSize()) {
            // Schwebende Navigationsleiste links
            TVNavigationSidebar(
                currentScreen = currentScreen,
                onNavigate = onNavigate,
                onRefresh = onRefresh,
                onPanic = onPanic
            )

            // Vertikale Scroll-Ansicht: Oben Metadaten, darunter die Reihen
            LazyColumn(
                state = verticalListState,
                contentPadding = PaddingValues(bottom = 64.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // A. Oberer Metadaten-Bereich für das fokussierte Video
                item(key = "hero_metadata_section") {
                    HeroMetadataSection(
                        video = focusedVideo,
                        onPlayClicked = {
                            focusedVideo?.let { onVideoSelected(it) }
                        },
                        onToggleFavorite = {
                            focusedVideo?.let { onToggleFavorite(it.id) }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // B. Horizontale Reihen für jede Darstellerin / Kategorie
                itemsIndexed(
                    items = categories,
                    key = { _, cat -> cat.name }
                ) { _, category ->
                    if (category.videos.isNotEmpty()) {
                        TVCategoryRow(
                            category = category,
                            onVideoSelected = onVideoSelected,
                            onVideoFocused = onVideoFocused,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}
