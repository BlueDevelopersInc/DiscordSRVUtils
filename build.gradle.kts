import net.kyori.blossom.BlossomExtension

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.kyori.blossom") version "1.3.1" apply false
}

dependencies {
    implementation(project(":bukkit"))
}

allprojects {
    apply(plugin = "java")
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.citizensnpcs.co")
        maven("https://nexus.scarsz.me/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://jitpack.io")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://m2.dv8tion.net/releases")
        maven("https://mvn-repo.arim.space/affero-gpl3")
        maven("https://mvn-repo.arim.space/gpl3")
        maven("https://mvn-repo.arim.space/lesser-gpl3")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.essentialsx.net/releases/")
    }
    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.22")
        compileOnly("org.projectlombok:lombok:1.18.22")
        annotationProcessor("org.projectlombok:lombok:1.18.22")

        testImplementation("org.projectlombok:lombok:1.18.22")
        testCompileOnly("org.projectlombok:lombok:1.18.22")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val buildNumber = project.properties["buildNumber"]?.toString() ?: "NONE"
val commit = project.properties["commit"]?.toString() ?: "NONE"

println("Build Number is $buildNumber")
println("Commit Hash is $commit")

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

tasks.getByName("build").finalizedBy(tasks.getByName("shadowJar"))
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
