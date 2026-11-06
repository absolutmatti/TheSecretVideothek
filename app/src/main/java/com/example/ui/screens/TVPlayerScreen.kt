package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.data.model.VideoItem
import com.example.data.remote.VideoExtractor
import com.example.player.TVPlayerManager
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed
import kotlinx.coroutines.delay

/**
 * Nativer Vollbild-Videoplayer für Android TV mit Media3 ExoPlayer.
 * Bietet D-Pad-Steuerung (Play/Pause, Spulen) und ein elegantes Glass-OSD.
 */
@OptIn(UnstableApi::class)
@Composable
fun TVPlayerScreen(
    video: VideoItem,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerManager = remember { TVPlayerManager(context) }

    val isPlaying by playerManager.isPlaying.collectAsState()
    val isBuffering by playerManager.isBuffering.collectAsState()
    val currentPos by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    val errorMessage by playerManager.errorMessage.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var seekNotice by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBackClicked()
    }

    // Beim Betreten des Screens: Video vorbereiten und abspielen
    LaunchedEffect(video) {
        val resolved = VideoExtractor.resolvePlayableUrl(video.url)
        playerManager.prepareAndPlay(resolved)
    }

    // Automatisches Ausblenden des OSD nach Inaktivität
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    // Seek Notification timer
    LaunchedEffect(seekNotice) {
        if (seekNotice != null) {
            delay(1200)
            seekNotice = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerManager.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .testTag("tv_player_screen")
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                            playerManager.togglePlayPause()
                            showControls = true
                            true
                        }
                        Key.DirectionRight, Key.MediaFastForward -> {
                            playerManager.seekForward(10_000)
                            seekNotice = "+10s"
                            showControls = true
                            true
                        }
                        Key.DirectionLeft, Key.MediaRewind -> {
                            playerManager.seekBackward(10_000)
                            seekNotice = "-10s"
                            showControls = true
                            true
                        }
                        Key.DirectionUp, Key.DirectionDown -> {
                            showControls = !showControls
                            true
                        }
                        Key.Back, Key.Escape -> {
                            onBackClicked()
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        // ExoPlayer Surface View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playerManager.exoPlayer
                    useController = false // Wir nutzen unser eigenes M3 TV Glass-OSD
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Lade- / Pufferungs-Anzeige
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 4.dp
                )
            }
        }

        // Seek Badge Pop-up (+10s / -10s)
        seekNotice?.let { notice ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = notice,
                        color = NeonCyan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Fehler-Dialog mit TV-Aktionen
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE05070B)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(520.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xEE1E293B))
                        .border(1.dp, WarningRed, RoundedCornerShape(20.dp))
                        .padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Fehler",
                        tint = WarningRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Video-Stream nicht direkt ladbar",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dieser Link ist möglicherweise eine geschützte Webseite oder benötigt einen Browser.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { playerManager.loadFallbackDemoStream() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = OledBlack),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test-Stream (OLED)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    // ignore
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Im Browser", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onBackClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zurück zur Übersicht", fontSize = 13.sp)
                    }
                }
            }
        }

        // On-Screen-Display (OSD) Steuerung
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xD9000000),
                                Color.Transparent,
                                Color(0xEB000000)
                            )
                        )
                    )
            ) {
                // Obere Leiste: Titel & Zurück
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x40FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = video.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${video.actress} • Philips 65OLED850 Cinema Engine",
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    }
                }

                // Untere Leiste: Timeline & Play/Pause
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 48.dp, vertical = 36.dp)
                ) {
                    // Fortschrittsbalken
                    val progressFraction = if (duration > 0) (currentPos.toFloat() / duration).coerceIn(0f, 1f) else 0f

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = Color(0x33FFFFFF)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause & Spul-Buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { playerManager.seekBackward(10_000) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2BFFFFFF))
                            ) {
                                Icon(Icons.Default.FastRewind, contentDescription = "-10s", tint = TextPrimary)
                            }

                            IconButton(
                                onClick = { playerManager.togglePlayPause() },
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = OledBlack,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = { playerManager.seekForward(10_000) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2BFFFFFF))
                            ) {
                                Icon(Icons.Default.FastForward, contentDescription = "+10s", tint = TextPrimary)
                            }

                            Text(
                                text = "D-Pad: ◄ -10s  |  OK: Pause  |  ► +10s",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }

                        // Zeitanzeige
                        Text(
                            text = "${formatTime(currentPos)} / ${formatTime(duration)}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
