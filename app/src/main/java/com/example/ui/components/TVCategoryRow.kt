package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

/**
 * Horizontale Video-Reihe (z.B. für eine Darstellerin oder Favoriten).
 * Unterstützt D-Pad-Navigation und saubere Z-Achsen-Abstände.
 */
@Composable
fun TVCategoryRow(
    category: ActressCategory,
    onVideoSelected: (VideoItem) -> Unit,
    onVideoFocused: (VideoItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        // Titel der Kategorie mit Link-Anzahl
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(
                text = category.name,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "• ${category.count} Videos",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontale Liste mit Videos
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp),
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
