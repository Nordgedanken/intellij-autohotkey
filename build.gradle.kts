import org.gradle.kotlin.dsl.register
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.grammarKit)
    jacoco
    alias(libs.plugins.ktlint)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// Include the generated files in the source set
sourceSets.main
    .get()
    .java
    .srcDirs("src/main/gen")

repositories {
    mavenCentral()

    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(libs.junit4)
    testImplementation(libs.bundles.kotest) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    }
    testImplementation(libs.mockk) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    }
    testRuntimeOnly(libs.junit.engine) {
        because(
            "this is needed to run parsing/lexing tests which extend " +
                "intellij base classes that use junit4",
        )
    }

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion"),
        )

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description =
            File("$rootDir/README.md")
                .readText()
                .lines()
                .run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n")
                .let(::markdownToHTML)

        val changelog = project.changelog // local variable for configuration cache compatibility
        changeNotes =
            provider {
                var newChangeNotes =
                    changelog.renderItem(
                        (changelog.getOrNull(version.get()) ?: changelog.getUnreleased()).withHeader(true),
                        Changelog.OutputType.HTML,
                    )
                check(newChangeNotes.contains("(compatibility:")) {
                    "Latest change notes must specify the compatibility range of the plugin!"
                }
                newChangeNotes +=
                    """Please see <a href=
                        |"https://github.com/Nordgedanken/intellij-autohotkey/blob/master/CHANGELOG.md"
                        |>CHANGELOG.md</a> for a full list of changes.
                    """.trimMargin()
                return@provider newChangeNotes
            }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = provider { null } // intentionally blank to allow unlimited future support
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels =
            providers
                .gradleProperty("pluginVersion")
                .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

ktlint {
    enableExperimentalRules.set(true)
}

changelog {
    groups.set(listOf("Added"))
}

qodana {
    cachePath.set(projectDir.resolve(".qodana").canonicalPath)
    reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
}

val generateAhkLexer =
    tasks.register<GenerateLexerTask>("generateAhkLexer") {
        sourceFile.set(file("src/main/kotlin/com/autohotkey/lang/lexer/AutoHotkey.flex"))
        targetOutputDir.set(file("src/main/gen/com/autohotkey/"))
        purgeOldFiles.set(true)
    }

val generateAhkParser =
    tasks.register<GenerateParserTask>("generateAhkParser") {
        sourceFile.set(file("src/main/kotlin/com/autohotkey/lang/parser/AutoHotkey.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("com/autohotkey/lang/parser/AhkParser.java")
        pathToPsiRoot.set("com/autohotkey/lang/psi")
        purgeOldFiles.set(false)
    }

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }

    // testing-related stuff below
    test {
        useJUnitPlatform()
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    withType<KotlinCompile> {
        dependsOn(generateAhkLexer, generateAhkParser)
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
                    minimum = "0.83".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    minimum = "0.66".toBigDecimal()
                }
            }
        }
    }
}

fun setClassesToIncludeInCoverageCheck(classDirectories: ConfigurableFileCollection) {
    // packages listed here can't be tested
    val packagesToExcludeFromCoverageCheck =
        listOf(
            "**/autohotkey/runconfig/execution/**",
            "**/autohotkey/util/**",
            // swing ui packages; must be tested manually
            "**/autohotkey/runconfig/ui/**",
            "**/autohotkey/sdk/ui/**",
            "**/autohotkey/project/configurable/**",
            "**/autohotkey/project/settings/ui/**",
        )

    // files listed here can't be tested, but the package wasn't excluded since other files within it can be tested
    val filesToExcludeFromCoverageCheck =
        listOf(
            // pattern must be specified with trailing asterisk to include extension fns
            "**/autohotkey/sdk/AhkSdkType*",
            "**/autohotkey/ide/actions/AhkCreateFileAction*",
            "**/autohotkey/ide/highlighter/AhkColorSettingsPage*",
        )

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(packagesToExcludeFromCoverageCheck + filesToExcludeFromCoverageCheck)
        },
    )
}
