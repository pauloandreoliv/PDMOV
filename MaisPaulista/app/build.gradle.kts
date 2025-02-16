plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    //connection firebase
    id("com.google.gms.google-services")

}

android {
    namespace = "com.projeto.maispaulista"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.projeto.maispaulista"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.android.material:material:1.3.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.0")
    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")

    implementation ("com.google.guava:guava:31.0.1-android")

}