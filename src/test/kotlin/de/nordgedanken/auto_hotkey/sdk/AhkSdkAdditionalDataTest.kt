package de.nordgedanken.auto_hotkey.sdk

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jdom.Element
import util.TestUtil.parseXmlFileToElement
import util.toCompactXmlString

class AhkSdkAdditionalDataTest : FunSpec(
    {
        test("default constructor") {
            AhkSdkAdditionalData().exeName shouldBe DEFAULT_AHK_EXE_NAME
        }

        context("test read/write operations") {
            test("test write") {
                val actualElement = Element("AhkSdkAdditionalData")
                AhkSdkAdditionalData("test.exe").writeTo(actualElement)
                val expectedElement = parseXmlFileToElement("ahksdkadditionaldata_testexe")
                actualElement.toCompactXmlString() shouldBe expectedElement.toCompactXmlString()
            }

            test("test read") {
                val ahkSdkAdditionalDataElem = parseXmlFileToElement("ahksdkadditionaldata_testexe")
                val actualAhkSdkAdditionalData = AhkSdkAdditionalData.generateFrom(ahkSdkAdditionalDataElem)
                val expectedAhkSdkAdditionalData = AhkSdkAdditionalData("test.exe")
                actualAhkSdkAdditionalData shouldBe expectedAhkSdkAdditionalData
            }

            test("test read with empty xml") {
                val ahkSdkAdditionalDataElem = parseXmlFileToElement("ahksdkadditionaldata")
                val actualAhkSdkAdditionalData = AhkSdkAdditionalData.generateFrom(ahkSdkAdditionalDataElem)
                val expectedAhkSdkAdditionalData = AhkSdkAdditionalData()
                actualAhkSdkAdditionalData shouldBe expectedAhkSdkAdditionalData
            }
        }
    }
)
