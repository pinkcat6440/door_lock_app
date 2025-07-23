plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.androidlab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.myapp.androidlab"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true // MultiDex 활성화
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
    kotlinOptions {
        jvmTarget = "11"
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

    // 테스트
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // MultiDex 라이브러리
    implementation("androidx.multidex:multidex:2.0.1")

    // Firebase (BOM을 사용하여 버전 관리)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0")) // 최신 BOM 버전 확인
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase 인증
    implementation("com.google.firebase:firebase-firestore-ktx") // Firebase Firestore
    implementation("com.google.firebase:firebase-storage-ktx") // Firebase Storage
    implementation("com.google.firebase:firebase-messaging-ktx") // Firebase 메시징 서비스

    // Google Sign-In 라이브러리 (Firebase Auth 사용 시 필요)
    implementation("com.google.android.gms:play-services-auth:21.0.0") // 최신 버전 사용 권장

    // OkHttp (Retrofit에 의해 사용됨, 로깅 인터셉터는 디버그용)
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
}