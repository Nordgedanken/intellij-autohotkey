import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Imports a property from gradle.properties file
fun properties(key: String) = providers.gradleProperty(key)

plugins {
    idea
    alias(libs.plugins.gradleIntelliJPlugin)
    alias(libs.plugins.grammarKit)
    alias(libs.plugins.kotlin)
    jacoco
    alias(libs.plugins.ktlint)
    alias(libs.plugins.jacocolog) // show coverage in console
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Include the generated files in the source set
sourceSets.main.get().java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine) {
        because(
            "this is needed to run parsing/lexing tests which extend " +
                "intellij base classes that use junit4",
        )
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2023.1")
    type.set("PC")
    plugins.set(properties("pluginDependencies").get().split(',').map(String::trim).filter(String::isNotEmpty))
}

ktlint {
    version.set("0.48.2")
    enableExperimentalRules.set(true)
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
    sourceFile.set(file("src/main/kotlin/com/autohotkey/lang/lexer/AutoHotkey.flex"))
    targetDir.set("src/main/gen/com/autohotkey/")
    targetClass.set("AhkLexer")
    purgeOldFiles.set(true)
}

val generateAhkParser = task<GenerateParserTask>("generateAhkParser") {
    sourceFile.set(file("src/main/kotlin/com/autohotkey/lang/parser/AutoHotkey.bnf"))
    targetRoot.set("src/main/gen")
    pathToParser.set("com/autohotkey/lang/parser/AhkParser.java")
    pathToPsiRoot.set("com/autohotkey/lang/psi")
    purgeOldFiles.set(false)
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    withType<KotlinCompile> {
        dependsOn(generateAhkLexer, generateAhkParser)
    }

    patchPluginXml {
        version.set(changelog.version)
        sinceBuild.set(properties("pluginSinceBuild").get())
        untilBuild.set(properties("pluginUntilBuild").get())
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
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
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
                    minimum = "0.66".toBigDecimal()
                }
            }
        }
    }

    runPluginVerifier {
        ideVersions.set(
            properties("pluginVerifierIdeVersions").get().split(',').map(String::trim).filter(String::isNotEmpty),
        )
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
