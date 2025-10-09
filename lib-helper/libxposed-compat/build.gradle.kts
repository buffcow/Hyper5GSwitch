plugins {
    alias(libs.plugins.agp.lib)
}

android {
    namespace = "io.github.libxposed.compat"

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
    implementation(libs.commons.lang3)
    implementation(libs.androidx.annotation)
}
