buildscript {
    val agp_version by extra("9.0.0")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
         
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}