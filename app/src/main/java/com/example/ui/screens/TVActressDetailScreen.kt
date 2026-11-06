package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GlassBorderFocused
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Zeigt die vollständige Liste aller Videotitel einer Darstellerin an.
 * Beim Klick auf einen Videotitel wird sofort die entsprechende Website geöffnet.
 */
@Composable
fun TVActressDetailScreen(
    category: ActressCategory,
    onVideoSelected: (VideoItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Klicke auf einen Titel, um direkt zur Website weitergeleitet zu werden",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vertikale Liste der Videotitel
        LazyColumn(
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = category.videos,
                key = { _, item -> item.id }
            ) { index, video ->
                VideoTitleListItem(
                    index = index + 1,
                    video = video,
                    onClick = { onVideoSelected(video) },
                    onToggleFavorite = { onToggleFavorite(video.id) }
                )
            }
        }
    }
}

/**
 * Einzelner Listeneintrag für einen Videotitel mit D-Pad-Fokus und Website-Weiterleitung.
 */
@Composable
fun VideoTitleListItem(
    index: Int,
    video: VideoItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.025f else 1.0f,
        label = "ItemScale"
    )

    val itemShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(if (isFocused) 10f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(itemShape)
            .background(
                if (isFocused) Color(0x3300F2FE) else GlassSurfaceDark
            )
            .border(
                border = if (isFocused) {
                    BorderStroke(2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonBlue)))
                } else {
                    BorderStroke(1.dp, GlassBorderSubtle)
                },
                shape = itemShape
            )
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                            onClick()
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
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Index & Videotitel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Nummerierungs-Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isFocused) NeonCyan else Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = if (isFocused) OledBlack else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = if (isFocused) Color.White else TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = video.url,
                        color = if (isFocused) NeonCyan else TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Aktionen & Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Favoriten-Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                ) {
                    Icon(
                        imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint = if (video.isFavorite) AccentGold else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Website öffnen Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isFocused) NeonCyan else Color(0x33FFFFFF))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Öffnen",
                            tint = if (isFocused) OledBlack else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFocused) "Website öffnen (OK)" else "Website",
                            color = if (isFocused) OledBlack else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
