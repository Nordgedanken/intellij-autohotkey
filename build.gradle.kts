import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.rjeschke.txtmark.Processor
import java.util.Scanner

plugins {
    idea
    id("org.jetbrains.intellij") version "0.4.20"
    id("org.jetbrains.grammarkit") version "2020.1.4"
    kotlin("jvm") version "1.3.72"
    java
}

group = "de.nordgedanken"
version = "0.1.3"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.13")
}

val intellijPublishToken: String? by project
tasks.publishPlugin {
    if (intellijPublishToken != null) {
        token(intellijPublishToken)
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "193-EAP-SNAPSHOT"
    setPlugins("java")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


buildscript {
    dependencies {
        classpath("es.nitaur.markdown:txtmark:0.16")
    }
}

val pluginDescMkdown = """
A simple plugin for developing AutoHotKey scripts. The following features are available:

- Syntax highlighting
- Run configurations
- More to come in the future...

*Note: This plugin is under development and does not have a stable release yet*
""".trimIndent()

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    val latestChangesRegex = """(## \[\d+\.\d+\.\d+\][\w\W]*?)## \["""
    val latestChangesMkdown = Scanner(rootProject.file("CHANGELOG.md")).findWithinHorizon(latestChangesRegex, 0)
    var latestChangeNotes = latestChangesRegex.toRegex().find(latestChangesMkdown)!!.groups[1]!!.value
    latestChangeNotes += "Please see [CHANGELOG.md](https://github.com/Nordgedanken/auto_hot_key_jetbrains_plugin/blob/master/CHANGELOG.md) for a full list of changes."
    changeNotes(Processor.process(latestChangeNotes))

    pluginDescription(Processor.process(pluginDescMkdown))

    setVersion(version)
}




project(":") {
    val generateAHKLexer = task<GenerateLexer>("generateAHKLexer") {
        source = "src/main/java/de/nordgedanken/auto_hotkey/AutoHotKey/flex/AutoHotKey.flex"
        targetDir = "src/main/gen/de/nordgedanken/auto_hotkey/"
        targetClass = "AHKLexer"
        purgeOldFiles = true
    }

    val generateAHKParser = task<GenerateParser>("generateAHKParser") {
        source = "src/main/java/de/nordgedanken/auto_hotkey/AutoHotKey.bnf"
        targetRoot = "src/main/gen"
        pathToParser = "de/nordgedanken/auto_hotkey/parser/AHKParser.java"
        pathToPsiRoot = "de/nordgedanken/auto_hotkey/psi"
        purgeOldFiles = true
    }

    tasks.withType<KotlinCompile> {
        dependsOn(
                generateAHKLexer,
                generateAHKParser
        )
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}
