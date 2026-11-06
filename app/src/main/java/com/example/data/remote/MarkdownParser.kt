package com.example.data.remote

import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import java.util.regex.Pattern

/**
 * Parser für README.md und links.md aus dem GitHub Repository.
 * Erkennt die Markdown-Syntax:
 * ## Name der Darstellerin
 * **X Links**
 * ## Links
 * - [Linktitel](URL)
 */
object MarkdownParser {

    private val LINK_PATTERN = Pattern.compile("-\\s*\\[(.*?)\\]\\((.*?)\\)")
    private val TITLE_PATTERN = Pattern.compile("^#{1,3}\\s*(.*?)$", Pattern.MULTILINE)
    private val COUNT_PATTERN = Pattern.compile("\\*\\*(\\d+)\\s*Links?\\*\\*", Pattern.CASE_INSENSITIVE)

    /**
     * Parst den Inhalt einer README.md Datei in eine [ActressCategory].
     */
    fun parseReadme(actressName: String, markdownContent: String): ActressCategory {
        val videos = mutableListOf<VideoItem>()

        val matcher = LINK_PATTERN.matcher(markdownContent)
        var index = 0
        while (matcher.find()) {
            val title = matcher.group(1)?.trim() ?: "Video #${index + 1}"
            val url = matcher.group(2)?.trim() ?: ""
            if (url.isNotEmpty()) {
                val cleanId = "${actressName.lowercase().replace(" ", "_")}_$index"
                videos.add(
                    VideoItem(
                        id = cleanId,
                        title = title,
                        url = url,
                        actress = actressName,
                        category = "Video",
                        durationText = "HD Stream"
                    )
                )
                index++
            }
        }

        // Ermittle gezählte Links
        val countMatcher = COUNT_PATTERN.matcher(markdownContent)
        val parsedCount = if (countMatcher.find()) {
            countMatcher.group(1)?.toIntOrNull() ?: videos.size
        } else {
            videos.size
        }

        return ActressCategory(
            name = actressName,
            count = if (parsedCount > 0) parsedCount else videos.size,
            folderName = actressName,
            videos = videos,
            isLoaded = true
        )
    }

    /**
     * Parst eine links.md Datei (reine URLs zeilenweise).
     */
    fun parseLinksMd(actressName: String, linksContent: String): List<VideoItem> {
        val lines = linksContent.lines().map { it.trim() }.filter { it.startsWith("http") }
        return lines.mapIndexed { index, url ->
            VideoItem(
                id = "${actressName.lowercase().replace(" ", "_")}_l_$index",
                title = "$actressName - Video ${index + 1}",
                url = url,
                actress = actressName,
                durationText = "Web Stream"
            )
        }
    }
}
