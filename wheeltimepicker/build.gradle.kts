plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

val publishGroupId = (findProperty("PUBLISH_GROUP_ID") as String?) ?: "io.github.seojw0124"
val publishArtifactId = (findProperty("PUBLISH_ARTIFACT_ID") as String?) ?: "wheeltimepicker"
val publishVersion = (findProperty("PUBLISH_VERSION") as String?) ?: "0.1.0"

val pomName = (findProperty("POM_NAME") as String?) ?: "WheelTimePicker"
val pomDescription =
    (findProperty("POM_DESCRIPTION") as String?)
        ?: "Composable wheel time picker with optional fade and cylinder effects."
val pomUrl = (findProperty("POM_URL") as String?) ?: "https://github.com/seojw0124/compose-wheel-time-picker.git"
val pomLicenseName = (findProperty("POM_LICENSE_NAME") as String?) ?: "The Apache License, Version 2.0"
val pomLicenseUrl =
    (findProperty("POM_LICENSE_URL") as String?) ?: "https://www.apache.org/licenses/LICENSE-2.0.txt"
val pomDeveloperId = (findProperty("POM_DEVELOPER_ID") as String?) ?: "seojw0124"
val pomDeveloperName = (findProperty("POM_DEVELOPER_NAME") as String?) ?: "Seojeong U"
val pomDeveloperUrl =
    (findProperty("POM_DEVELOPER_URL") as String?) ?: "https://github.com/seojw0124"
val pomScmUrl = (findProperty("POM_SCM_URL") as String?) ?: "https://github.com/seojw0124/compose-wheel-time-picker.git"
val pomScmConnection =
    (findProperty("POM_SCM_CONNECTION") as String?)
        ?: "scm:git:git://github.com/seojw0124/compose-wheel-time-picker.git"
val pomScmDevConnection =
    (findProperty("POM_SCM_DEV_CONNECTION") as String?)
        ?: "scm:git:ssh://git@github.com/seojw0124/compose-wheel-time-picker.git"

android {
    namespace = "com.jade.wheeltimepicker.library"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.immutable)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(publishGroupId, publishArtifactId, publishVersion)

    pom {
        name.set(pomName)
        description.set(pomDescription)
        url.set(pomUrl)

        licenses {
            license {
                name.set(pomLicenseName)
                url.set(pomLicenseUrl)
                distribution.set(pomLicenseUrl)
            }
        }

        developers {
            developer {
                id.set(pomDeveloperId)
                name.set(pomDeveloperName)
                url.set(pomDeveloperUrl)
            }
        }

        scm {
            url.set(pomScmUrl)
            connection.set(pomScmConnection)
            developerConnection.set(pomScmDevConnection)
        }
    }
}
