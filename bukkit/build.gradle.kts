import java.net.URL

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
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

fun urlFile(url: String, name: String): ConfigurableFileCollection {
    val file = File("$buildDir/download/${name}.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        URL(url).openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
        }
    }
    return files(file.path.toString())
}


dependencies {
    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    compileOnly("com.gitlab.ruany:LiteBansAPI:0.3.4")
    compileOnly("space.arim.libertybans:bans-api:0.8.0")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.17")
    compileOnly("com.github.DevLeoko:AdvancedBan:b4bbb6a")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly(urlFile("https://github.com/Zrips/CMI-API/releases/download/8.7.8.2/CMIAPI8.7.8.2.jar", "CMI-API"))
    compileOnly("net.lapismc:AFKPlus:3.3.15")
    compileOnly("com.discordsrv:discordsrv:${rootProject.properties["discordsrv_version"]}")
    compileOnly("net.essentialsx:EssentialsX:2.19.0")
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("org.jooq:jooq:3.14.16")
    implementation("org.json:json:${rootProject.properties["org_json_version"]}")
    implementation(project(":core"))
}