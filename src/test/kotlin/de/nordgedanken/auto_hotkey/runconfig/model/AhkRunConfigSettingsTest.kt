package de.nordgedanken.auto_hotkey.runconfig.model

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.jdom.Element
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import util.TestUtil.parseXmlFileToElement
import util.toCompactXmlString

class AhkRunConfigSettingsTest : FunSpec({
    context("verify custom getters") {
        context("getEnabledSwitchesAsList") {
            forAll<Pair<AhkRunConfigSettings, List<String>>>(
                "default" to Pair(AhkRunConfigSettings(), listOf("/ErrorStdOut")),
                "empty switch list" to Pair(
                    AhkRunConfigSettings(switches = mutableMapOf()),
                    emptyList()
                ),
                "one enabled switch" to Pair(
                    AhkRunConfigSettings(switches = mutableMapOf(AhkSwitch.ERROR_STD_OUT to true)),
                    listOf("/ErrorStdOut")
                ),
                "one disabled switch" to Pair(
                    AhkRunConfigSettings(switches = mutableMapOf(AhkSwitch.ERROR_STD_OUT to false)),
                    emptyList()
                )
            ) { (settings, expectedList) ->
                settings.getEnabledSwitchesAsList() shouldContainExactly expectedList
            }
        }

        context("getArgsAsList") {
            forAll<Pair<AhkRunConfigSettings, List<String>>>(
                "default" to Pair(AhkRunConfigSettings(), emptyList()),
                "regular args" to Pair(
                    AhkRunConfigSettings(arguments = """arg1 arg2"""),
                    listOf("arg1", "arg2")
                ),
                "quoted args" to Pair(
                    AhkRunConfigSettings(arguments = """arg1 "ar g2" arg3"""),
                    listOf("arg1", "ar g2", "arg3")
                )
            ) { (settings, expectedList) ->
                settings.getArgsAsList() shouldContainExactly expectedList
            }
        }
    }

    context("test xml operations") {
        test("test read from xml") {
            val actualElement = parseXmlFileToElement("samplerunconfigsettings")
            val actualSettings = AhkRunConfigSettings().apply {
                readFromElement(actualElement)
            }

            val expectedSettings = AhkRunConfigSettings(
                runner = "AutoHotkey",
                pathToScript = """C:\my\test\path""",
                arguments = "test1 test2"
            )
            actualSettings shouldBe expectedSettings
        }

        test("test read from empty xml") {
            val actualSettings = AhkRunConfigSettings().apply {
                readFromElement(Element("configuration"))
            }

            val expectedSettings = AhkRunConfigSettings(
                runner = "",
                pathToScript = "",
                arguments = ""
            )
            actualSettings shouldBe expectedSettings
        }

        test("test write to xml") {
            val testSettings = AhkRunConfigSettings(
                runner = "AutoHotkey",
                pathToScript = """C:\my\test\path""",
                arguments = "test1 test2"
            )
            val actualElement = Element("configuration")
            testSettings.writeToElement(actualElement)

            val expectedElement = parseXmlFileToElement("samplerunconfigsettings")
            actualElement.toCompactXmlString() shouldBe expectedElement.toCompactXmlString()
        }
    }
})
