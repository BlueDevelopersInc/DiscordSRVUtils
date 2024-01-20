/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

import net.kyori.blossom.BlossomExtension

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.kyori.blossom") version "1.3.1" apply false
}

dependencies {
    implementation(project(":bukkit")) {
        isTransitive = false
    }
    implementation(project(":core"))
}

allprojects {
    apply(plugin = "java")
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://mvn-repo.arim.space/affero-gpl3/")
        maven("https://mvn-repo.arim.space/lesser-gpl3/")
        maven("https://nexus.scarsz.me/content/groups/public/")
    }
    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val buildNumber = project.properties["buildNumber"]?.toString() ?: "NONE"
val commit = System.getenv("GIT_COMMIT") ?: System.getProperty("GIT_COMMIT") ?: System.getenv("GITHUB_SHA") ?: "UNKNOWN"

println("Build number is $buildNumber")
println("Commit hash is $commit")

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    tasks.withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    if (name == "core") {
        apply(plugin = "net.kyori.blossom")
        extensions.configure<BlossomExtension> {
            val main = "src/main/java/dev/bluetree242/discordsrvutils/VersionInfo.java"
            replaceToken("@version@", version, main)
            replaceToken("@build@", buildNumber, main)
            replaceToken("@commit@", commit, main)
            replaceToken("@buildDate@", System.currentTimeMillis(), main)
        }
    } else if (name == "bukkit") {
        tasks.processResources {
            expand("version" to project.version)
        }
    }
}

tasks.build {
    finalizedBy(tasks.shadowJar)
}
tasks.shadowJar {
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
