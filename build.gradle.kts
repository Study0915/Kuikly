plugins {
    kotlin("multiplatform") version "2.0.21" apply false
    kotlin("android") version "2.0.21" apply false
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://mirrors.tencent.com/repository/maven-tencent/")
    }
    dependencies {
        classpath("com.tencent.kuikly-open:core-gradle-plugin:2.4.0-2.0.21")
    }
}

allprojects {
repositories {
    google()
    mavenCentral()
    maven("https://mirrors.tencent.com/repository/maven-tencent/")
}
group = "io.github.study0915"
version = "0.1.0"

// Local Windows verification reuses the project-scoped Node/Yarn runtime.
// Do not apply these Windows-only executable paths on Linux CI: Kotlin/JS then
// provisions its own compatible runtime in GRADLE_USER_HOME.
if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    gradle.projectsEvaluated {
        rootProject.extensions.findByName("kotlinNodeJs")?.let { nodeExtension ->
            nodeExtension.javaClass.getMethod("setDownload", Boolean::class.javaPrimitiveType).invoke(nodeExtension, false)
            nodeExtension.javaClass.getMethod("setNodeCommand", String::class.java).invoke(
                nodeExtension,
                rootProject.file(".cache/node/node.exe").absolutePath,
            )
        }
        rootProject.extensions.findByName("kotlinYarn")?.let { yarnExtension ->
            yarnExtension.javaClass.getMethod("setDownload", Boolean::class.javaPrimitiveType).invoke(yarnExtension, false)
            yarnExtension.javaClass.getMethod("setCommand", String::class.java).invoke(
                yarnExtension,
                rootProject.file(".cache/npm/yarn-runtime/node_modules/.bin/yarn.cmd").absolutePath,
            )
        }
    }
}
}

tasks.register<Delete>("cleanAll") {
    delete(rootProject.layout.buildDirectory)
}
