import com.github.rjeschke.txtmark.Processor
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    idea
    id("org.jetbrains.intellij") version "0.4.20"
    id("org.jetbrains.grammarkit") version "2020.1.4"
    kotlin("jvm") version "1.3.72"
    java
}

group = "de.nordgedanken"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("com.google.flogger:flogger:0.5.1")
    implementation("com.google.flogger:flogger-system-backend:0.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

val intellijPublishToken: String? by project
tasks.publishPlugin {
    if (intellijPublishToken != null) {
        token(intellijPublishToken)
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1"
    type = "IC"
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

- Syntax highlighting (under construction)
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
    version("## \\[(\\d+\\.\\d+\\.\\d+)\\]".toRegex().find(latestChangesMkdown)!!.groups[1]!!.value)
    untilBuild("202.*")
}




project(":") {
    val generateAHKLexer = task<GenerateLexer>("generateAHKLexer") {
        source = "src/main/java/de/nordgedanken/auto_hotkey/lang/lexer/AutoHotkey.flex"
        targetDir = "src/main/gen/de/nordgedanken/auto_hotkey/"
        targetClass = "AhkLexer"
        purgeOldFiles = true
    }

    val generateAHKParser = task<GenerateParser>("generateAHKParser") {
        source = "src/main/java/de/nordgedanken/auto_hotkey/lang/grammar/AutoHotkey.bnf"
        targetRoot = "src/main/gen"
        pathToParser = "de/nordgedanken/auto_hotkey/lang/parser/AhkParser.java"
        pathToPsiRoot = "de/nordgedanken/auto_hotkey/lang/psi"
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
