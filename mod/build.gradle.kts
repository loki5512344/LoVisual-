plugins {
    id("fabric-loom") version "1.14.10"
}

base {
    archivesName = "lovisual"
}

repositories {
    maven("https://maven.fabricmc.net/")
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.6:v2")
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.4+1.21.11")

    implementation("com.google.code.gson:gson:2.11.0")
}

loom {
    accessWidenerPath = file("src/main/resources/lovisual.accesswidener")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand("version" to project.version, "loaderVersion" to ">=0.19.3")
        }
    }
}
