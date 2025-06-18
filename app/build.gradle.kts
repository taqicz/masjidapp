plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.masjidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.masjidapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Core Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.play.services.maps)
    implementation(libs.fragment)
    implementation(libs.firebase.auth)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.google.gms.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-storage:20.3.0")


    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    // Glide Image Loading Library
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    //bumptech
    implementation ("com.loopj.android:android-async-http:1.4.9")
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    implementation ("com.cloudinary:cloudinary-android:2.4.0")

    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("org.ocpsoft.prettytime:prettytime:5.0.7.Final")

}
