package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Oberer Metadaten-Bereich für das aktuell per D-Pad fokussierte Video.
 * Blendet Titel, Darstellerin, Qualitäts-Badges und Schnellaktionen ein.
 */
@Composable
fun HeroMetadataSection(
    video: VideoItem?,
    onPlayClicked: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 48.dp, vertical = 12.dp)
    ) {
        AnimatedContent(
            targetState = video,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "HeroMetadataTransition"
        ) { focused ->
            if (focused != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Darstellerinnen-Pill & Qualitäts-Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(NeonCyan, NeonBlue)))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = focused.actress.uppercase(),
                                color = OledBlack,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33FFFFFF))
                                .border(0.5.dp, GlassBorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OLED 4K Ready",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Web Direktlink",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Titel des Videos
                    Text(
                        text = focused.title,
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Sub-Info / URL
                    Text(
                        text = "Quelle: ${focused.url}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Schnell-Aktionen (Website öffnen, Favorite)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = onPlayClicked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = OledBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Zur Website (OK)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onToggleFavorite,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (focused.isFavorite) AccentGold else TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = if (focused.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (focused.isFavorite) AccentGold else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (focused.isFavorite) "Gemerkt" else "Favorisieren",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                // Platzhalter, wenn noch kein Element fokussiert wurde
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SECRET VIDEOTHEK",
                        color = NeonCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Wähle einen Film mit dem D-Pad der Fernbedienung",
                        color = TextSecondary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
