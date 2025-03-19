package com.pubscale.basicvideoplayer.presentation.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.pubscale.basicvideoplayer.R
import com.pubscale.basicvideoplayer.databinding.ActivityMainBinding
import com.pubscale.basicvideoplayer.presentation.event.VideoEvent
import com.pubscale.basicvideoplayer.presentation.uiState.VideoUiState
import com.pubscale.basicvideoplayer.presentation.viewModel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: VideoViewModel by viewModels()
    private var player: ExoPlayer? = null

    private val pipReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_PAUSE" -> pauseVideo()
                "ACTION_PLAY" -> playVideo()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupExoPlayer()

        // Trigger the loading of video through ViewModel
        viewModel.onEvent(VideoEvent.LoadVideo)

        // Observe ViewModel's UI state changes
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is VideoUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is VideoUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        playVideo(state.video.url)
                    }

                    is VideoUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showError(state.message)
                    }
                }
            }
        }

        // Register BroadcastReceiver for PiP mode actions
        registerReceiver(pipReceiver, IntentFilter().apply {
            addAction("ACTION_PAUSE")
            addAction("ACTION_PLAY")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pipReceiver) // Unregister receiver to avoid leaks
        player?.release()               // Release ExoPlayer resources
    }

    // Initializes ExoPlayer with looping enabled
    private fun setupExoPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
        }
        binding.playerView.player = player
    }

    // Play video from provided URL
    private fun playVideo(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    // Pause video playback
    private fun pauseVideo() {
        player?.pause()
    }

    // Resume video playback
    private fun playVideo() {
        player?.play()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (player?.isPlaying == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPiPMode()
        } else {
            player?.pause()
        }
    }

    // Enter Picture-in-Picture mode (API 26+)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPiPMode() {
        val pauseIntent = PendingIntent.getBroadcast(
            this, 100,
            Intent("ACTION_PAUSE").setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playIntent = PendingIntent.getBroadcast(
            this, 101,
            Intent("ACTION_PLAY").setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val actions = listOf(
            RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_pause),
                "Pause", "Pause Video", pauseIntent
            ),
            RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_play),
                "Play", "Play Video", playIntent
            )
        )

        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .setActions(actions)
            .build()

        enterPictureInPictureMode(params)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        binding.playerView.useController = !isInPictureInPictureMode
        if (isInPictureInPictureMode) supportActionBar?.hide()
        else supportActionBar?.show()
    }

    override fun onPause() {
        super.onPause()
        if (!isInPictureInPictureMode) player?.pause()
    }

    // Display error messages
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}