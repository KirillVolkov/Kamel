import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    multiplatform
    compose
    `android-application`
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources {
            excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
        }
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        }
    }

    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
        named("implementation") {
            exclude(group = "androidx.compose.animation")
            exclude(group = "androidx.compose.foundation")
            exclude(group = "androidx.compose.material")
            exclude(group = "androidx.compose.runtime")
            exclude(group = "androidx.compose.ui")
        }
    }
}

kotlin {

    explicitApi = ExplicitApiMode.Warning

    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(project(":kamel-image"))
                implementation(project(":kamel-tests"))
                implementation(compose.material)
                implementation(compose.animation)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Android.Appcompat)
                implementation(Dependencies.Android.Core)
                implementation(Dependencies.Android.ActivityCompose)
                implementation(Dependencies.Android.Material)
                implementation(Dependencies.Ktor.Android)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(Dependencies.Ktor.CIO)
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.Experimental")
            }
        }

        targets.all {
            compilations.all {
                kotlinOptions {
                    freeCompilerArgs =
                        listOf("-Xopt-in=kotlin.RequiresOptIn")
                }
            }
        }

    }
}

compose {
    desktop {
        application {
            mainClass = "io.kamel.samples.DesktopSampleKt"
        }
    }
}