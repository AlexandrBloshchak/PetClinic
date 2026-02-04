plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

android {
    namespace = "com.example.petclinic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.petclinic"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = false
        viewBinding = true
        dataBinding = true
    }
}

kotlin {
    jvmToolchain(17)
}

/**
 * Фикс ошибки Duplicate classes:
 * com.intellij:annotations:12.0 конфликтует с org.jetbrains:annotations:23.x
 */
configurations.configureEach {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // RecyclerView + ListAdapter + DiffUtil + LayoutManager
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // lifecycleScope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // viewModels() + viewModelScope
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room (KSP)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
