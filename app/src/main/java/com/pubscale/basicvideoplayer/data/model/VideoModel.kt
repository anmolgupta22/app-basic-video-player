package com.pubscale.basicvideoplayer.data.model

import com.pubscale.basicvideoplayer.domain.model.Video

data class VideoModel(
    val url: String
) {
    fun toDomain() = Video(url)
}