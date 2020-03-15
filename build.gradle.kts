import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    id("org.jetbrains.grammarkit") version "2020.1"
    kotlin("jvm") version "1.3.60"
    java
}

group = "de.nordgedanken"
version = "0.1.0"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
}

val intellijPublishToken: String? by project
tasks.publishPlugin {
    if (intellijPublishToken != null) {
        token(intellijPublishToken)
    }

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3.3"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      <h2>0.1.0</h2>
        <h3>Added</h3>
        <ul>
            <li>Initial Release</li>
            <li>Added most basic features</li>
        </ul>""")
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
