import org.jetbrains.changelog.closure
import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    id("org.jetbrains.intellij") version "0.6.5"
    id("org.jetbrains.grammarkit") version "2020.3.2"
    kotlin("jvm") version "1.4.31"
    java
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.barfuin.gradle.jacocolog") version "1.2.4" // show coverage in console
    id("org.jetbrains.changelog") version "1.1.2"
}

group = "de.nordgedanken"

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.flogger:flogger:0.5.1")
    implementation("com.google.flogger:flogger-system-backend:0.5.1")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.0")
    testImplementation("io.kotest:kotest-assertions-core:4.4.0")
    testImplementation("io.kotest:kotest-property:4.4.0")
    testImplementation("io.mockk:mockk:1.10.6")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.0") {
        because(
            "this is needed to run parsing/lexing tests which extend " +
                "intellij base classes that use junit4"
        )
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1"
    type = "PC"
}

val generateAhkLexer = task<GenerateLexer>("generateAhkLexer") {
    source = "src/main/kotlin/de/nordgedanken/auto_hotkey/lang/lexer/AutoHotkey.flex"
    targetDir = "src/main/gen/de/nordgedanken/auto_hotkey/"
    targetClass = "AhkLexer"
    purgeOldFiles = true
}

val generateAhkParser = task<GenerateParser>("generateAhkParser") {
    source = "src/main/kotlin/de/nordgedanken/auto_hotkey/lang/parser/AutoHotkey.bnf"
    targetRoot = "src/main/gen"
    pathToParser = "de/nordgedanken/auto_hotkey/lang/parser/AhkParser.java"
    pathToPsiRoot = "de/nordgedanken/auto_hotkey/lang/psi"
    purgeOldFiles = true
}

changelog {
    version = "0.4.0"
    header = closure { "[$version] - ${date()}" }
    groups = listOf("Added")
}

// Imports a property from gradle.properties file
fun prop(name: String) = extra.properties[name] as? String ?: error("`$name` is not defined in gradle.properties")

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn(generateAhkLexer, generateAhkParser)
    }

    patchPluginXml {
        changeNotes(
            closure {
                changelog.get(changelog.version).withHeader(true).toHTML() +
                    """Please see <a href=
                        |"https://github.com/Nordgedanken/intellij-autohotkey/blob/master/CHANGELOG.md"
                        |>CHANGELOG.md</a> for a full list of changes.""".trimMargin()
            }
        )
        pluginDescription(
            closure {
                File("./README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )
        version(changelog.version)
        sinceBuild(prop("pluginSinceBuild"))
        untilBuild(prop("pluginUntilBuild"))
    }

    val intellijPublishToken: String? by project
    publishPlugin {
        if (intellijPublishToken != null) {
            token(intellijPublishToken)
        }
    }

    // testing-related stuff below
    test {
        useJUnitPlatform()
    }

    val packagesToExcludeFromCoverageCheck = listOf(
        // below runconfig directories can't be tested to my knowledge atm
        "**/auto_hotkey/runconfig/execution/**",

        // swing ui packages; must be tested manually
        "**/auto_hotkey/runconfig/ui/**",
        "**/auto_hotkey/settings/ui/**"
    )

    jacocoTestReport {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude(packagesToExcludeFromCoverageCheck)
                    }
                }
            )
        )
    }

    jacocoTestCoverageVerification {
        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude(packagesToExcludeFromCoverageCheck)
            }
        )

        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    minimum = "0.75".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    minimum = "0.60".toBigDecimal()
                }
            }
        }
    }

    register("checkTestCoverage") {
        group = "verification"
        description = "Runs the unit tests with coverage."

        dependsOn(test, jacocoTestReport, jacocoTestCoverageVerification)
        val jacocoTestReport = jacocoTestReport.get()
        jacocoTestReport.mustRunAfter(test)
        jacocoTestCoverageVerification.get().mustRunAfter(jacocoTestReport)
    }
}
