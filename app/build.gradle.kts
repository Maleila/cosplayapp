plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "us.cosplayapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "us.cosplayapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    //implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    val nav_version = "2.8.6"
    implementation("androidx.navigation:navigation-runtime-ktx:$nav_version")
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.compose.material:material-icons-extended:1.7.7")

    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation("io.coil-kt:coil-compose:2.1.0")

    //implementation("androidx.compose.material3:material3:1.2.0-alpha05")
    implementation("androidx.compose.material3:material3:1.4.0-alpha07")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.7")
    implementation("com.google.firebase:firebase-storage:21.0.1")

//    // Import the BoM for the Firebase platform
//    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
//
//    // Add the dependency for the Cloud Storage library
//    // When using the BoM, you don't specify versions in Firebase library dependencies
//    implementation("com.google.firebase:firebase-storage")
}