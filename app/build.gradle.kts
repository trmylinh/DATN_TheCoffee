plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.thecoffee"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.thecoffee"
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
    
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // add dependencies
    // material design
    implementation("com.google.android.material:material:1.11.0")

    // circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // card view
    implementation("androidx.cardview:cardview:1.0.0")

    //scalable unit text size
//    implementation("com.intuit.sdp:ssp-android:1.1.0")

    //scalable unit size
    implementation("com.intuit.sdp:sdp-android:1.1.0")


    implementation("com.makeramen:roundedimageview:2.3.0")

    //easy permission
    implementation("pub.devrel:easypermissions:3.0.0")

    //coroutines core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // splash screen api
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // push notification - fcm
    implementation("com.google.firebase:firebase-messaging")
//    implementation("com.google.firebase:firebase-analytics")

    val nav_version = "2.7.6"

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // image slide show
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // ViewModel and LiveData
    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //Kodein Dependency Injection
    implementation("org.kodein.di:kodein-di-generic-jvm:6.2.1")
    implementation("org.kodein.di:kodein-di-framework-android-x:6.2.1")

    //fragment extension
    val fragment_version = "1.4.0"
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // read more - text
    implementation("com.github.colourmoon:readmore-textview:v1.0.2")

    //Notification Badge
    implementation("com.nex3z:notification-badge:1.0.4")

    // event bus
    implementation("org.greenrobot:eventbus:3.3.1")

    //gson
    implementation("com.google.code.gson:gson:2.8.5")

    // swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // swipe view
    implementation("com.chauthai.swipereveallayout:swipe-reveal-layout:1.4.1")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    // volley
    implementation("com.android.volley:volley:1.2.1")

}
kapt {
    correctErrorTypes = true
}