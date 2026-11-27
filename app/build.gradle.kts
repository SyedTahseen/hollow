import java.net.HttpURLConnection
import java.net.URI

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}

val isReleaseOrBundleTask = gradle.startParameter.taskNames.any {
  it.contains("Release", ignoreCase = true) || it.contains("bundle", ignoreCase = true)
}

val buildNumberFile = rootProject.file("build_number.properties")
val currentBuildNumber = buildNumberFile.takeIf { it.isFile }
  ?.readText()
  ?.trim()
  ?.toIntOrNull()
  ?: 0
val buildNumber = currentBuildNumber + if (isReleaseOrBundleTask) 1 else 0
if (isReleaseOrBundleTask) {
  buildNumberFile.writeText(buildNumber.toString())
}

val shippedAbis: List<String> = providers.gradleProperty("abiOnly").orNull
  ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
  ?: listOf("armeabi-v7a", "arm64-v8a")
val skippedAbis = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64") - shippedAbis.toSet()

android {
  namespace = "com.hollow.vpn"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.hllow.vpn.snow"
    minSdk = 24
    targetSdk = 36
    if (!isReleaseOrBundleTask) {
      ndk {
        abiFilters += listOf("arm64-v8a")
      }
    }
    versionCode = buildNumber.coerceAtLeast(1)
    versionName = "1.0.$buildNumber"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  splits {
    abi {
      isEnable = isReleaseOrBundleTask
      reset()
      include(*shippedAbis.toTypedArray())
      isUniversalApk = false
    }
  }

  signingConfigs {
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
    val keystorePath = System.getenv("KEYSTORE_PATH")
    val storePasswordValue = System.getenv("STORE_PASSWORD")
    val keyAliasValue = System.getenv("KEY_ALIAS") ?: "upload"
    val keyPasswordValue = System.getenv("KEY_PASSWORD")
    if (!keystorePath.isNullOrBlank() && !storePasswordValue.isNullOrBlank() && !keyPasswordValue.isNullOrBlank()) {
      create("release") {
        storeFile = file(keystorePath)
        storePassword = storePasswordValue
        keyAlias = keyAliasValue
        keyPassword = keyPasswordValue
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debugConfig")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all {
        it.maxParallelForks = 1
        it.systemProperty("java.awt.headless", "true")
      }
    }
  }
  packaging {
    jniLibs {
      if (isReleaseOrBundleTask) excludes += skippedAbis.map { "**/$it/**" }
      useLegacyPackaging = true
    }
    resources {
      excludes += listOf(
        "/META-INF/{AL2.0,LGPL2.1}",
        "/META-INF/*.version",
        "/META-INF/LICENSE*",
        "/META-INF/NOTICE*",
        "/META-INF/DEPENDENCIES",
        "/DebugProbesKt.bin",
        "/kotlin-tooling-metadata.json",
        "/kotlin/**",
        "**/*.kotlin_builtins",
        "**/*.kotlin_module"
      )
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

val libV2rayFile = layout.projectDirectory.file("libs/libv2ray.aar").asFile
if (!libV2rayFile.exists() || libV2rayFile.length() < 1024L) {
  libV2rayFile.parentFile.mkdirs()
  val releaseUrl = "https://github.com/2dust/AndroidLibXrayLite/releases/download/v26.10.31/libv2ray.aar"
  logger.lifecycle("Downloading libv2ray.aar (v26.10.31) from $releaseUrl ...")
  var currentUrl = releaseUrl
  var connection: HttpURLConnection
  var hops = 0
  while (true) {
    connection = (URI(currentUrl).toURL().openConnection() as HttpURLConnection).apply {
      instanceFollowRedirects = false
      connectTimeout = 15_000
      readTimeout = 60_000
      setRequestProperty("User-Agent", "HollowVPN-Gradle/1.0")
    }
    val code = connection.responseCode
    if (code in 300..399 && hops < 6) {
      val next = connection.getHeaderField("Location") ?: error("Redirect without Location header for $currentUrl")
      connection.disconnect()
      currentUrl = URI(currentUrl).resolve(next).toString()
      hops++
    } else {
      break
    }
  }
  if (connection.responseCode !in 200..299) {
    val msg = "Failed to download libv2ray.aar (HTTP ${connection.responseCode})"
    connection.disconnect()
    error(msg)
  }
  val tempFile = file("${libV2rayFile.absolutePath}.tmp")
  connection.inputStream.use { input ->
    tempFile.outputStream().use { output -> input.copyTo(output) }
  }
  connection.disconnect()
  if (!tempFile.renameTo(libV2rayFile)) {
    tempFile.copyTo(libV2rayFile, overwrite = true)
    tempFile.delete()
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.zxing.core)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.okhttp)
  testImplementation(libs.androidx.core)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
}
