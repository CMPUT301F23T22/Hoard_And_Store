plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hoard"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.hoard"
        minSdk = 30
        targetSdk = 33
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
}

dependencies {
    implementation("com.github.skydoves:colorpickerview:2.3.0")
    implementation("com.github.blackfizz:eazegraph:1.2.5l@aar")
    implementation("com.nineoldandroids:library:2.4.0")
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-firestore:24.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation ("androidx.activity:activity:1.7.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("org.json:json:20210307")
    implementation("com.google.mlkit:vision-common:16.0.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:16.0.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    var cameraxversion = "1.1.0-beta01"
    implementation ("androidx.camera:camera-camera2:$cameraxversion")
    implementation ("androidx.camera:camera-lifecycle:$cameraxversion")
    implementation ("androidx.camera:camera-view:1.0.0-alpha28")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha28")
    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    implementation("com.google.android.material:material:1.3.0-alpha03")
    implementation ("com.github.skydoves:colorpickerview:2.3.0")
    compileOnly(files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"))
}