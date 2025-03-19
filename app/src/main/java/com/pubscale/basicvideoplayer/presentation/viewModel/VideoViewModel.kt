package com.pubscale.basicvideoplayer.presentation.viewModel

import com.pubscale.basicvideoplayer.presentation.event.VideoEvent
import com.pubscale.basicvideoplayer.presentation.uiState.VideoUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pubscale.basicvideoplayer.domain.usecasestate.UseCaseState
import com.pubscale.basicvideoplayer.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val useCaseState: UseCaseState
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    // Handles events from the UI
    fun onEvent(event: VideoEvent) {
        when (event) {
            is VideoEvent.LoadVideo -> loadVideo()
        }
    }

    // Loads video data asynchronously using the provided use case and handles ResultState
    private fun loadVideo() {
        viewModelScope.launch {
            _uiState.value = VideoUiState.Loading
            when (val result = useCaseState.getVideoUrlUseCase()) {
                is ResultState.Success -> _uiState.value = VideoUiState.Success(result.data)
                is ResultState.Error -> _uiState.value = VideoUiState.Error(result.message)
            }
        }
    }
}
