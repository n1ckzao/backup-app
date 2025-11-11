plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.app_journey"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app_journey"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM (Bill of Materials) - Gerencia as versões automaticamente
    implementation(platform(libs.androidx.compose.bom))
    
    // Dependências básicas do Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    
    // Material3 (inclui shape e outros componentes)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    
    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended)
    
    // Material Design (para compatibilidade com componentes legados)
    implementation("androidx.compose.material:material:1.6.6")
    implementation("com.google.android.material:material:${libs.versions.material.get()}")
    
    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:${libs.versions.composeCompiler.get()}")
    
    // Material3 Adaptive
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation)
    
    // Coil for image loading
    implementation(libs.coil.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Firebase
    implementation(libs.firebase.auth)
    
    // Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    
    // Retrofit & Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.code.gson:gson:2.9.0")
    
    // Socket.IO
    implementation("io.socket:socket.io-client:2.1.0") {
        exclude(group = "org.json", module = "json")
    }
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // Window Manager
    implementation(libs.androidx.window)
    implementation(libs.androidx.window.java)
}