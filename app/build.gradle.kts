plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pet"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // TODO: Understand what's the meaning of line 17.
        testInstrumentationRunner = "androidx.feeding_textTitle.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("androidx.webkit:webkit:1.8.0")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)
    implementation(libs.legacy.support.v4)
    implementation(libs.activity)
    implementation(libs.preference)
    implementation(libs.firebase.inappmessaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}