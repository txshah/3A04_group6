plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.tensorflow:tensorflow-core-platform:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-onnx:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-visualization:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:0.5.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    implementation("io.ktor:ktor-server-core:1.6.6")
    implementation("io.ktor:ktor-server-netty:1.6.6")
    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("io.ktor:ktor-client-core:1.6.6")
    implementation("io.ktor:ktor-client-cio:1.6.6")
    implementation("io.ktor:ktor-client-logging:1.6.6")
    implementation("io.ktor:ktor-html-builder:1.6.6")
    implementation("com.google.mlkit:image-labeling:17.0.7")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
