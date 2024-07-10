import BuildPluginsVersion.KOTLIN

plugins {
    kotlin("jvm")// version BuildPluginsVersion.KOTLIN apply false
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish")
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN")
    implementation("org.jetbrains.kotlin:kotlin-native-utils:$KOTLIN")

    implementation("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.archive:5.10.0.202012080955-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:5.10.0.202012080955-r")
    implementation("commons-io:commons-io:2.8.0")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    testImplementation(TestingLib.JUNIT)
}
/*
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(17)
}
*/
gradlePlugin {
    plugins {
        create(PluginCoordinates.ID) {
            id = PluginCoordinates.ID
            displayName = PluginBundle.DISPLAY_NAME
            description = PluginBundle.DESCRIPTION
            tags = PluginBundle.TAGS
            implementationClass = PluginCoordinates.IMPLEMENTATION_CLASS
            version = PluginCoordinates.VERSION
        }
    }
}

// Configuration Block for the Plugin Marker artifact on Plugin Central
gradlePlugin {
     website = PluginBundle.WEBSITE
     vcsUrl = PluginBundle.VCS
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
