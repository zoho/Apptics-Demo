plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("apptics-plugin")
}

android {
    namespace = "com.zoho.apptics.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zoho.apptics.sample"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    apptics {
        showLogs.put("default", true) // To enable Apptics Log

        // Disabled: the plugin's appticsDebugAPIInjection task uses the raw URL
        // string as a Java field name when generating the API-IDs class. If the
        // Apptics console has any URL with characters that aren't valid Java
        // identifiers (e.g. `https://catfact.ninja/fact`), JavaPoet rejects it
        // and the build fails. Flip back to true after cleaning up the network
        // monitoring URL list on the Apptics console (or once the plugin
        // sanitises identifiers).
        generateApiValues.put("default", false)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Apptics Dependency
    implementation(platform(libs.apptics.bom))
    implementation(libs.apptics.analytics)
    implementation(libs.apptics.crash.tracker)
    implementation(libs.apptics.feedback)
    implementation(libs.apptics.ratings)
    implementation(libs.apptics.appupdates)
    implementation(libs.apptics.logger)
    implementation(libs.apptics.rc)
    implementation(libs.apptics.crosspromo)
    // apptics-pns + firebase-messaging require a Firebase project (google-services.json + plugin).
    // Add them — plus `id("com.google.gms.google-services")` — once you've wired up Firebase.
    // implementation(libs.apptics.pns)
    // implementation(libs.firebase.messaging)

    // Network Dependency
    implementation(libs.retrofit)
    debugImplementation(libs.okhttp.logging)
}