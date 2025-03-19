package com.pubscale.basicvideoplayer.data.remote

import com.pubscale.basicvideoplayer.data.model.VideoModel
import retrofit2.http.GET

interface VideoApiService {
    @GET("video_url.json")
    suspend fun getVideoUrl(): VideoModel
}