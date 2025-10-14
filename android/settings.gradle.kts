pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.name == "apptics-plugin") {
                useModule("com.zoho.apptics:apptics-plugin:${requested.version}")
            }
        }
    }
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://maven.zohodl.com/")
            content {
                includeGroup("com.zoho.apptics")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://maven.zohodl.com/")
            content {
                includeGroup("com.zoho.apptics")
            }
        }
    }
}

rootProject.name = "Apptics Android"
include(":app")
