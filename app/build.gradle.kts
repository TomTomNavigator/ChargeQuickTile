import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val preferenceVersion = "1.2.1"

dependencies {
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
}

android {
    namespace = "de.tebbeubben.chargequicktile"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.tebbeubben.chargequicktile"
        minSdk = 35
        targetSdk = 36
        versionCode = 2
        versionName = "1.4"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}