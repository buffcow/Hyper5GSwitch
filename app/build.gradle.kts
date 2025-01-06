@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
}

val localProp by lazy {
    Properties().apply {
        rootProject.file("local.properties").takeIf(File::exists)?.let {
            load(it.bufferedReader())
        }
    }
}
var releaseSigningCfg: ApkSigningConfig? = null

android {
    localProp["sign.storeFile"]?.let(::file)?.takeIf { it.exists() }?.let { signFile ->
        signingConfigs {
            create("release") {
                enableV3Signing = true
                storeFile = signFile
                keyAlias = localProp.getProperty("sign.keyAlias")
                keyPassword = localProp.getProperty("sign.keyPassword")
                storePassword = localProp.getProperty("sign.storePassword")
            }.also { releaseSigningCfg = it }
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
        }

        all {
            signingConfig = releaseSigningCfg ?: signingConfigs["debug"]
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
    ksp(libs.ksp.yuki.xposed)
    implementation(libs.api.yuki)
    implementation(libs.androidx.annotation)

    compileOnly(libs.api.xposed)
    compileOnly(files("libs/telephony.jar"))
}
