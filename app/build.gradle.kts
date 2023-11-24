plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")

    id ("dagger.hilt.android.plugin")

}

android {
    namespace = "com.example.accidentdetectionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.accidentdetectionapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11)) // Set this to 8 to match your existing settings


    }
}




dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation ("com.google.dagger:hilt-android:2.44")
    kapt ("com.google.dagger:hilt-compiler:2.44")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("androidx.navigation:navigation-compose:2.7.5")

    implementation ("androidx.compose.material:material-icons-core:1.5.4")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")

    implementation("androidx.hilt:hilt-navigation-fragment:1.1.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation ("androidx.compose.material3:material3:1.2.0-alpha10")
    implementation ("androidx.compose.material:material:1.5.4")

    implementation ("org.tensorflow:tensorflow-lite:2.4.0")// Use the latest version

    implementation ("org.tensorflow:tensorflow-lite-support:0.1.0")// Use the latest version available

    implementation ("com.cloudinary:cloudinary-android:2.5.0")
    implementation ("androidx.documentfile:documentfile:1.0.1") // Add this line


}