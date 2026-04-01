import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.Sign

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

val publishGroupId = (findProperty("PUBLISH_GROUP_ID") as String?) ?: "io.github.your-org"
val publishArtifactId = (findProperty("PUBLISH_ARTIFACT_ID") as String?) ?: "wheeltimepicker"
val publishVersion = (findProperty("PUBLISH_VERSION") as String?) ?: "0.1.0"

val ossrhUsername = (findProperty("OSSRH_USERNAME") as String?) ?: System.getenv("OSSRH_USERNAME")
val ossrhPassword = (findProperty("OSSRH_PASSWORD") as String?) ?: System.getenv("OSSRH_PASSWORD")

val signingKeyId = (findProperty("SIGNING_KEY_ID") as String?) ?: System.getenv("SIGNING_KEY_ID")
val signingKey =
    ((findProperty("SIGNING_KEY") as String?) ?: System.getenv("SIGNING_KEY"))
        ?.replace("\\n", "\n")
val signingPassword = (findProperty("SIGNING_PASSWORD") as String?) ?: System.getenv("SIGNING_PASSWORD")

val pomName = (findProperty("POM_NAME") as String?) ?: "WheelTimePicker"
val pomDescription =
    (findProperty("POM_DESCRIPTION") as String?)
        ?: "Composable wheel time picker with optional fade and cylinder effects."
val pomUrl = (findProperty("POM_URL") as String?) ?: "https://github.com/your-id/WheelTimePicker"
val pomLicenseName = (findProperty("POM_LICENSE_NAME") as String?) ?: "The Apache License, Version 2.0"
val pomLicenseUrl =
    (findProperty("POM_LICENSE_URL") as String?) ?: "https://www.apache.org/licenses/LICENSE-2.0.txt"
val pomDeveloperId = (findProperty("POM_DEVELOPER_ID") as String?) ?: "your-id"
val pomDeveloperName = (findProperty("POM_DEVELOPER_NAME") as String?) ?: "Your Name"
val pomDeveloperEmail = (findProperty("POM_DEVELOPER_EMAIL") as String?) ?: "your@email.com"
val pomScmUrl =
    (findProperty("POM_SCM_URL") as String?) ?: "https://github.com/your-id/WheelTimePicker"

val isSonatypePublishRequested =
    gradle.startParameter.taskNames.any { taskName ->
        taskName.contains("SonatypeRepository", ignoreCase = true)
    }

if (isSonatypePublishRequested) {
    check(!ossrhUsername.isNullOrBlank() && !ossrhPassword.isNullOrBlank()) {
        "OSSRH credentials are required. Set OSSRH_USERNAME and OSSRH_PASSWORD."
    }
    check(!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        "PGP signing is required. Set SIGNING_KEY and SIGNING_PASSWORD."
    }
    check(pomUrl != "https://github.com/your-id/WheelTimePicker") {
        "Set real POM metadata (POM_URL, POM_DEVELOPER_*, POM_SCM_URL) before Sonatype publish."
    }
}

group = publishGroupId
version = publishVersion

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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = publishGroupId
            artifactId = publishArtifactId
            version = publishVersion

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set(pomName)
                description.set(pomDescription)
                url.set(pomUrl)

                licenses {
                    license {
                        name.set(pomLicenseName)
                        url.set(pomLicenseUrl)
                    }
                }

                developers {
                    developer {
                        id.set(pomDeveloperId)
                        name.set(pomDeveloperName)
                        email.set(pomDeveloperEmail)
                    }
                }

                scm {
                    url.set(pomScmUrl)
                    connection.set("scm:git:$pomScmUrl.git")
                    developerConnection.set("scm:git:$pomScmUrl.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "localBuildRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }

        maven {
            name = "sonatype"
            val isSnapshot = publishVersion.endsWith("SNAPSHOT")
            url =
                uri(
                    if (isSnapshot) {
                        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    } else {
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    },
                )
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    val hasSigningKey = !signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()
    isRequired = gradle.taskGraph.allTasks.any { it is Sign } && hasSigningKey

    if (hasSigningKey) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
