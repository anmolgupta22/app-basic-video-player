package com.pubscale.basicvideoplayer.data.repository

import com.pubscale.basicvideoplayer.data.remote.VideoApiService
import com.pubscale.basicvideoplayer.domain.model.Video
import com.pubscale.basicvideoplayer.domain.repository.VideoRepository
import com.pubscale.basicvideoplayer.utils.ResultState
import java.io.IOException
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val api: VideoApiService
) : VideoRepository {

    override suspend fun fetchVideoUrl(): ResultState<Video> {
        return try {
            val response = api.getVideoUrl()
            ResultState.Success(response.toDomain())
        } catch (e: IOException) {
            e.printStackTrace()
            ResultState.Error("No internet connection available.")
        } catch (e: Exception) {
            e.printStackTrace()
            ResultState.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}