package com.example.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.remote.VideoExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * TV Player Manager auf Basis von AndroidX Media3 (ExoPlayer).
 * Optimiert für Google TV & Philips 65OLED850 Hardware-Decoding.
 */
@OptIn(UnstableApi::class)
class TVPlayerManager(private val context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setSeekBackIncrementMs(10_000)
        .setSeekForwardIncrementMs(10_000)
        .build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(true)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _isBuffering.value = true
                    }
                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                        _errorMessage.value = null
                    }
                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                        _isBuffering.value = false
                    }
                    Player.STATE_IDLE -> {
                        _isBuffering.value = false
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _isBuffering.value = false
                _errorMessage.value = error.localizedMessage ?: "Fehler bei der Videowiedergabe"
            }
        })
    }

    fun prepareAndPlay(streamUrl: String) {
        _errorMessage.value = null
        _isBuffering.value = true
        val mediaItem = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekForward(ms: Long = 10_000) {
        val target = (exoPlayer.currentPosition + ms).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(target)
        _currentPosition.value = target
    }

    fun seekBackward(ms: Long = 10_000) {
        val target = (exoPlayer.currentPosition - ms).coerceAtLeast(0L)
        exoPlayer.seekTo(target)
        _currentPosition.value = target
    }

    fun loadFallbackDemoStream() {
        prepareAndPlay(VideoExtractor.DEMO_STREAM_URL)
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _currentPosition.value = exoPlayer.currentPosition.coerceAtLeast(0L)
                _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                delay(500)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracking()
        exoPlayer.release()
    }
}
