plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.pubscale.basicvideoplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pubscale.basicvideoplayer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "VIDEO_BASE_URL",
                "\"${project.findProperty("VIDEO_BASE_URL")}\""
            )
        }
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "VIDEO_BASE_URL",
                "\"${project.findProperty("VIDEO_BASE_URL")}\""
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp for Networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Dagger Hilt for Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Kotlin Coroutines for Background Tasks
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle (ViewModel & LiveData)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Navigation Component (if using Jetpack Navigation)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Picture-in-Picture (PiP) Support
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // ExoPlayer for Video Playback
    implementation(libs.exoplayer)

    // Hilt Android Compiler (Kotlin Annotation Processing)
    kapt(libs.androidx.hilt.compiler)

    // Latest Media3 ExoPlayer & UI dependencies
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
}