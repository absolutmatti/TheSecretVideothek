package com.example.data.model

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Repräsentiert eine Darstellerin mit ihren zugehörigen Videos und dem Profilfoto aus dem Repository.
 */
data class ActressCategory(
    val name: String,
    val count: Int,
    val folderName: String,
    val videos: List<VideoItem> = emptyList(),
    val isLoaded: Boolean = true,
    val photoUrl: String? = null
) {
    /**
     * Ermittelt die primäre Bild-URL für das Darstellerinnen-Foto im GitHub-Repository.
     */
    fun resolvedPhotoUrl(): String {
        if (!photoUrl.isNullOrBlank()) return photoUrl
        val encoded = URLEncoder.encode(folderName, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        return "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/photo.jpg"
    }

    /**
     * Liefert alternative Bild-URLs (z.B. cover.jpg, poster.jpg), falls photo.jpg noch nicht existiert.
     */
    fun candidatePhotoUrls(): List<String> {
        val encoded = URLEncoder.encode(folderName, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        return listOf(
            "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/photo.jpg",
            "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/cover.jpg",
            "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/poster.jpg",
            "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/profile.jpg"
        )
    }
}
