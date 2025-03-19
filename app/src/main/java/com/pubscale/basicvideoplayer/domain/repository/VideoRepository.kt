package com.pubscale.basicvideoplayer.domain.repository

import com.pubscale.basicvideoplayer.domain.model.Video
import com.pubscale.basicvideoplayer.utils.ResultState

interface VideoRepository {
    suspend fun fetchVideoUrl(): ResultState<Video>
}