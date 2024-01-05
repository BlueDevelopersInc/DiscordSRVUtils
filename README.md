# DiscordSRVUtils

DiscordSRVUtils is a plugin that adds more features to the DiscordSRV bot

[Modrinth Page](https://modrinth.com/plugin/discordsrvutils)

[![](https://bstats.org/signatures/bukkit/DiscordSRVUtils.svg)](https://bstats.org/plugin/bukkit/DiscordSRVUtils/9456 "DiscordSRVUtils' bstats")

# API

## Maven

###
Replace `VERSION` with the API version you would like. Latest release can be seen [here](https://repo.bluetree242.dev/api/maven/latest/version/maven-releases/dev/bluetree242/discordsrvutils/core), and latest development build/snapshot can be seen [here](https://repo.bluetree242.dev/api/maven/latest/version/maven-snapshots/dev/bluetree242/discordsrvutils/core).

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
    // Any other dependency...
    implementation 'dev.bluetree242.discordsrvutils:core:VERSION'
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

To contribute you can fork this repo and make changes on the `develop` branch. And you can Pull Request to the `develop`
branch. Pull requests on `master` will be denied and closed.
  






