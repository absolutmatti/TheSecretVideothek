package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TVScreen

/**
 * Schwebende Glassmorphism Navigationsleiste auf der linken Bildschirmseite.
 */
@Composable
fun TVNavigationSidebar(
    currentScreen: TVScreen,
    onNavigate: (TVScreen) -> Unit,
    onRefresh: () -> Unit,
    onPanic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(76.dp)
            .padding(vertical = 24.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurfaceDark)
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SidebarIconItem(
                icon = Icons.Default.Home,
                label = "Katalog",
                isSelected = currentScreen is TVScreen.Catalog,
                onClick = { onNavigate(TVScreen.Catalog) }
            )

            SidebarIconItem(
                icon = Icons.Default.Favorite,
                label = "Favoriten",
                isSelected = currentScreen is TVScreen.Favorites,
                activeColor = AccentGold,
                onClick = { onNavigate(TVScreen.Favorites) }
            )

            SidebarIconItem(
                icon = Icons.Default.Search,
                label = "Suche",
                isSelected = currentScreen is TVScreen.Search,
                onClick = { onNavigate(TVScreen.Search) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SidebarIconItem(
                icon = Icons.Default.Refresh,
                label = "Neu laden",
                isSelected = false,
                onClick = onRefresh
            )

            SidebarIconItem(
                icon = Icons.Default.Security,
                label = "Tarnmodus",
                isSelected = false,
                activeColor = Color(0xFFEF4444),
                onClick = onPanic
            )
        }
    }
}

@Composable
private fun SidebarIconItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color = NeonCyan
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.2f else 1.0f,
        label = "SidebarScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(
                when {
                    isFocused -> activeColor
                    isSelected -> Color(0x3300F2FE)
                    else -> Color.Transparent
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown && (it.key == Key.DirectionCenter || it.key == Key.Enter)) {
                    onClick()
                    true
                } else false
            }
            .focusable()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = when {
                isFocused -> OledBlack
                isSelected -> activeColor
                else -> TextSecondary
            },
            modifier = Modifier.size(22.dp)
        )
    }
}
