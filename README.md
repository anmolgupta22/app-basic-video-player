Android Video Player Project Documentation

Brief Description

This project follows Clean Architecture with MVVM to ensure modular, scalable, and testable code. It enhances a basic Android video player by implementing Picture-in-Picture (PiP) functionality, dynamically fetching video URLs, and adhering strictly to architectural best practices. The Clean Architecture approach separates concerns into distinct layers, making the app more maintainable.
This project enhances a basic Android video player by implementing Picture-in-Picture (PiP) functionality, dynamically fetching video URLs, and adhering strictly to the MVVM architecture pattern for clean separation of concerns, maintainability, and scalability.
Dynamic Configuration for Video URL in Gradle
To allow dynamic configuration of the video URL, we define it in gradle.properties and use it in build.gradle.kts.
**1. Define the video URL in **gradle.properties
VIDEO_BASE_URL=https://raw.githubusercontent.com/greedyraagava/test/refs/heads/main/
**2. Use it in **build.gradle.kts
android {
    buildFeatures {
        buildConfig = true
    }
}

android {
    defaultConfig {
 buildConfigField(
                "String",
                "VIDEO_BASE_URL",
                "\"${project.findProperty("VIDEO_BASE_URL")}\""
            )
    }
}
Key Implementations
1. Picture-in-Picture (PiP) Mode:
Implemented PiP for seamless video playback when the app is sent to the background.
Used a BroadcastReceiver to handle play and pause actions within PiP mode.
PiP Implementation Snippet:
@RequiresApi(Build.VERSION_CODES.O)
private fun enterPiPMode() {
    val actions = listOf(
        createRemoteAction("Pause", R.drawable.ic_pause, "ACTION_PAUSE"),
        createRemoteAction("Play", R.drawable.ic_play, "ACTION_PLAY")
    )
    val params = PictureInPictureParams.Builder()
        .setAspectRatio(Rational(16, 9))
        .setActions(actions)
        .build()
    enterPictureInPictureMode(params)
}
Dynamic Video Source Fetching:
Utilized Retrofit and OkHttpClient to fetch video URL dynamically from a remote JSON endpoint.
Integrated error handling for network failures and invalid responses.
Video Fetching Snippet:
override suspend fun fetchVideoUrl(): ResultState<Video> {
    return try {
        val response = api.getVideoUrl()
        ResultState.Success(response.toDomain())
    } catch (e: IOException) {
        ResultState.Error("No internet connection available.")
    } catch (e: HttpException) {
        ResultState.Error("Server error: ${e.code()} ${e.message()}")
    }
}
MVVM Architecture:
Model: Managed data fetching and transformation.
ViewModel: Managed UI logic, handling states clearly through StateFlow.
View: Reacted clearly to UI states provided by ViewModel.
ViewModel State Management:
private fun loadVideo() {
    viewModelScope.launch {
        _uiState.value = VideoUiState.Loading
        when (val result = useCaseState.getVideoUrlUseCase()) {
            is ResultState.Success -> _uiState.value = VideoUiState.Success(result.data)
            is ResultState.Error -> _uiState.value = VideoUiState.Error(result.message)
        }
    }
}
Clean Architecture and Project Structure:
This project follows Clean Architecture principles by dividing the codebase into different layers, ensuring separation of concerns and improved maintainability.
1. Presentation Layer
Handles UI-related components such as Activities, Fragments, and ViewModels.
Uses Jetpack ViewModel to store UI-related data.
Observes StateFlow to handle UI updates reactively.
Example: ViewModel Implementation
@HiltViewModel
class VideoViewModel @Inject constructor(
    private val useCaseState: UseCaseState
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    fun onEvent(event: VideoEvent) {
        when (event) {
            is VideoEvent.LoadVideo -> loadVideo()
        }
    }

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
2. Domain Layer
Contains Use Cases, which encapsulate business logic.
Acts as an intermediary between the Presentation Layer and the Data Layer.
Defines interfaces that the Data Layer implements.
Example: Use Case Implementation
class GetVideoUrlUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(): ResultState<Video> {
        return repository.fetchVideoUrl()
    }
}
3. Data Layer
Responsible for fetching data from network sources (Retrofit API service).
Uses Repositories to act as a single source of truth for the data.
Implements Retrofit with OkHttp Interceptors for network requests.
Example: Repository Implementation
class VideoRepositoryImpl @Inject constructor(
    private val api: VideoApiService
) : VideoRepository {
    override suspend fun fetchVideoUrl(): ResultState<Video> {
        return try {
            val response = api.getVideoUrl()
            ResultState.Success(response.toDomain())
        } catch (e: IOException) {
            ResultState.Error("No internet connection available.")
        } catch (e: HttpException) {
            ResultState.Error("Server error: ${e.code()} ${e.message()}")
        }
    }
}
4. Network Interceptor
Uses OkHttp Interceptor to monitor and modify network requests before they are sent.
Handles no internet connectivity errors gracefully.
Example: Custom Network Interceptor
class NetworkConnectionInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable()) {
            throw IOException("No internet connection available")
        }
        return chain.proceed(chain.request())
    }
}
Project Structure Overview
- data (API services, Repositories, Network Interceptor)
- domain (Use Cases, Models)
- presentation (UI, ViewModel, Events)
- utils (Common states, utilities)
This project follows Clean Architecture principles by dividing the codebase into different layers:
Presentation Layer: UI components (Activity, ViewModel) that observe and react to state changes.
Domain Layer: Business logic (Use Cases) that fetch and process data.
Data Layer: Responsible for network requests and data transformations.
Evaluation Criteria Met:
Implemented proper floating mini-player (PiP).
Maintained seamless playback in the background.
Dynamically fetched and integrated video source.
Strict adherence to MVVM architecture.
Clean and well-documented codebase.

Repository Link: https://github.com/anmolgupta22/app-basic-video-player



