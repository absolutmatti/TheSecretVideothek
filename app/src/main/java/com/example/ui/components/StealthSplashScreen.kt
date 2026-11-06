package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.OledBlack
import com.example.ui.theme.SystemBlue
import com.example.ui.theme.SystemCardBg
import com.example.ui.theme.SystemGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Getarnter System-Splash-Screen ("Zurücksetzen").
 * Zeigt einen harmlosen Wartungs- und Systemprüfungs-Dialog für Google TV.
 * Wechselt nach ca. 1.8 Sekunden oder per OK-Taste in die Videothek.
 */
@Composable
fun StealthSplashScreen(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0.15f) }
    var statusText by remember { mutableStateOf("Systemkomponenten werden initialisiert...") }

    // Endlos drehender Reset-Pfeil (passend zum App-Icon)
    val infiniteTransition = rememberInfiniteTransition(label = "ResetRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ResetRotationAngle"
    )

    // Automatischer Übergang nach Systemprüfung
    LaunchedEffect(Unit) {
        delay(400)
        progress = 0.45f
        statusText = "Speicher- und Cache-Integrität prüfen..."
        delay(500)
        progress = 0.85f
        statusText = "Philips 65OLED850 Treiber bereit..."
        delay(600)
        progress = 1.0f
        statusText = "Prüfung abgeschlossen. Systemdaten bereit."
        delay(350)
        onUnlocked()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .testTag("stealth_splash_screen")
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                            onUnlocked()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onUnlocked()
            },
        contentAlignment = Alignment.Center
    ) {
        // Harmloser System-Wartungskasten
        Box(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SystemCardBg)
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rotierendes Zurücksetzen-Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0x223B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "System Reset",
                        tint = SystemBlue,
                        modifier = Modifier
                            .size(44.dp)
                            .rotate(rotation)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Systemprüfung & Reset-Service",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Philips 65OLED850 • Google TV Android 14",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Fortschrittsbalken
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SystemBlue,
                    trackColor = Color(0x33FFFFFF)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = statusText,
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Checklist-Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SystemGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dienst aktiv",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "OK drücken zum Starten",
                        color = Color(0x66FFFFFF),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
