import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val clientIdSpotify = localProperties.getProperty("CLIENT_ID")?:""
val clientSecretSpotify = localProperties.getProperty("CLIENT_SECRET")?:""

android {
    namespace = "org.riza.iluvmusix"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.riza.iluvmusix"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "demo v-1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            // Uses your local machine's debug keystore automatically
        }
    }

    buildTypes {
        android.buildFeatures.buildConfig = true
        release {
            resValue("string", "app_name", "I Luv Musix")
            isMinifyEnabled = false
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "CLIENT_ID", "\"$clientIdSpotify\"")
            buildConfigField("String", "CLIENT_SECRET", "\"$clientSecretSpotify\"")
        }
        debug {
            resValue("string", "app_name", "musix-debug")
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "CLIENT_ID", "\"$clientIdSpotify\"")
            buildConfigField("String", "CLIENT_SECRET", "\"$clientSecretSpotify\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(
                org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
            )
            freeCompilerArgs.add(
                "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode"
            )
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
    implementation(libs.gson)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.logging.interceptor)
    implementation(libs.coil.compose)
    implementation(libs.androidx.palette)

    implementation(libs.exoplayer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}