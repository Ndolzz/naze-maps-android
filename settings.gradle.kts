pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // MapLibre Native Android SDK — open source, no API key/payment method required.
        maven { url = uri("https://repo.maplibre.org/repository/maplibre/") }
    }
}

rootProject.name = "NazeMaps"
include(":app")
