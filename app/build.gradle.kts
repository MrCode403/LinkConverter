
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "xyz.illuminate.dlinks"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "xyz.illuminate.dlinks"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "6.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    
    signingConfigs {
         getByName("debug") {
            storeFile = file("config/Falcon.jks")
            storePassword = "Falcon@69"
            keyAlias = "Falcon"
            keyPassword = "Falcon@69"
         }
         create("release") {
            storeFile = file("config/Falcon.jks")
            storePassword = "Falcon@69"
            keyAlias = "Falcon"
            keyPassword = "Falcon@69"
         }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false 
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    
    // Material 3
    implementation(libs.material3)
    
    // Jsoup for link parsing
    implementation(libs.jsoup)
    
    // Billing
    implementation(libs.billing)
    implementation(libs.billing.ktx)
}
