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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.ActressCategory
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
 * Darstellerinnen-Kachel für die Startseite.
 * Lädt das hochgeladene Foto aus dem GitHub-Repository (z.B. photo.jpg oder cover.jpg)
 * und zeigt darunter den Namen und die Anzahl der Videos an.
 */
@Composable
fun ActressTileCard(
    category: ActressCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.10f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TileScaleAnimation"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isFocused) 22f else 3f,
        label = "TileElevation"
    )

    val cardShape = RoundedCornerShape(18.dp)
    val initials = remember(category.name) {
        val parts = category.name.trim().split(" ")
        if (parts.size >= 2) {
            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        } else {
            category.name.take(2).uppercase()
        }
    }

    Box(
        modifier = modifier
            .width(200.dp)
            .height(280.dp)
            .zIndex(if (isFocused) 15f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = elevation
                clip = false
            }
            .testTag("actress_tile_${category.name.lowercase().replace(" ", "_")}")
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
    ) {
        // Äußere Glaskarte mit leuchtendem Neon-Fokusrahmen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (isFocused) 16.dp else 2.dp,
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
                // 1. Fotobereich (lädt das hochgeladene Darstellerinnen-Foto)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
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
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(category.resolvedPhotoUrl())
                            .crossfade(true)
                            .build(),
                        contentDescription = category.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            ActressPlaceholder(
                                initials = initials,
                                isFocused = isFocused
                            )
                        },
                        error = {
                            ActressPlaceholder(
                                initials = initials,
                                isFocused = isFocused
                            )
                        }
                    )

                    // Overlay-Gradient für sauberen Übergang zum Textbereich
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0x22000000),
                                        Color(0xDD0B101B)
                                    )
                                )
                            )
                    )

                    // Fokus-Badge "Öffnen"
                    if (isFocused) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xCC000000))
                                .border(0.5.dp, NeonCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "▶ OK",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 2. Textbereich unter der Kachel (Name und Anzahl der Videos)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            if (isFocused) Color(0x3300F2FE) else Color(0x18FFFFFF)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = category.name,
                        color = if (isFocused) Color.White else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = if (isFocused) NeonCyan else TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val countText = if (category.count > 0) {
                            "${category.count} Videos"
                        } else if (category.videos.isNotEmpty()) {
                            "${category.videos.size} Videos"
                        } else {
                            "Videos anzeigen"
                        }
                        Text(
                            text = countText,
                            color = if (isFocused) NeonCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Platzhalter, falls noch kein Foto im Repository hochgeladen wurde.
 */
@Composable
private fun ActressPlaceholder(
    initials: String,
    isFocused: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        if (isFocused) Color(0x4000F2FE) else Color(0x253B82F6),
                        Color(0xFF070B14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFocused) {
                            Brush.linearGradient(listOf(NeonCyan, NeonBlue))
                        } else {
                            Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x15FFFFFF)))
                        }
                    )
                    .border(1.dp, if (isFocused) NeonCyan else GlassBorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = if (isFocused) OledBlack else TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
