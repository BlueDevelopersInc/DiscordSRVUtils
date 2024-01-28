<div align="center">
  <img alt="Spigot" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg">
  <a href="https://modrinth.com/plugin/discordsrvutils"><img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
  <a href="https://discordsrvutils.xyz"><img alt="Website" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/website_vector.svg"></a>
</div>

# DiscordSRVUtils

Elevate your Discord server with custom messages, synced punishments, tickets, and leveling!

![Modrinth Downloads](https://img.shields.io/modrinth/dt/kPCZgvM2?label=Modrinth%20downloads)
![Spigot Downloads](https://img.shields.io/spiget/downloads/85958?label=SpigotMC%20downloads&color=FF7F7F)
[![](https://bstats.org/signatures/bukkit/DiscordSRVUtils.svg)](https://bstats.org/plugin/bukkit/DiscordSRVUtils/9456 "DiscordSRVUtils' bstats")

# API

### Version

![Latest release](https://repo.bluetree242.dev/api/badge/latest/maven-releases/dev/bluetree242/discordsrvutils/core?name=Latest%20Release&color=FF7F7F)
![Latest Development Build](https://repo.bluetree242.dev/api/badge/latest/maven-snapshots/dev/bluetree242/discordsrvutils/core?name=Latest%20Development%20Build&color=FF7F7F)

Replace `VERSION` with the API version you would like. The badges above displays the latest versions available.

## Maven

```xml

<repository>
    <id>bluetree242-repo</id>
    <url>https://repo.bluetree242.dev/maven-public</url>
</repository>
```

```xml

<dependency>
    <groupId>dev.bluetree242.discordsrvutils</groupId>
    <artifactId>core</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

## Gradle (Groovy DSL)

```gradle
repositories {
    // Any other repository...
    maven { url 'https://repo.bluetree242.dev/maven-public' }
}
```

```gradle
dependencies {
    // Any other dependency...
    compileOnly 'dev.bluetree242.discordsrvutils:core:VERSION'
}
```

## Gradle (Kotlin DSL)

```kts
repositories {
    // Any other repository...
    maven("https://repo.bluetree242.dev/maven-public")
}
```

```kts
dependencies {
    // Any other dependency...
    compileOnly("dev.bluetree242.discordsrvutils:core:VERSION")
}
```

You also need to add DiscordSRV's dependency and repository, which you can
find [here](https://docs.discordsrv.com/master/#developers)

# Contributing

To contribute you can fork this repo and make changes on the `develop` branch. And you can pull request to the `develop`
branch. Pull requests on `master` will be denied and closed.

# License

[License]:https://img.shields.io/github/license/BlueDevelopersInc/DiscordSRVUtils?color=e
[![License]](https://github.com/BlueDevelopersInc/DiscordSRVUtils/blob/master/LICENSE)
  






