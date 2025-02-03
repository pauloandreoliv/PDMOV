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
    implementation(libs.firebase.firestore)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    testImplementation(libs.junit)
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}