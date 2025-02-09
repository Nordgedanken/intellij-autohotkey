package com.autohotkey.runconfig.model

import org.jdom.Element
import org.junit.jupiter.api.Test
import util.TestUtil.parseXmlFileToElement
import util.toCompactXmlString
import kotlin.test.assertEquals

class AhkRunConfigSettingsTest {
    @Test
    fun `test getEnabledSwitchesAsList`() {
        val testCases =
            listOf(
                AhkRunConfigSettings() to listOf("/ErrorStdOut"),
                AhkRunConfigSettings(switches = mutableMapOf()) to emptyList(),
                AhkRunConfigSettings(
                    switches = mutableMapOf(AhkSwitch.ERROR_STD_OUT to true),
                ) to listOf("/ErrorStdOut"),
                AhkRunConfigSettings(switches = mutableMapOf(AhkSwitch.ERROR_STD_OUT to false)) to emptyList(),
            )

        testCases.forEach { (settings, expectedSwitches) ->
            assertEquals(expectedSwitches, settings.getEnabledSwitchesAsList())
        }
    }

    @Test
    fun `test getArgsAsList`() {
        val testCases =
            listOf(
                AhkRunConfigSettings() to emptyList(),
                AhkRunConfigSettings(arguments = """arg1 arg2""") to listOf("arg1", "arg2"),
                AhkRunConfigSettings(arguments = """arg1 "ar g2" arg3""") to listOf("arg1", "ar g2", "arg3"),
            )

        testCases.forEach { (settings, expectedArguments) ->
            assertEquals(expectedArguments, settings.getArgsAsList())
        }
    }

    @Test
    fun `test read from xml`() {
        val actualSettings =
            AhkRunConfigSettings().apply {
                readFromElement(parseXmlFileToElement("samplerunconfigsettings"))
            }

        val expectedSettings =
            AhkRunConfigSettings(
                runner = "AutoHotkey",
                pathToScript = """C:\my\test\path""",
                arguments = "test1 test2",
            )
        assertEquals(expectedSettings, actualSettings)
    }

    @Test
    fun `test read from empty xml`() {
        val actualSettings =
            AhkRunConfigSettings().apply {
                readFromElement(Element("configuration"))
            }

        assertEquals(AhkRunConfigSettings(runner = "", pathToScript = "", arguments = ""), actualSettings)
    }

    @Test
    fun `test write to xml`() {
        val testSettings =
            AhkRunConfigSettings(
                runner = "AutoHotkey",
                pathToScript = """C:\my\test\path""",
                arguments = "test1 test2",
            )
        val actualElement = Element("configuration")
        testSettings.writeToElement(actualElement)

        assertEquals(
            parseXmlFileToElement("samplerunconfigsettings").toCompactXmlString(),
            actualElement.toCompactXmlString(),
        )
    }
}
