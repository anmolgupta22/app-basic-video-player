package com.pubscale.basicvideoplayer.presentation.uiState

import com.pubscale.basicvideoplayer.domain.model.Video

sealed class VideoUiState {
    data object Loading : VideoUiState()
    data class Success(val video: Video) : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}