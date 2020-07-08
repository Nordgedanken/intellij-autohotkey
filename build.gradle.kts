import com.github.rjeschke.txtmark.Processor
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    idea
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "0.4.21"


    id("org.jetbrains.grammarkit") version "2020.1.4"


    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Java support
    id("java")

    // detekt linter - read more: https://detekt.github.io/detekt/kotlindsl.html
    id("io.gitlab.arturbosch.detekt") version "1.10.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "de.nordgedanken"
version = "0.2.0"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
    jcenter()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("com.google.flogger:flogger:0.5.1")
    implementation("com.google.flogger:flogger-system-backend:0.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    implementation(kotlin("stdlib-jdk8"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.10.0")
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}


val intellijPublishToken: String? by project
tasks.publishPlugin {
    if (intellijPublishToken != null) {
        token(intellijPublishToken)
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3"
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
    untilBuild("201.*")
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

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
        }
    }

    withType<Detekt> {
        jvmTarget = "1.8"
    }

}
