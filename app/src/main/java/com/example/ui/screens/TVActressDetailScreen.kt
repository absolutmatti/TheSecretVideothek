package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Zeigt alle Videotitel einer Darstellerin an.
 * Mit getrennt fokussierbaren Elementen per D-Pad:
 * 1. Videotitel-Kachel (Klick öffnet die Website)
 * 2. Favoriten-Herz-Button (Rechts mit D-Pad ansteuerbar, Klick toggelt Favorit)
 */
@Composable
fun TVActressDetailScreen(
    category: ActressCategory,
    favoriteIds: Set<String>,
    onVideoSelected: (VideoItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .testTag("tv_actress_detail_screen")
    ) {
        // Kopfzeile: Zurück, Darstellerinnen-Name & Anzahl der Videos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            var isBackFocused by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isBackFocused) NeonCyan else Color(0x33FFFFFF))
                    .border(1.dp, if (isBackFocused) NeonCyan else GlassBorderSubtle, CircleShape)
                    .onFocusChanged { isBackFocused = it.isFocused }
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && (it.key == Key.DirectionCenter || it.key == Key.Enter)) {
                            onBack()
                            true
                        } else false
                    }
                    .focusable()
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = if (isBackFocused) OledBlack else TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3300F2FE))
                            .border(0.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${category.videos.size} Videos",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "D-Pad: Titel auswählen (Website öffnen) • D-Pad Rechts: Herz-Symbol (Favorit umschalten)",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (category.videos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = NeonCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Lade Videos für ${category.name}...",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            // Vertikale Liste aller Videotitel
            LazyColumn(
                contentPadding = PaddingValues(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = category.videos,
                    key = { _, item -> item.id }
                ) { index, video ->
                    val isFavorite = favoriteIds.contains(video.id)
                    VideoTitleListItem(
                        index = index + 1,
                        video = video,
                        isFavorite = isFavorite,
                        onOpenWebsite = { onVideoSelected(video) },
                        onToggleFavorite = { onToggleFavorite(video.id) }
                    )
                }
            }
        }
    }
}

/**
 * Einzelner Listeneintrag für einen Videotitel.
 * Besitzt ZWEI getrennt per D-Pad ansteuerbare Elemente:
 * 1. Titel-Kachel (links): Klick öffnet die Website
 * 2. Favoriten-Button (rechts): Klick toggelt Favorit
 */
@Composable
fun VideoTitleListItem(
    index: Int,
    video: VideoItem,
    isFavorite: Boolean,
    onOpenWebsite: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTitleFocused by remember { mutableStateOf(false) }
    var isHeartFocused by remember { mutableStateOf(false) }

    val titleScale by animateFloatAsState(
        targetValue = if (isTitleFocused) 1.02f else 1.0f,
        label = "TitleScale"
    )

    val heartScale by animateFloatAsState(
        targetValue = if (isHeartFocused) 1.25f else 1.0f,
        label = "HeartScale"
    )

    val itemShape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. HAUPTKACHEL LINKS: Videotitel & Website-Öffnen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .zIndex(if (isTitleFocused) 5f else 1f)
                .graphicsLayer {
                    scaleX = titleScale
                    scaleY = titleScale
                }
                .clip(itemShape)
                .background(
                    if (isTitleFocused) Color(0x3300F2FE) else GlassSurfaceDark
                )
                .border(
                    border = if (isTitleFocused) {
                        BorderStroke(2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonBlue)))
                    } else {
                        BorderStroke(1.dp, GlassBorderSubtle)
                    },
                    shape = itemShape
                )
                .onFocusChanged { isTitleFocused = it.isFocused }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                                onOpenWebsite()
                                true
                            }
                            Key.F, Key.Menu -> {
                                onToggleFavorite()
                                true
                            }
                            else -> false
                        }
                    } else false
                }
                .focusable()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onOpenWebsite()
                }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Nummerierung & Titel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isTitleFocused) NeonCyan else Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            color = if (isTitleFocused) OledBlack else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            color = if (isTitleFocused) Color.White else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = if (isTitleFocused) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = video.url,
                            color = if (isTitleFocused) NeonCyan else TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Website-Öffnen Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isTitleFocused) NeonCyan else Color(0x28FFFFFF))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Website",
                            tint = if (isTitleFocused) OledBlack else TextPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTitleFocused) "Website öffnen (OK)" else "Website",
                            color = if (isTitleFocused) OledBlack else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. FAVORITEN-HERZ-BUTTON RECHTS (eigenständig per D-Pad ansteuerbar!)
        Box(
            modifier = Modifier
                .size(width = if (isHeartFocused) 130.dp else 56.dp, height = 76.dp)
                .zIndex(if (isHeartFocused) 10f else 1f)
                .graphicsLayer {
                    scaleX = heartScale
                    scaleY = heartScale
                }
                .clip(itemShape)
                .background(
                    when {
                        isHeartFocused -> Color(0x40FFB300)
                        isFavorite -> Color(0x22FFB300)
                        else -> GlassSurfaceDark
                    }
                )
                .border(
                    border = when {
                        isHeartFocused -> BorderStroke(2.dp, AccentGold)
                        isFavorite -> BorderStroke(1.dp, Color(0x80FFB300))
                        else -> BorderStroke(1.dp, GlassBorderSubtle)
                    },
                    shape = itemShape
                )
                .onFocusChanged { isHeartFocused = it.isFocused }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                                onToggleFavorite()
                                true
                            }
                            else -> false
                        }
                    } else false
                }
                .focusable()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onToggleFavorite()
                }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Favorit entfernen" else "Zu Favoriten hinzufügen",
                    tint = if (isFavorite || isHeartFocused) AccentGold else TextSecondary,
                    modifier = Modifier.size(26.dp)
                )

                if (isHeartFocused) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFavorite) "Gemerkt" else "Merken",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
