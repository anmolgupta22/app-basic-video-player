package com.pubscale.basicvideoplayer.domain.usecase

import com.pubscale.basicvideoplayer.domain.model.Video
import com.pubscale.basicvideoplayer.domain.repository.VideoRepository
import com.pubscale.basicvideoplayer.utils.ResultState
import javax.inject.Inject

class GetVideoUrlUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(): ResultState<Video> {
        return repository.fetchVideoUrl()
    }
}