plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.dontforget"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dontforget"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Kotlin & Coroutines
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.kotlinxCoroutinesAndroid)

    // AndroidX Core
    implementation(libs.coreKtx)

    // Lifecycle + ViewModel
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleViewModelCompose)

    // Compose (with BOM)
        implementation(platform(libs.composeBom))
        implementation(libs.composeUi)
        implementation(libs.composeMaterial3) // ✅ new line
        implementation(libs.composeIcons)
        implementation(libs.composeUiToolingPreview)
        debugImplementation(libs.composeUiTooling)

    implementation(libs.retrofit)
    implementation(libs.retrofitgson)
    implementation(libs.okhttp)
    implementation(libs.okhttplogging)
    implementation(libs.gson)
    implementation(libs.securityCrypto)
    implementation(libs.datastorePreferences)

    // Activity + Navigation
    implementation(libs.activityCompose)
    implementation(libs.navigationCompose)

    // Room (KSP)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    ksp(libs.roomCompiler)

    // WorkManager
    implementation(libs.workRuntimeKtx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}
