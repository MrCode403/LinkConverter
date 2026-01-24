
plugins {
    id("com.android.application")
    
}

android {
    namespace = "xyz.illuminate.dlinks"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "xyz.illuminate.dlinks"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"
        
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
    implementation("com.google.android.material:material:1.13.0")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.android.billingclient:billing:8.3.0")

}
