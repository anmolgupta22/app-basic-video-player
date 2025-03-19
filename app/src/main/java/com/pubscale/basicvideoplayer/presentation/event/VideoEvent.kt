package com.pubscale.basicvideoplayer.presentation.event

sealed class VideoEvent {
    data object LoadVideo : VideoEvent()
}