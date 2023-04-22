import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Imports a property from gradle.properties file
fun properties(key: String) = project.findProperty(key).toString()

plugins {
    idea
    id("org.jetbrains.intellij") version "1.8.+"
    id("org.jetbrains.grammarkit") version "2021.2.+"
    kotlin("jvm") version "1.7.+"
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "10.+"
    id("org.barfuin.gradle.jacocolog") version "1.+" // show coverage in console
    id("org.jetbrains.changelog") version "1.3.+"
    id("org.jetbrains.qodana") version "0.1.+"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.+")
    testImplementation("io.kotest:kotest-assertions-core:5.+")
    testImplementation("io.kotest:kotest-framework-datatest:5.+")
    testImplementation("io.mockk:mockk:1.12.4")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.+") {
        because(
            "this is needed to run parsing/lexing tests which extend " +
                "intellij base classes that use junit4",
        )
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2022.1")
    type.set("PC")
    plugins.set(properties("pluginDependencies").split(',').map(String::trim).filter(String::isNotEmpty))
}

ktlint {
    enableExperimentalRules.set(true)
    disabledRules.set(setOf("experimental:package-name"))
}

changelog {
    version.set(properties("pluginVersion"))
    header.set(provider { "[${version.get()}] - ${date()}" })
    groups.set(listOf("Added"))
}

qodana {
    cachePath.set(projectDir.resolve(".qodana").canonicalPath)
    reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
}

val generateAhkLexer = task<GenerateLexerTask>("generateAhkLexer") {
    source.set("src/main/kotlin/de/nordgedanken/auto_hotkey/lang/lexer/AutoHotkey.flex")
    targetDir.set("src/main/gen/de/nordgedanken/auto_hotkey/")
    targetClass.set("AhkLexer")
    purgeOldFiles.set(true)
}

val generateAhkParser = task<GenerateParserTask>("generateAhkParser") {
    source.set("src/main/kotlin/de/nordgedanken/auto_hotkey/lang/parser/AutoHotkey.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("de/nordgedanken/auto_hotkey/lang/parser/AhkParser.java")
    pathToPsiRoot.set("de/nordgedanken/auto_hotkey/lang/psi")
    purgeOldFiles.set(false)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        dependsOn(generateAhkLexer, generateAhkParser)
    }

    patchPluginXml {
        version.set(changelog.version)
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        changeNotes.set(
            provider {
                val newChangeNotes = changelog.get(changelog.version.get()).withHeader(true).toHTML() +
                    """Please see <a href=
                        |"https://github.com/Nordgedanken/intellij-autohotkey/blob/master/CHANGELOG.md"
                        |>CHANGELOG.md</a> for a full list of changes.
                    """.trimMargin()
                check(newChangeNotes.contains("(compatibility:")) {
                    "Latest change notes must specify the compatibility range of the plugin!"
                }
                return@provider newChangeNotes
            },
        )
        pluginDescription.set(
            File("$rootDir/README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) },
        )
    }

    val intellijPublishToken: String? by project
    publishPlugin {
        if (intellijPublishToken != null) {
            token.set(intellijPublishToken)
        }
    }

    // testing-related stuff below
    test {
        useJUnitPlatform()
    }

    jacocoTestReport {
        dependsOn(test)
        setClassesToIncludeInCoverageCheck(classDirectories)
    }

    jacocoTestCoverageVerification {
        dependsOn(jacocoTestReport)
        setClassesToIncludeInCoverageCheck(classDirectories)

        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    minimum = "0.87".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    minimum = "0.67".toBigDecimal()
                }
            }
        }
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }
}

fun setClassesToIncludeInCoverageCheck(classDirectories: ConfigurableFileCollection) {
    // packages listed here can't be tested
    val packagesToExcludeFromCoverageCheck = listOf(
        "**/autohotkey/runconfig/execution/**",
        "**/autohotkey/util/**",

        // swing ui packages; must be tested manually
        "**/autohotkey/runconfig/ui/**",
        "**/autohotkey/sdk/ui/**",
        "**/autohotkey/project/configurable/**",
        "**/autohotkey/project/settings/ui/**",
    )

    // files listed here can't be tested, but the package wasn't excluded since other files within it can be tested
    val filesToExcludeFromCoverageCheck = listOf(
        "**/autohotkey/sdk/AhkSdkType*", // pattern must be specified like this to include declared extension fns
        "**/autohotkey/ide/actions/AhkCreateFileAction*",
        "**/autohotkey/ide/highlighter/AhkColorSettingsPage*",
    )

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(packagesToExcludeFromCoverageCheck + filesToExcludeFromCoverageCheck)
        },
    )
}
