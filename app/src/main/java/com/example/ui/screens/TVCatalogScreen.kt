package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActressCategory
import com.example.ui.components.ActressTileCard
import com.example.ui.components.TVNavigationSidebar
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TVScreen

/**
 * Startseite der App: Zeigt ausschließlich die Darstellerinnen als Kacheln an.
 * Erst beim Klick auf eine Darstellerin werden alle ihre Videos aufgelistet.
 */
@Composable
fun TVCatalogScreen(
    categories: List<ActressCategory>,
    currentScreen: TVScreen,
    onActressSelected: (ActressCategory) -> Unit,
    onNavigate: (TVScreen) -> Unit,
    onRefresh: () -> Unit,
    onPanic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("tv_catalog_screen")
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. Schwebende Navigationsleiste links
            TVNavigationSidebar(
                currentScreen = currentScreen,
                onNavigate = onNavigate,
                onRefresh = onRefresh,
                onPanic = onPanic
            )

            // 2. Hauptbereich: Darstellerinnen-Raster (Kacheln)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 40.dp, vertical = 24.dp)
            ) {
                // Kopfbereich: Titel & Statistik
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Darstellerinnen",
                                color = TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x3300F2FE))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${categories.size} Darstellerinnen",
                                    color = NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Wähle eine Darstellerin, um alle ihre Videos anzuzeigen",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "A - Z Verzeichnis",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Kachelraster für alle Darstellerinnen
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 190.dp),
                    contentPadding = PaddingValues(bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = categories,
                        key = { it.name }
                    ) { category ->
                        ActressTileCard(
                            category = category,
                            onClick = { onActressSelected(category) }
                        )
                    }
                }
            }
        }
    }
}
