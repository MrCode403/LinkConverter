
plugins {
    id("com.android.application")
    
}

android {
    namespace = "xyz.illuminate.dlinks"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "xyz.illuminate.dlinks"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
        viewBinding = true
        
    }
    
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jsoup:jsoup:1.17.2")
}
