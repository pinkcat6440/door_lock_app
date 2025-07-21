plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.ch20_firebase"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ch20_firebase"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🌟 2. Multidex 활성화
        multiDexEnabled = true // 이 줄 추가
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
        // jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // MultiDex 라이브러리 추가
    implementation("androidx.multidex:multidex:2.0.1") // 이 줄 추가
    implementation("com.google.firebase:firebase-analytics")

    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    // Firebase 인증 (FirebaseAuth)
    implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Firestore (FirebaseFirestore)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Storage (FirebaseStorage)
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Sign-In 라이브러리 추가
    implementation("com.google.android.gms:play-services-auth:21.0.0") // 최신 버전 사용 권장

    implementation("com.google.firebase:firebase-messaging-ktx") // MyFirebaseMessageService 사용을 위해

}
