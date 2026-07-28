plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        moduleName = "h5App"
        browser {
            webpackTask {
                outputFileName = "h5App.js"
            }
            commonWebpackConfig {
                output?.library = null
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.kuikly.core)
            implementation(libs.kuikly.render.web.base)
            implementation(libs.kuikly.render.web.h5)
        }
    }
}
