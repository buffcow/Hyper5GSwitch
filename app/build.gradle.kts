@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
}

val properties by lazy {
    rootProject.file("local.properties").takeIf { it.exists() }?.let { f ->
        Properties().apply { load(f.inputStream()) }.takeUnless {
            it.keys().toList().count { k -> k.toString().contains("sign") } < 3
        }
    }
}

android {
    properties?.let { prop ->
        signingConfigs {
            create("release") {
                enableV3Signing = true
                storeFile = file(prop.getProperty("sign.storeFile"))
                keyAlias = prop.getProperty("sign.keyAlias")
                keyPassword = prop.getProperty("sign.storePassword")
                storePassword = prop.getProperty("sign.storePassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.let { configs ->
                configs.runCatching {
                    getByName("release")
                }.getOrDefault(configs.getByName("debug"))
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }

    applicationVariants.all {
        outputs.all {
            val appName = rootProject.name
            val newApkName = "$appName-${versionName}_$versionCode.apk"
            (this as BaseVariantOutputImpl).outputFileName = newApkName
        }
    }
}

dependencies {
    compileOnly(libs.api.xposed)
    compileOnly(files("libs/telephony.jar"))

    ksp(libs.ksp.yuki.xposed)
    implementation(libs.api.yuki)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.annotation)
}
