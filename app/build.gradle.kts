plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Read the API keys directly using Gradle's built-in property system
// Make sure to add your API keys to gradle.properties (not local.properties)
// with the format: API_KEY=your_api_key_here
val geminiApiKey = project.findProperty("GEMINI_API_KEY") as String? ?: ""
val googleSearchApiKey = project.findProperty("GOOGLE_SEARCH_API_KEY") as String? ?: ""
val googleSearchEngineId = project.findProperty("GOOGLE_SEARCH_ENGINE_ID") as String? ?: ""
val googleSearchCloud = project.findProperty("GOOGLE_API_CLOUD") as String? ?: ""
android {
    namespace = "com.example.gaim"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gaim"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Use the API keys from gradle.properties
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "GOOGLE_SEARCH_API_KEY", "\"$googleSearchApiKey\"")
        buildConfigField("String", "GOOGLE_SEARCH_ENGINE_ID", "\"$googleSearchEngineId\"")
        buildConfigField("String", "GOOGLE_API_CLOUD", "\"$googleSearchCloud\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Add packaging options to handle duplicate files
    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/ASL2.0"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "META-INF/INDEX.LIST"
        }
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.cloud:google-cloud-aiplatform:3.53.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.cloud:google-cloud-vision:3.15.0")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
