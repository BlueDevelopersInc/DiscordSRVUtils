import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Property

/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

plugins {
    id("java")
    id("maven-publish")
    id("nu.studer.jooq") version "5.2.2"
    id("org.flywaydb.flyway") version "8.5.3"
    id("com.github.johnrengelman.shadow")
}

rootProject.allprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    tasks.shadowJar {
        archiveClassifier.set("")
        relocate("com.vdurmont", "dev.bluetree242.discordsrvutils.dependencies.emoji-java")
        relocate("org.bstats", "dev.bluetree242.discordsrvutils.dependencies.bstats")
        relocate("space.arim.dazzleconf", "dev.bluetree242.discordsrvutils.dependencies.dazzleconf")
        relocate("com.zaxxer", "dev.bluetree242.discordsrvutils.dependencies.hikariCP")
        relocate("org.hsqldb", "dev.bluetree242.discordsrvutils.dependencies.hsqldb")
        relocate("org.mariadb.jdbc", "dev.bluetree242.discordsrvutils.dependencies.mariadb")
        relocate("org.yaml", "dev.bluetree242.discordsrvutils.dependencies.yaml")
        relocate("com.squareup.okhttp3", "dev.bluetree242.discordsrvutils.dependencies.okhttp")
        relocate("org.flywaydb", "dev.bluetree242.discordsrvutils.dependencies.flywaydb")
        relocate("org.bstats", "dev.bluetree242.discordsrvutils.dependencies.bstats")
        relocate("com.github.benmanes.caffeine", "dev.bluetree242.discordsrvutils.dependencies.caffeine")
        relocate("org.json", "dev.bluetree242.discordsrvutils.dependencies.json")
        relocate("com.google.errorprone", "dev.bluetree242.discordsrvutils.dependencies.google")
        relocate("org.apache.commons", "dev.bluetree242.discordsrvutils.dependencies.commons")
        relocate("org.jooq", "dev.bluetree242.discordsrvutils.dependencies.jooq")
        relocate("org.slf4j", "dev.bluetree242.discordsrvutils.dependencies.slf4j")
        relocate("org.reactivestreams", "dev.bluetree242.discordsrvutils.dependencies.reactivestreams")
        relocate("org.checkerframework", "dev.bluetree242.discordsrvutils.dependencies.checkerframework")
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

dependencies {
    // Database
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.hsqldb:hsqldb:2.6.1:jdk8")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.0")

    // Database Migration
    implementation("org.flywaydb:flyway-core:7.5.3")

    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.1")

    // Configuration
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.0-M2")

    // Discord Integration
    compileOnly("com.discordsrv:discordsrv:${rootProject.properties["discordsrv_version"]}")

    // JSON Processing
    implementation("org.json:json:${rootProject.properties["org_json_version"]}")

    // jOOQ for SQL queries
    implementation("org.jooq:jooq:3.14.16")

    // Code Generation
    testImplementation("com.h2database:h2:1.4.197")
    jooqGenerator("com.h2database:h2:1.4.197")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.1")

    // General Utilities
    implementation("commons-io:commons-io:2.7")
}


flyway {
    url = "jdbc:h2:file:${project.layout.buildDirectory.asFile.get()}/migration;MODE=Mysql;DATABASE_TO_UPPER=false"
    user = "SA"
    password = ""
    validateMigrationNaming = true
    locations = arrayOf("filesystem:src/main/resources/flyway-migrations")
    group = true
}

jooq {
    version.set("3.14.16")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:file:${project.layout.buildDirectory.asFile.get()}/migration"
                    user = "SA"
                    password = ""
                    properties.add(Property().apply {
                        key = "ssl"
                        value = "false"
                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.h2.H2Database"
                        inputSchema = "PUBLIC"
                        isOutputSchemaToDefault = true
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "dev.bluetree242.discordsrvutils.jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    strategy.apply {
                        matchers = Matchers().apply {
                            tables.add(MatchersTableType().withTableClass(MatcherRule().apply {
                                transform = MatcherTransformType.PASCAL
                                expression = "\$0_TABLE"
                            }))
                            enums.add(MatchersEnumType().withEnumClass(MatcherRule().apply {
                                transform = MatcherTransformType.PASCAL
                                expression = "\$0_ENUM"
                            }))
                        }
                    }
                }
            }
        }
    }
}

tasks.withType<JooqGenerate> {
    dependsOn("flywayMigrate")
    allInputsDeclared.set(true)
    inputs.apply {
        files(fileTree("src/main/resources/flyway-migrations"))
                .withPropertyName("migrations")
                .withPathSensitivity(PathSensitivity.RELATIVE)
    }
}

tasks {
    java {
        withSourcesJar()
    }
}

publishing {
    repositories {
        maven {
            val repository = "https://repo.bluetree242.dev/maven-"
            val releasesRepoUrl = repository + "releases"
            val snapshotsRepoUrl = repository + "snapshots"
            url = uri(if (version.toString().endsWith("-DEV")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"]) {
                classifier = null
            }
            artifact(tasks["sourcesJar"])
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

tasks.named("publishMavenPublicationToMavenRepository") {
    dependsOn("jar")
}