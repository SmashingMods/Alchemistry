plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

val minecraftVersion: String by extra
val modVersion: String by extra
val neoVersion: String by extra
val parchmentMappingsVersion: String by extra
val parchmentMinecraftVersion: String by extra
val neoforgeVersionRange: String by extra
val jeiVersion: String by extra
val chemlibVersion: String by extra
val chemlibVersionRange: String by extra

val localRuntime: Configuration by configurations.creating

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

version = modVersion
group = "com.smashingmods.chemlib"

repositories {
    maven("https://maven.blamejared.com/")
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }

        filter {
            includeGroup("maven.modrinth")
        }
    }
}

base {
    archivesName = "chemlib"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    accessTransformers.from("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("data") {
            data()
            programArguments.addAll("--mod", "chemlib", "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.INFO
        }
    }

    mods {
        create("chemlib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

configurations {
    runtimeClasspath.get().extendsFrom(localRuntime)
}

dependencies {
    compileOnly("mezz.jei:jei-$minecraftVersion-common-api:${jeiVersion}")
    compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:${jeiVersion}")
    implementation(files("libs/alchemylib-1.21.1-1.0.30.jar"))
    implementation("maven.modrinth:chemlib-updated:${chemlibVersion}")
    localRuntime("mezz.jei:jei-$minecraftVersion-neoforge:${jeiVersion}")
}

tasks.processResources {
    var replaceProperties = mapOf("minecraftVersion" to minecraftVersion, "neoVersion" to neoVersion,
        "neoforgeVersionRange" to neoforgeVersionRange, "modVersion" to modVersion
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

/*
publishing {
    publications {
        register("mavenJava", MavenPublication) {
            from components.java
        }
    }
    repositories {
        maven {
            url "file://${project.projectDir}/repo"
        }
    }
}
*/
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

/*
publishing {
    publications {
        mavenJava(MavenPublication) {
            afterEvaluate {
                artifact project.jar
                artifact project.sourcesJar
                artifact project.javadocJar
            }
            setGroupId "smashingmods"
            setArtifactId "chemlib"
        }
    }
    repositories {
        maven {
            url "https://maven.tamaized.com/releases"
            credentials {
                username secrets.getProperty("maven_username")
                password secrets.getProperty("maven_password")
            }
        }
    }
}
 */

/*
plugins {
    id 'java'
    id 'idea'
    id 'net.minecraftforge.gradle' version '[6.0,6.2)'
    id 'org.parchmentmc.librarian.forgegradle' version '1.+'
    id 'com.matthewprenger.cursegradle' version '1.4.0'
    id 'maven-publish'
}

apply plugin: 'net.minecraftforge.gradle'
apply plugin: 'org.parchmentmc.librarian.forgegradle'
apply plugin: 'maven-publish'

version = "$minecraft_version-$mod_version"
group = 'com.smashingmods'

java {
    archivesBaseName = 'alchemistry'
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
    withJavadocJar()
}

minecraft {
    mappings channel: "$mapping_channel", version: "$mapping_version-1.20.1"
    copyIdeResources = true
    accessTransformer = file('src/main/resources/META-INF/accesstransformer.cfg')
    runs {
        client {
            workingDirectory project.file('run')
            property 'forge.logging.markers', 'SCAN,REGISTRIES,REGISTRYDUMP'
            property 'forge.logging.console.level', 'debug'
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
            jvmArgs '-XX:+AllowEnhancedClassRedefinition'

            mods {
                alchemistry {
                    source sourceSets.main
                }
            }
        }

        server {
            workingDirectory project.file('run')
            property 'forge.logging.markers', 'SCAN,REGISTRIES,REGISTRYDUMP'
            property 'forge.logging.console.level', 'debug'
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
            jvmArgs '-XX:+AllowEnhancedClassRedefinition'
            args 'nogui'

            mods {
                alchemistry {
                    source sourceSets.main
                }
            }
        }

        gameTestServer {
            workingDirectory project.file('run')
            property 'forge.logging.markers', 'REGISTRIES'
            property 'forge.logging.console.level', 'debug'
            property 'forge.enabledGameTestNamespaces', 'alchemistry'

            mods {
                alchemistry {
                    source sourceSets.main
                }
            }
        }

        data {
            workingDirectory project.file('run')
            property 'forge.logging.markers', 'SCAN,REGISTRIES,REGISTRYDUMP'
            property 'forge.logging.console.level', 'debug'
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
            jvmArgs '-XX:+AllowEnhancedClassRedefinition'

            args '--mod', "$archivesBaseName", '--all', '--output', file('src/generated/resources/'), '--existing', file('src/main/resources')

            mods {
                alchemistry {
                    source sourceSets.main
                }
            }
        }
    }
}

repositories {
    maven { url "https://maven.tamaized.com/releases" }
    maven { url "https://www.cursemaven.com" }
    maven { url "https://dvs1.progwml6.com/files/maven/" }
    maven { url "https://maven.blamejared.com" }
}

dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}"

    implementation fg.deobf("smashingmods:chemlib:${minecraft_version}-${chemlib_version}")
    implementation fg.deobf("smashingmods:alchemylib:${minecraft_version}-${alchemylib_version}")

    compileOnly fg.deobf("mezz.jei:jei-${minecraft_version}-common-api:${jei_version}")
    compileOnly fg.deobf("mezz.jei:jei-${minecraft_version}-forge-api:${jei_version}")
    runtimeOnly fg.deobf("mezz.jei:jei-${minecraft_version}-forge:${jei_version}")

    compileOnly fg.deobf("vazkii.patchouli:Patchouli:${minecraft_version}-${patchouli_version}-FORGE:api")
    runtimeOnly fg.deobf("vazkii.patchouli:Patchouli:${minecraft_version}-${patchouli_version}-FORGE")

//    runtimeOnly fg.deobf("curse.maven:ctm-267602:3737369")
//    runtimeOnly fg.deobf("curse.maven:mekanism-268560:4020942")

    runtimeOnly fg.deobf("curse.maven:generatorgalore-691049:4580850")
    runtimeOnly fg.deobf("curse.maven:pipez-443900:4629656")
}

def resourceTargets = ['META-INF/mods.toml', 'pack.mcmeta']
def replaceProperties = [
        minecraft_version: minecraft_version, minecraft_version_range: minecraft_version_range,
        forge_version: forge_version, forge_version_range: forge_version_range,
        loader_version_range: loader_version_range,
        mod_id: mod_id, mod_name: mod_name, mod_license: mod_license, mod_version: mod_version,
        mod_authors: mod_authors, mod_description: mod_description,
        chemlib_version: chemlib_version, alchemylib_version: alchemylib_version
]
processResources {
    inputs.properties replaceProperties
    replaceProperties.put 'project', project

    filesMatching(resourceTargets) {
        expand replaceProperties
    }
}

jar {
    manifest {
        attributes([
                "Specification-Title"     : "$archivesBaseName",
                "Specification-Vendor"    : "SmashingMods",
                "Specification-Version"   : "1",
                "Implementation-Title"    : project.name,
                "Implementation-Version"  : project.version,
                "Implementation-Vendor"   : "SmashingMods",
                "Implementation-Timestamp": new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
        ])
    }
}

def secrets = new Properties()
file('secrets.properties').withInputStream {
    stream -> secrets.load(stream)
}

fileTree("secrets").matching {
    include ".properties"
}.each {
    File file ->
        file.withInputStream {
            stream -> secrets.load(stream)
        }
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            afterEvaluate {
                artifact project.jar
                artifact project.sourcesJar
                artifact project.javadocJar
            }
            setGroupId 'smashingmods'
            setArtifactId 'alchemistry'
        }
    }
    repositories {
        maven {
            url "https://maven.tamaized.com/releases"
            credentials {
                username secrets.getProperty("maven_username")
                password secrets.getProperty("maven_password")
            }
        }
    }
}

curseforge {
    apiKey = secrets.getProperty("apiKey")
    project {
        id = '293425'
        releaseType = 'release'
        changelogType = 'markdown'
        changelog = file("changelog.md")
        addGameVersion 'Forge'
        addGameVersion 'Java 17'
        addGameVersion "$minecraft_version"
        mainArtifact(jar) {
            displayName = "Alchemistry - $project.version"
            relations {
                requiredDependency 'chemlib'
                requiredDependency 'alchemylib'
                optionalDependency 'jei'
                optionalDependency 'patchouli'
//                optionalDependency 'ctm'
            }
        }
    }
}

sourceSets.main.resources { srcDir 'src/generated/resources' }

jar.finalizedBy('reobfJar')

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}

 */
