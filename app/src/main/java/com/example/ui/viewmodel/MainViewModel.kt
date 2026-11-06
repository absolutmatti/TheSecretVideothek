package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActressCategory
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TVScreen {
    data object StealthSplash : TVScreen
    data object Catalog : TVScreen
    data class ActressDetail(val category: ActressCategory) : TVScreen
    data object Search : TVScreen
    data object Favorites : TVScreen
    data object PanicReset : TVScreen
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application.applicationContext)

    private val _currentScreen = MutableStateFlow<TVScreen>(TVScreen.StealthSplash)
    val currentScreen: StateFlow<TVScreen> = _currentScreen.asStateFlow()

    val categories: StateFlow<List<ActressCategory>> = repository.categories

    private val _focusedVideo = MutableStateFlow<VideoItem?>(null)
    val focusedVideo: StateFlow<VideoItem?> = _focusedVideo.asStateFlow()

    val favorites: StateFlow<Set<String>> = repository.favorites

    val favoriteVideos: StateFlow<List<VideoItem>> = combine(categories, favorites) { cats, favIds ->
        val allVideos = cats.flatMap { it.videos }.distinctBy { it.id }
        allVideos.filter { favIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<VideoItem>> = combine(categories, _searchQuery) { cats, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            cats.flatMap { it.videos }
                .distinctBy { it.id }
                .filter { it.title.lowercase().contains(q) || it.actress.lowercase().contains(q) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeCatalog()
            // Setze das erste Video als initialen Fokus, sobald vorhanden
            if (_focusedVideo.value == null && repository.categories.value.isNotEmpty()) {
                val first = repository.categories.value.firstOrNull()?.videos?.firstOrNull()
                _focusedVideo.value = first
            }
        }
    }

    fun onSplashFinished() {
        if (_currentScreen.value is TVScreen.StealthSplash) {
            _currentScreen.value = TVScreen.Catalog
        }
    }

    fun navigateTo(screen: TVScreen) {
        _currentScreen.value = screen
    }

    fun onVideoFocused(video: VideoItem) {
        _focusedVideo.value = video
    }

    fun openActress(category: ActressCategory) {
        _currentScreen.value = TVScreen.ActressDetail(category)
        if (category.videos.isEmpty()) {
            viewModelScope.launch {
                val loaded = repository.loadActressVideosIfNeeded(category)
                if (_currentScreen.value is TVScreen.ActressDetail) {
                    _currentScreen.value = TVScreen.ActressDetail(loaded)
                }
            }
        }
    }

    fun closeActressDetail() {
        _currentScreen.value = TVScreen.Catalog
    }

    fun toggleFavorite(videoId: String) {
        repository.toggleFavorite(videoId)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun triggerPanicMode() {
        _currentScreen.value = TVScreen.PanicReset
    }

    fun exitPanicMode() {
        _currentScreen.value = TVScreen.Catalog
    }

    fun refreshCatalog() {
        viewModelScope.launch {
            repository.initializeCatalog()
        }
    }
}
