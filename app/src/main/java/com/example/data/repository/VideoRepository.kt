package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.data.remote.MarkdownParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Repository zur Verwaltung von Kategorien, Darstellerinnen und Videos.
 * Unterstützt dynamisches Nachladen aus dem GitHub-Repository
 * sowie Offline-Caching für schnellen TV-Start.
 */
class VideoRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("secret_videothek_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _categories = MutableStateFlow<List<ActressCategory>>(emptyList())
    val categories: StateFlow<List<ActressCategory>> = _categories.asStateFlow()

    private val _allActressesNames = MutableStateFlow<List<String>>(emptyList())
    val allActressesNames: StateFlow<List<String>> = _allActressesNames.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStoredFavorites()
    }

    private fun loadStoredFavorites() {
        val stored = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        _favorites.value = stored
    }

    /**
     * Schaltet den Favoriten-Status eines Videos um.
     */
    fun toggleFavorite(videoId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(videoId)) {
            current.remove(videoId)
        } else {
            current.add(videoId)
        }
        _favorites.value = current
        prefs.edit().putStringSet("favorite_ids", current).apply()

        // Aktualisiere gecachte Kategorien mit Favorite-Status
        _categories.value = _categories.value.map { cat ->
            cat.copy(videos = cat.videos.map { v ->
                v.copy(isFavorite = current.contains(v.id))
            })
        }
    }

    /**
     * Initialisiert den Datenbestand aus den Assets und lädt anschließend frische Daten.
     */
    suspend fun initializeCatalog() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            // 1. Lade alle 196 Darstellerinnen-Namen aus den Assets
            loadActressesIndex()

            // 2. Lade den vorbereiteten Snapshot der Top-Kategorien
            val initialCategories = loadBundledCache()
            val initialMap = initialCategories.associateBy { it.name }
            val favs = _favorites.value

            // 3. Führe alle 196 Darstellerinnen zusammen
            val allList = _allActressesNames.value.map { name ->
                val cached = initialMap[name]
                if (cached != null) {
                    cached.copy(videos = cached.videos.map { v -> v.copy(isFavorite = favs.contains(v.id)) })
                } else {
                    ActressCategory(
                        name = name,
                        count = 0,
                        folderName = name,
                        videos = emptyList(),
                        isLoaded = false
                    )
                }
            }.sortedBy { it.name }

            _categories.value = allList

            // 4. Ergänze im Hintergrund weitere Kategorien aus dem GitHub Repo
            fetchMissingCategoriesFromGitHub()
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Initialisieren des Katalogs: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Lädt bei Bedarf die Videos einer Darstellerin nach, falls sie noch nicht im Cache sind.
     */
    suspend fun loadActressVideosIfNeeded(category: ActressCategory): ActressCategory = withContext(Dispatchers.IO) {
        if (category.videos.isNotEmpty()) {
            return@withContext category
        }
        val loaded = fetchActressFromGitHub(category.name)
        if (loaded != null) {
            val favs = _favorites.value
            val updated = loaded.copy(videos = loaded.videos.map { v -> v.copy(isFavorite = favs.contains(v.id)) })
            val current = _categories.value.toMutableList()
            val index = current.indexOfFirst { it.name == category.name }
            if (index >= 0) {
                current[index] = updated
                _categories.value = current
            }
            return@withContext updated
        }
        return@withContext category
    }

    private fun loadActressesIndex() {
        try {
            val jsonString = context.assets.open("actresses_index.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonString)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            _allActressesNames.value = list
        } catch (e: Exception) {
            Log.w(TAG, "Konnte actresses_index.json nicht laden: ${e.message}")
        }
    }

    private fun loadBundledCache(): List<ActressCategory> {
        val list = mutableListOf<ActressCategory>()
        try {
            val jsonString = context.assets.open("catalog_cache.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val actress = obj.getString("actress")
                val count = obj.optInt("count", 0)
                val videosArray = obj.getJSONArray("videos")
                val videos = mutableListOf<VideoItem>()
                for (j in 0 until videosArray.length()) {
                    val vObj = videosArray.getJSONObject(j)
                    videos.add(
                        VideoItem(
                            id = vObj.getString("id"),
                            title = vObj.getString("title"),
                            url = vObj.getString("url"),
                            actress = vObj.getString("actress"),
                            category = vObj.optString("category", "Video"),
                            durationText = "HD Stream"
                        )
                    )
                }
                list.add(
                    ActressCategory(
                        name = actress,
                        count = count,
                        folderName = actress,
                        videos = videos,
                        isLoaded = true
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Konnte catalog_cache.json nicht parsen: ${e.message}")
        }
        return list
    }

    /**
     * Lädt gezielt eine Darstellerin live aus dem GitHub Repository nach.
     */
    suspend fun fetchActressFromGitHub(actressName: String): ActressCategory? = withContext(Dispatchers.IO) {
        try {
            val encoded = URLEncoder.encode(actressName, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
            val url = "https://raw.githubusercontent.com/absolutmatti/secret-videothek-/main/$encoded/README.md"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SecretVideothek-AndroidTV")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val markdown = response.body?.string().orEmpty()
                    val category = MarkdownParser.parseReadme(actressName, markdown)
                    return@withContext category
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fehler beim Laden von $actressName aus GitHub: ${e.message}")
        }
        return@withContext null
    }

    private suspend fun fetchMissingCategoriesFromGitHub() = withContext(Dispatchers.IO) {
        val existingNames = _categories.value.map { it.name }.toSet()
        val toFetch = _allActressesNames.value.filter { !existingNames.contains(it) }.take(15)

        for (name in toFetch) {
            val loaded = fetchActressFromGitHub(name)
            if (loaded != null && loaded.videos.isNotEmpty()) {
                val current = _categories.value.toMutableList()
                current.add(loaded)
                _categories.value = current
            }
        }
    }

    companion object {
        private const val TAG = "VideoRepository"
    }
}
