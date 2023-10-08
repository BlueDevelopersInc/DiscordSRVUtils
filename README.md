# DiscordSRVUtils

DiscordSRVUtils is a plugin that adds more features to the DiscordSRV bot

[Spigot Page](https://www.spigotmc.org/resources/discordsrvutils.85958/)

[![](https://bstats.org/signatures/bukkit/DiscordSRVUtils.svg)](https://bstats.org/plugin/bukkit/DiscordSRV/387 "DiscordSRVUtils' bstats")

# API

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
        implementation 'dev.bluetree242.discordsrvutils:core:VERSION'
    }
```
## Gradle (Kotlin DSL)

    repositories {
        // Any other repository...
        maven("https://repo.bluetree242.dev/maven-public")
    }
```
```gradle
    dependencies {
        // Any other dependency...
        compileOnly("dev.bluetree242.discordsrvutils:core:VERSION")
    }
```

You also need to add DiscordSRV's dependency and repository, which you can find [here](https://docs.discordsrv.com/master/#developers)

# Contributing

To contribute you can fork this repo and make changes on the `develop` branch. And you can Pull Request to the `develop`
branch. Pull requests on `master` will be denied and closed.
  






