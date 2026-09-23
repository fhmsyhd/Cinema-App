import java.util.Properties

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

val tmdbApiKey = providers.environmentVariable("TMDB_API_KEY").orNull
    ?: localProperties.getProperty("TMDB_API_KEY")
    ?: ""

val escapedTmdbApiKey = tmdbApiKey
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.fhmsyhd.cinema.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "TMDB_API_KEY", "\"$escapedTmdbApiKey\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // ROOM
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // RETROFIT & NETWORKING
    api(libs.retrofit)
    api(libs.converter.gson)

    // COROUTINES
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    // HILT
    api(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // CORE
    api(libs.androidx.core.ktx)

    // UNIT TESTING
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)

    // INSTRUMENTED TESTING
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
