
plugins {
    id("com.android.application")
    
}

android {
    namespace = "moe.zl.freelauncher"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "moe.zl.freelauncher"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {

    compileOnly("de.robv.android.xposed:api:82")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.14.0-alpha03")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.crossbowffs.remotepreferences:remotepreferences:0.8")
    
}
