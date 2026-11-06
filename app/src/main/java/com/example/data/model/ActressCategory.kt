package com.example.data.model

/**
 * Repräsentiert eine Darstellerin / Kategorie mit ihren zugehörigen Videos.
 */
data class ActressCategory(
    val name: String,
    val count: Int,
    val folderName: String,
    val videos: List<VideoItem> = emptyList(),
    val isLoaded: Boolean = true
)
