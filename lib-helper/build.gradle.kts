plugins {
    alias(libs.plugins.agp.lib)
}

android {
    namespace = "cn.buffcow.xp.helper"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    compileOnlyApi(libs.libxposed.api)
    compileOnly(project("libxposed-compat"))
    implementation(libs.commons.lang3)
    implementation(libs.androidx.annotation)
}
