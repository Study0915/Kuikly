import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuikly-open.kuikly")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    js(IR) {
        moduleName = "finance"
        browser {
            webpackTask {
                outputFileName = "finance.js"
            }
            commonWebpackConfig {
                output?.library = null
                devtool = "source-map"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":KuiklyChart"))
            implementation(libs.kuikly.core)
            implementation(libs.kuikly.annotations)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

ksp {
    arg("pageName", providers.gradleProperty("pageName").orNull ?: "")
    arg("packLocalJsBundle", providers.gradleProperty("packLocalJsBundle").orNull ?: "")
}

dependencies {
    compileOnly(libs.kuikly.ksp) {
        add("kspAndroid", this)
        add("kspJs", this)
    }
}

android {
    namespace = "io.github.study0915.kuiklyfinance.shared"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
}

kuikly {
    js {
        outputName("finance")
    }
}
