package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Horizontale Video-Reihe für eine Darstellerin.
 * Ermöglicht das direkte Öffnen der Website jedes Videos
 * oder das Anzeigen aller Videotitel in der Detailansicht.
 */
@Composable
fun TVCategoryRow(
    category: ActressCategory,
    onVideoSelected: (VideoItem) -> Unit,
    onVideoFocused: (VideoItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onActressSelected: (ActressCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var isHeaderFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Kopfzeile mit Darstellerin-Titel & Button "Alle Titel auflisten"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = category.name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "• ${category.count} Videos",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            // Klickbarer Button zum Anzeigen aller Videotitel der Darstellerin
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isHeaderFocused) NeonCyan else Color(0x22FFFFFF)
                    )
                    .border(
                        1.dp,
                        if (isHeaderFocused) NeonCyan else GlassBorderSubtle,
                        RoundedCornerShape(10.dp)
                    )
                    .onFocusChanged { isHeaderFocused = it.isFocused }
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && (it.key == Key.DirectionCenter || it.key == Key.Enter)) {
                            onActressSelected(category)
                            true
                        } else false
                    }
                    .focusable()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onActressSelected(category)
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Alle ${category.videos.size} Titel anzeigen",
                        color = if (isHeaderFocused) OledBlack else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isHeaderFocused) FontWeight.Bold else FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Öffnen",
                        tint = if (isHeaderFocused) OledBlack else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontale Reihe von Videokarten (Klick öffnet direkt die Website)
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            itemsIndexed(
                items = category.videos,
                key = { _, item -> item.id }
            ) { _, video ->
                SpatialGlassCard(
                    video = video,
                    onClick = { onVideoSelected(video) },
                    onFocused = { onVideoFocused(video) },
                    onToggleFavorite = { onToggleFavorite(video.id) }
                )
            }
        }
    }
}
