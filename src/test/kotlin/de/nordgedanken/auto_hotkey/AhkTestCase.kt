package de.nordgedanken.auto_hotkey

import java.nio.file.Path

/**
 * Defines convenience methods to access test resources during tests
 */
interface AhkTestCase {
    fun getTestDataPath(): String
    fun getTestName(lowercaseFirstLetter: Boolean): String

    companion object {
        const val testResourcesPath = "src/test/resources"

        @JvmStatic
        fun camelOrWordsToSnake(name: String): String {
            if (' ' in name) return name.trim().replace(" ", "_")

            return name.split("(?=[A-Z])".toRegex()).joinToString("_", transform = String::lowercase)
        }
    }
}

fun AhkTestCase.pathToSourceTestFile(): Path =
    java.nio.file.Paths.get("${AhkTestCase.testResourcesPath}/${getTestDataPath()}/${getTestName(true)}.ahk")

fun AhkTestCase.pathToGoldTestFile(): Path =
    java.nio.file.Paths.get("${AhkTestCase.testResourcesPath}/${getTestDataPath()}/${getTestName(true)}.txt")
