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

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.apache.tools.ant.filters.ReplaceTokens
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
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://jitpack.io")
}

fun urlFile(url: String, name: String): ConfigurableFileCollection {
    val file = File("${project.layout.buildDirectory.asFile.get()}/download/${name}.jar")
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
    // Bukkit Plugins and APIs
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.gitlab.ruany:LiteBansAPI:0.3.4")
    compileOnly("space.arim.libertybans:bans-api:0.8.0")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.17")
    compileOnly("com.github.DevLeoko:AdvancedBan:b4bbb6a")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly(urlFile("https://github.com/Zrips/CMI-API/releases/download/8.7.8.2/CMIAPI8.7.8.2.jar", "CMI-API"))
    compileOnly("net.lapismc:AFKPlus:3.3.15")
    compileOnly("com.discordsrv:discordsrv:${rootProject.properties["discordsrv_version"]}")
    compileOnly("net.essentialsx:EssentialsX:2.19.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("org.jooq:jooq:3.14.16")
    implementation("org.json:json:${rootProject.properties["org_json_version"]}")

    // Core plugin
    implementation(project(":core"))
}


// Generates plugin.yml
bukkit {
    name = rootProject.name
    description = rootProject.description
    version = project.version.toString()
    author = "BlueTree242"
    main = "dev.bluetree242.discordsrvutils.bukkit.DiscordSRVUtilsBukkit"
    apiVersion = "1.13"
    website = "bluetree242.dev"
    foliaSupported = true
    commands {
        register("discordsrvutils") {
            aliases = listOf("dsrvu", "dsu", "du", "discordu", "srvu", "dut")
        }
    }
    softDepend = listOf("DiscordSRV", "Essentials", "AdvancedBan", "PlaceholderAPI", "LiteBans", "LibertyBans", "CMI", "SuperVanish", "PremiumVanish", "AfkPlus")
    permissions {
        register("discordsrvutils.reload")
        register("discordsrvutils.debug")
        register("discordsrvutils.errornotifications")
        register("discordsrvutils.updatecheck")
        register("discordsrvutils.removeslash")
        register("discordsrvutils.resetlevel")
        register("discordsrvutils.addxp")
        forEach {
            it.default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

allprojects {
    project.java {
        disableAutoTargetJvm() // Paper uses java 17, but we need to support java 8
        toolchain.languageVersion.set(JavaLanguageVersion.of(17)) // This doesn't change the compiled class version.
    }
}
