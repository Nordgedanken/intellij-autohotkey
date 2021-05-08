package de.nordgedanken.auto_hotkey.ide.documentation

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.AhkTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdkAsProjDefault
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test

class AhkDocumentationProviderTest : AhkBasePlatformTestCase() {

    override fun getTestDataPath(): String = AhkTestCase.testResourcesPath

    private fun configureHomePath() {
        (project.defaultAhkSdk as ProjectJdkImpl).homePath =
            "$testDataPath/de/nordgedanken/auto_hotkey/documentation/"
    }

    private fun getFirstPsiElementOfFileWithText(text: String): PsiElement? {
        myFixture.configureByText(AhkFileType, text)
        return myFixture.findElementByText(text, PsiElement::class.java).firstChild
    }

    fun `test getCustomDocumentationElement`() {
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val customDocumentationElement = AhkDocumentationProvider().getCustomDocumentationElement(
            myFixture.editor,
            myFixture.file,
            element,
            0
        )
        customDocumentationElement shouldNotBe null
        customDocumentationElement shouldBe element
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for command`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        url shouldBe listOf("https://www.autohotkey.com/docs/commands/WinSet.htm")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for wrong command`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("WrongCommand")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        url shouldBe null
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternal for command`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("WinSet")
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs
        val bool = AhkDocumentationProvider().handleExternal(element, element)
        bool shouldBe true
        verify {
            BrowserUtil.browse("https://www.autohotkey.com/docs/commands/WinSet.htm")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for variable`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("A_LineNumber")
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        url shouldBe listOf("https://www.autohotkey.com/docs/Variables.htm#LineNumber")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for command`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        doc shouldContain "<title>WinSet - Syntax &amp; Usage | AutoHotkey</title>"
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for variable`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("A_LineNumber")
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        doc shouldContain "The number of the currently executing line within the script"
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternalLink http`() {
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs

        AhkDocumentationProvider().handleExternalLink(null, "http://test", null) shouldBe true

        verify {
            BrowserUtil.browse("http://test")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternalLink local`() {
        mockkStatic(BrowserUtil::class)
        every { BrowserUtil.browse(any<String>()) } just Runs

        AhkDocumentationProvider().let {
            it.handleExternalLink(null, "#test", null) shouldBe true
            it.handleExternalLink(null, "test", null) shouldBe true
        }

        verify(exactly = 0) {
            BrowserUtil.browse(any<String>())
        }
    }

    @Test
    fun `test canFetchDocumentationLink`() {
        AhkDocumentationProvider().let {
            it.canFetchDocumentationLink(null) shouldBe false
            it.canFetchDocumentationLink("#test") shouldBe false
            it.canFetchDocumentationLink("http") shouldBe false
            it.canFetchDocumentationLink("test") shouldBe true
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test fetchExternalDocumentation for function`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("WinSet")
        val doc = AhkDocumentationProvider().fetchExternalDocumentation("WinTitle", element)
        doc shouldContain "<title>WinTitle &amp; Last Found Window | AutoHotkey</title>"
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test fetchExternalDocumentation for wrong function`() {
        configureHomePath()
        val element = getFirstPsiElementOfFileWithText("Wrong")
        val doc = AhkDocumentationProvider().fetchExternalDocumentation("Wrong", element)
        doc shouldContain "Cannot find file in chm file for Wrong"
    }
}
