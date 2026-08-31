import io.gitlab.arturbosch.detekt.Detekt

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.room)
  alias(libs.plugins.detekt)
  // Apply these once google-services.json is added (Firebase project connected):
  // alias(libs.plugins.google.services)
  // alias(libs.plugins.firebase.crashlytics)
  // alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.example.valtiva"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.example.valtiva"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = false
      shaders = false
    }

    lint {
      abortOnError = true
      warningsAsErrors = false
      checkDependencies = true
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

room {
  schemaDirectory("$projectDir/schemas")
}

detekt {
  buildUponDefaultConfig = true
  config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = "17"
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components (ViewModel, lifecycle-aware Flow collection)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose (versions managed by the BOM)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.extended)
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Navigation
  implementation(libs.androidx.navigation.compose)

  // Coroutines / serialization
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)

  // Dependency injection (Hilt)
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.androidx.hilt.navigation.compose)

  // Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  // Networking
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.kotlinx.serialization)
  implementation(libs.okhttp)
  debugImplementation(libs.okhttp.logging.interceptor)

  // WorkManager (+ Hilt integration)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  ksp(libs.androidx.hilt.compiler)

  // Firebase (versions managed by the BoM)
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.perf)

  // Local unit tests
  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.turbine)
  testImplementation(libs.kotlinx.coroutines.test)
  // Exercises the Retrofit interface and the JSON converter without hitting the network.
  testImplementation(libs.okhttp.mockwebserver)
  // Room's builders all require a Context, so DAO tests run on Robolectric instead of an emulator.
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.room.testing)
  testImplementation(libs.androidx.test.core)
  // Validates that Hilt can materialize the production graph; Dagger only checks requested
  // bindings at compile time, and nothing in the app requests the repositories yet.
  testImplementation(libs.hilt.android.testing)
  kspTest(libs.hilt.compiler)
  // Builds and runs a Worker outside a real WorkManager instance, and lets tests assert on what
  // was actually enqueued.
  testImplementation(libs.androidx.work.testing)

  // Instrumented tests
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
