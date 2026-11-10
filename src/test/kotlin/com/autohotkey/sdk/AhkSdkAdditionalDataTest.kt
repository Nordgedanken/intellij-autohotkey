package com.autohotkey.sdk

import org.jdom.Element
import org.junit.jupiter.api.Test
import util.TestUtil.parseXmlFileToElement
import util.toCompactXmlString
import kotlin.test.assertEquals

class AhkSdkAdditionalDataTest {
    @Test
    fun `test default constructor`() {
        assertEquals(DEFAULT_AHK_EXE_NAME, AhkSdkAdditionalData().exeName)
    }

    @Test
    fun `test write`() {
        val actualElement = Element("AhkSdkAdditionalData")
        AhkSdkAdditionalData("test.exe").writeTo(actualElement)

        assertEquals(
            parseXmlFileToElement("ahksdkadditionaldata_testexe").toCompactXmlString(),
            actualElement.toCompactXmlString(),
        )
    }

    @Test
    fun `test read`() {
        val actualAhkSdkAdditionalData =
            AhkSdkAdditionalData.generateFrom(parseXmlFileToElement("ahksdkadditionaldata_testexe"))

        assertEquals(AhkSdkAdditionalData("test.exe"), actualAhkSdkAdditionalData)
    }

    @Test
    fun `test read with empty xml`() {
        val actualAhkSdkAdditionalData =
            AhkSdkAdditionalData.generateFrom(parseXmlFileToElement("ahksdkadditionaldata"))

        assertEquals(AhkSdkAdditionalData(), actualAhkSdkAdditionalData)
    }
}
