import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    //id("com.google.devtools.ksp")
    // alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.egnss4coffeev2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.egnss4coffeev2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // Load BASE_URL from local.properties
            val baseUrl = project.rootProject.file("local.properties").let { propertiesFile ->
                if (propertiesFile.exists()) {
                    val properties = Properties()
                    properties.load(propertiesFile.inputStream())
                    properties.getProperty("BASE_URL") ?: "default_debug_url"
                } else {
                    "default_debug_url"
                }
            }
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // Load BASE_URL from local.properties
            val baseUrl = project.rootProject.file("local.properties").let { propertiesFile ->
                if (propertiesFile.exists()) {
                    val properties = Properties()
                    properties.load(propertiesFile.inputStream())
                    properties.getProperty("BASE_URL") ?: "default_release_url"
                } else {
                    "default_release_url"
                }
            }
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
//    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.documentfile)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.maps.compose)
    implementation(libs.google.accompanist.permissions)
    implementation(libs.gson)
    implementation(libs.net.android.joda)
    implementation(libs.hilt.android)
    implementation(libs.androidx.work.runtime.ktx)
//    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    kapt(libs.hilt.compiler)
    implementation(libs.gms.play.services.ads.identifier)
    implementation(libs.maps.ktx)
    implementation(libs.maps.utils.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    kapt(libs.androidx.room.compiler)

//
//    implementation(libs.google.accompanist.pager)
//    implementation(libs.accompanist.pager.indicators)


    //ksp(libs.androidx.room.compiler)  // Correctly added KSP dependency


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("script-runtime"))
}
