package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Hilfsklasse zur Extraktion von abspielbaren Video-Streams (MP4, HLS/M3U8)
 * aus direkten Links oder Webseiten.
 */
object VideoExtractor {

    private const val TAG = "VideoExtractor"

    // Demo / Test-Stream für Philips OLED TV (Big Buck Bunny 1080p 60fps)
    const val DEMO_STREAM_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    const val DEMO_STREAM_TEARS = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    // Typische Video-Endungen, die direkt von ExoPlayer abgespielt werden können
    private val DIRECT_EXTENSIONS = listOf(".mp4", ".m3u8", ".mpd", ".webm", ".mkv", ".ts")

    // Regex zur Auffindung eingebetteter Video-Quellen in Webseiten
    private val VIDEO_SRC_REGEX = Pattern.compile(
        """(?:source\s+src|video\s+src|file|video_url)["']?\s*[:=]\s*["'](https?://[^"'\s>]+\.(?:mp4|m3u8)[^"'\s>]*)["']""",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Prüft, ob es sich um eine direkte Stream-URL handelt.
     */
    fun isDirectStream(url: String): Boolean {
        val lower = url.lowercase()
        return DIRECT_EXTENSIONS.any { lower.contains(it) }
    }

    /**
     * Versucht, eine abspielbare Stream-URL im Hintergrund zu ermitteln.
     */
    suspend fun resolvePlayableUrl(url: String): String = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext DEMO_STREAM_URL

        if (isDirectStream(url)) {
            return@withContext url
        }

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android TV; Philips 65OLED850; Linux; Android 14) AppleWebKit/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val html = response.body?.string().orEmpty()
                    val matcher = VIDEO_SRC_REGEX.matcher(html)
                    if (matcher.find()) {
                        val extracted = matcher.group(1)
                        if (!extracted.isNullOrBlank()) {
                            Log.d(TAG, "Stream erfolgreich extrahiert: $extracted")
                            return@withContext extracted
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Konnte Stream nicht automatisch extrahieren für: $url (${e.message})")
        }

        // Falls die Extraktion fehlschlägt, geben wir die Original-URL zurück,
        // damit ExoPlayer versucht sie zu laden, oder der Error-Dialog greift
        return@withContext url
    }
}
