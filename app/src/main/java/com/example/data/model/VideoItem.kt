package com.example.data.model

/**
 * Repräsentiert ein einzelnes Video aus der Link-Sammlung.
 */
data class VideoItem(
    val id: String,
    val title: String,
    val url: String,
    val actress: String,
    val category: String = "Video",
    val durationText: String = "HD 1080p",
    val isFavorite: Boolean = false,
    val posterUrl: String? = null,
    val streamUrl: String? = null
)
