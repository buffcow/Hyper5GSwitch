// Top-level build file where you can add configuration options common to all sub-projects/modules.

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.agp.lib) apply false
}

val gitCommitCount
    get() = kotlin.runCatching {
        ProcessBuilder("git", "rev-list", "--count", "HEAD").start().let { process ->
            process.waitFor()
            process.inputStream.bufferedReader().readText().trim().toInt()
        }
    }.getOrDefault(1)

val gitCommitTag
    get() = kotlin.runCatching {
        ProcessBuilder("git", "describe", "--tags", "--long").start().let { process ->
            process.waitFor()
            val output = process.inputStream.bufferedReader().readText().trim()
            val split = output.split('-')
            if (split.size < 3) throw NullPointerException()
            "${split[0].filter { it.isDigit() || it == '.' }}.${split[1]}"
        }
    }.getOrDefault("1.0.0")

val defaultAppVerCode by extra(gitCommitCount)
val defaultAppVerName by extra(gitCommitTag)
val defaultAppPackageName by extra("cn.buffcow.hyper5g")

val androidMinSdkVersion by extra(24)
val androidTargetSdkVersion by extra(35)
val androidCompileSdkVersion by extra(35)
val androidBuildToolsVersion by extra("35.0.0")
val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)
val kotlinJvmTarget by extra(JvmTarget.JVM_21)


subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            compileSdk = androidCompileSdkVersion
            buildToolsVersion = androidBuildToolsVersion

            defaultConfig {
                minSdk = androidMinSdkVersion
                if (this is ApplicationDefaultConfig) {
                    versionCode = defaultAppVerCode
                    versionName = defaultAppVerName
                    targetSdk = androidTargetSdkVersion
                    applicationId = namespace
                }
                if (this@subprojects.name == "app") namespace = defaultAppPackageName
            }

            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }

            compileOptions {
                sourceCompatibility = androidSourceCompatibility
                targetCompatibility = androidTargetCompatibility
            }
        }
    }

    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    tasks.withType(KotlinCompile::class.java).all {
        compilerOptions {
            jvmTarget = kotlinJvmTarget
        }
    }
}
