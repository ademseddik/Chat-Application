plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}




android {
    namespace = "tn.esprit.androidapplicationtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.esprit.androidapplicationtest"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            viewBinding = true
        }


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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation ("io.socket:socket.io-client:2.0.0")
    //implementation ("com.github.AgoraIO-Community:VideoUIKit-Android:v4.0.0")
    // Agora
   // implementation ("com.github.AgoraIO-Community:Android-UIKit:v4.0.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.android.identity:identity-credential:20231002")
    implementation(files("D:\\chatsdkbackend\\AndroidProjectTest\\app\\build\\outputs\\apk\\debug\\app-debug.apk"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}