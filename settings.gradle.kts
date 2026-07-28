pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://mirrors.tencent.com/repository/maven-tencent/")
        maven("https://mirrors.tencent.com/nexus/repository/gradle-plugins/")
    }
}

dependencyResolutionManagement {
    // Kotlin/JS's official Yarn resolver adds its distribution repository at
    // project level; allow that repository while keeping the same mirrors in
    // every module.
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://mirrors.tencent.com/repository/maven-tencent/")
    }
}

rootProject.name = "KuiklyFinance"

include(":KuiklyChart")
include(":shared")
include(":androidApp")
include(":h5App")
