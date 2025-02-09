package com.autohotkey.ide.documentation

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.AhkTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdkAsProjDefault
import com.autohotkey.lang.core.AhkFileType
import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.sdk.AHK_DOCUMENTATION_URL_V2
import com.intellij.ide.BrowserUtil
import com.intellij.psi.PsiElement
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test
import util.TestUtil
import util.changeHomePathTo
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AhkDocumentationProviderTest : AhkBasePlatformTestCase() {
    override fun getTestDataPath(): String = "${AhkTestCase.TEST_RESOURCES_PATH}/${TestUtil.packagePath()}"

    private fun getFirstPsiElementOfFileWithText(text: String): PsiElement? {
        myFixture.configureByText(AhkFileType, text)
        return myFixture.findElementByText(text, PsiElement::class.java).firstChild
    }

    fun `test getCustomDocumentationElement`() {
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val customDocumentationElement =
            AhkDocumentationProvider().getCustomDocumentationElement(
                myFixture.editor,
                myFixture.file,
                element,
                0,
            )
        assertNotNull(customDocumentationElement)
        assertEquals(element, customDocumentationElement)
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for command`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        assertEquals(listOf("$AHK_DOCUMENTATION_URL_V2/commands/WinSet.htm"), url)
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for wrong command`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("WrongCommand")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        assertNull(url)
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternal for command`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("WinSet")
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs
        val bool = AhkDocumentationProvider().handleExternal(element, element)
        assertTrue(bool)
        verify {
            BrowserUtil.browse("$AHK_DOCUMENTATION_URL_V2/commands/WinSet.htm")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for variable`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("A_LineNumber")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        assertEquals(listOf("$AHK_DOCUMENTATION_URL_V2/Variables.htm#LineNumber"), url)
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for command`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        assertNotNull(doc)
        assertContains(doc!!, "<title>WinSet - Syntax &amp; Usage | AutoHotkey</title>")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for variable`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("A_LineNumber")
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        assertNotNull(doc)
        assertContains(doc!!, "The number of the currently executing line within the script")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternalLink http`() {
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs

        assertTrue(AhkDocumentationProvider().handleExternalLink(null, "http://test", null))

        verify {
            BrowserUtil.browse("http://test")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternalLink local`() {
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs

        AhkDocumentationProvider().let {
            assertTrue(it.handleExternalLink(null, "#test", null))
            assertTrue(it.handleExternalLink(null, "test", null))
        }

        verify(exactly = 0) {
            BrowserUtil.browse(any<String>())
        }
    }

    @Test
    fun `test canFetchDocumentationLink`() {
        AhkDocumentationProvider().let {
            assertFalse(it.canFetchDocumentationLink(null))
            assertFalse(it.canFetchDocumentationLink("#test"))
            assertFalse(it.canFetchDocumentationLink("http"))
            assertTrue(it.canFetchDocumentationLink("test"))
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test fetchExternalDocumentation for function`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val doc = AhkDocumentationProvider().fetchExternalDocumentation("WinTitle", element)
        assertNotNull(doc)
        assertContains(doc!!, "<title>WinTitle &amp; Last Found Window | AutoHotkey</title>")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test fetchExternalDocumentation for wrong function`() {
        project.defaultAhkSdk!!.changeHomePathTo("$testDataPath/")
        val element = getFirstPsiElementOfFileWithText("Wrong")
        val doc = AhkDocumentationProvider().fetchExternalDocumentation("Wrong", element)
        assertNotNull(doc)
        assertContains(doc!!, "Cannot find file in chm file for Wrong")
    }
}
