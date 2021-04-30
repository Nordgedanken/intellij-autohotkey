package de.nordgedanken.auto_hotkey.ide.documentation

import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdkAsProjDefault
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.io.File


class AhkDocumentationProviderTest : AhkBasePlatformTestCase() {

    override fun getTestDataPath(): String = File("src/test/resources/").absolutePath

    private fun configureHomePath() {
        (project.defaultAhkSdk as ProjectJdkImpl).homePath =
            "$testDataPath/de/nordgedanken/auto_hotkey/documentation/"
    }

    fun `test getCustomDocumentationElement`() {
        myFixture.configureByText(AhkFileType, "WinSet")
        val element = myFixture.findElementByText("WinSet", PsiElement::class.java)
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
        myFixture.configureByText(AhkFileType, "WinSet")
        val element = myFixture.findElementByText("WinSet", PsiElement::class.java).firstChild
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        url shouldBe listOf("https://www.autohotkey.com/docs/commands/WinSet.htm")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test getUrlFor for variable`() {
        configureHomePath()
        myFixture.configureByText(AhkFileType, "A_LineNumber")
        val element = myFixture.findElementByText("A_LineNumber", PsiElement::class.java).firstChild
        val url = AhkDocumentationProvider().getUrlFor(element, element)
        url shouldBe listOf("https://www.autohotkey.com/docs/Variables.htm#LineNumber")
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for command`() {
        configureHomePath()
        myFixture.configureByText(AhkFileType, "WinSet")
        val element = myFixture.findElementByText("WinSet", PsiElement::class.java).firstChild
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        doc shouldContain "<title>WinSet - Syntax &amp; Usage | AutoHotkey</title>"
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test generateDoc for variable`() {
        configureHomePath()
        myFixture.configureByText(AhkFileType, "A_LineNumber")
        val element = myFixture.findElementByText("A_LineNumber", PsiElement::class.java).firstChild
        val doc = AhkDocumentationProvider().generateDoc(element, element)
        doc shouldContain "The number of the currently executing line within the script"
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test handleExternalLink`() {

        AhkDocumentationProvider().let {
            it.handleExternalLink(null, "#test", null) shouldBe true
            it.handleExternalLink(null, "test", null) shouldBe true
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
        (project.defaultAhkSdk as ProjectJdkImpl).homePath = "$testDataPath/de/nordgedanken/auto_hotkey/documentation/"
        myFixture.configureByText(AhkFileType, "WinSet")
        val element = myFixture.findElementByText("WinSet", PsiElement::class.java).firstChild
        val doc = AhkDocumentationProvider().fetchExternalDocumentation("WinTitle", element)
        doc shouldContain "<title>WinTitle &amp; Last Found Window | AutoHotkey</title>"
    }

}
