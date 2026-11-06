package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.components.SpatialGlassCard
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * TV Such-Bildschirm mit Schnellwahl für populäre Darstellerinnen und Textsuche.
 */
@Composable
fun TVSearchScreen(
    searchQuery: String,
    searchResults: List<VideoItem>,
    onQueryChanged: (String) -> Unit,
    onVideoSelected: (VideoItem) -> Unit,
    onVideoFocused: (VideoItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val quickFilters = listOf(
        "Adriana Chechik",
        "Alexa Payne",
        "Anissa Kate",
        "Ava Addams",
        "Blake Blossom",
        "Angela White",
        "Alina Lopez"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(horizontal = 48.dp, vertical = 28.dp)
            .testTag("tv_search_screen")
    ) {
        // Kopfzeile mit Zurück-Button & Suchfeld
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x2BFFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                placeholder = { Text("Darstellerin oder Titel suchen...", color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Löschen", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = GlassBorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan,
                    focusedContainerColor = GlassSurfaceDark,
                    unfocusedContainerColor = GlassSurfaceDark
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Schnell-Filter-Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(quickFilters) { filter ->
                var isFocused by remember { mutableStateOf(false) }
                val isSelected = searchQuery.equals(filter, ignoreCase = true)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when {
                                isFocused -> NeonCyan
                                isSelected -> Color(0x3300F2FE)
                                else -> Color(0x1AFFFFFF)
                            }
                        )
                        .border(
                            1.dp,
                            if (isFocused || isSelected) NeonCyan else GlassBorderSubtle,
                            RoundedCornerShape(20.dp)
                        )
                        .onFocusChanged { isFocused = it.isFocused }
                        .onKeyEvent {
                            if (it.type == KeyEventType.KeyDown && (it.key == Key.DirectionCenter || it.key == Key.Enter)) {
                                onQueryChanged(filter)
                                true
                            } else false
                        }
                        .focusable()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onQueryChanged(filter)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isFocused) OledBlack else if (isSelected) NeonCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isFocused || isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ergebnisübersicht
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0x33FFFFFF),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isBlank()) "Wähle eine Darstellerin oder gib einen Suchbegriff ein" else "Keine Treffer für \"$searchQuery\"",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            Text(
                text = "${searchResults.size} Treffer gefunden",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 210.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults, key = { it.id }) { video ->
                    SpatialGlassCard(
                        video = video,
                        onClick = { onVideoSelected(video) },
                        onFocused = { onVideoFocused(video) }
                    )
                }
            }
        }
    }
}
