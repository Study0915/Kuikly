plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "io.github.study0915.kuiklyfinance.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.study0915.kuiklyfinance"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-reset"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        doNotStrip("**/*.so")
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.kuikly.render.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.material)
}
