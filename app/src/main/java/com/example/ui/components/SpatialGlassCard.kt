package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GlassBorderFocused
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Spatial Glassmorphism Video Card für Google TV.
 * Realisiert Z-Achsen-Verschiebung: Vergrößerung, Z-Index Erhöhung und Neon-Glow
 * bei D-Pad Fokus.
 */
@Composable
fun SpatialGlassCard(
    video: VideoItem,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    // Z-Achsen-Animation: Sanfter Zoom und Schwebe-Effekt bei D-Pad-Fokus
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SpatialScaleAnimation"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isFocused) 24f else 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ElevationAnimation"
    )

    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .width(220.dp)
            .height(290.dp)
            .zIndex(if (isFocused) 15f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = elevation
                clip = false
            }
            .testTag("video_card_${video.id}")
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (focusState.isFocused) {
                    onFocused()
                }
            }
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
    ) {
        // Leuchtender Rahmen (Glow-Effekt auf der Z-Achse)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (isFocused) 18.dp else 2.dp,
                    shape = cardShape,
                    ambientColor = if (isFocused) NeonCyan else Color.Transparent,
                    spotColor = if (isFocused) NeonBlue else Color.Transparent
                )
                .clip(cardShape)
                .background(GlassSurfaceDark)
                .border(
                    border = if (isFocused) {
                        BorderStroke(
                            2.5.dp,
                            Brush.linearGradient(listOf(NeonCyan, NeonBlue, NeonPurple))
                        )
                    } else {
                        BorderStroke(1.dp, GlassBorderSubtle)
                    },
                    shape = cardShape
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Poster-Bereich mit Glas-Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    OledBlack
                                )
                            )
                        )
                ) {
                    if (!video.posterUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = video.posterUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Generierter stilisierter Poster-Platzhalter im Glass-Design
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            if (isFocused) Color(0x4000F2FE) else Color(0x203B82F6),
                                            Color(0xFF070B14)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (isFocused) NeonCyan else Color(0x33FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Abspielen",
                                        tint = if (isFocused) OledBlack else TextPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = video.actress,
                                    color = if (isFocused) NeonCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Favoriten-Badge
                    if (video.isFavorite) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xCC000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorit",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Auflösungs-Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xCC05070B))
                            .border(0.5.dp, Color(0x40FFFFFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = video.durationText,
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Metadaten-Bereich der Karte
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            if (isFocused) Color(0x3300F2FE) else Color(0x15FFFFFF)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = video.actress,
                            color = if (isFocused) NeonBlue else TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "▶ OK",
                            color = if (isFocused) NeonCyan else Color.Transparent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
