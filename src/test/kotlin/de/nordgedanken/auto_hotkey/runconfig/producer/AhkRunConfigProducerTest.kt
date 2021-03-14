package de.nordgedanken.auto_hotkey.runconfig.producer

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.AhkTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import de.nordgedanken.auto_hotkey.WithOneAhkSdkAsProjDefault
import de.nordgedanken.auto_hotkey.WithOneJavaSdkAsProjDefault
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfig
import io.kotest.matchers.shouldBe
import org.jdom.Element
import util.TestUtil.packagePath
import util.toXmlString
import com.intellij.openapi.fileTypes.PlainTextFileType.INSTANCE as PlainTextFileType

class AhkRunConfigProducerTest : AhkBasePlatformTestCase(), AhkTestCase {
    private val ahkRunConfigProducer = AhkRunConfigProducer()

    fun `test producer makes no config for non-ahk file`() {
        val configurations = generateContextAhkRunconfigsFromFile("test.txt", "test")
        configurations.size shouldBe 0
    }

    fun `test producer makes no config for empty ahk file`() {
        val configurations = generateContextAhkRunconfigsFromFile("test.ahk", "")
        configurations.size shouldBe 0
    }

    fun `test producer makes no config for ahk file with no code`() {
        val configurations = generateContextAhkRunconfigsFromFile("test.ahk", ";test comment")
        configurations.size shouldBe 0
    }

    fun `test producer makes config with no sdk for proj w no sdk matching hello-world-no-sdk`() {
        val configurations = generateContextAhkRunconfigsFromFile("hello-world.ahk", "Msgbox Hi")
        val runConfigXml = generateXmlStringFromRunConfigs(configurations)
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", runConfigXml)
    }

    @ProjectDescriptor(WithOneJavaSdkAsProjDefault::class)
    fun `test producer makes config with no sdk for proj w other sdk set as default matching hello-world-no-sdk`() {
        val configurations = generateContextAhkRunconfigsFromFile("hello-world.ahk", "Msgbox Hi")
        val runConfigXml = generateXmlStringFromRunConfigs(configurations)
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", runConfigXml)
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test producer makes config w proj sdk for proj w ahkSdk set as default matching hello-world-with-sdk`() {
        val configurations = generateContextAhkRunconfigsFromFile("hello-world.ahk", "Msgbox Hi")
        val runConfigXml = generateXmlStringFromRunConfigs(configurations)
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", runConfigXml)
    }

    /**
     * In this scenario, the project contains some other sdk as the default, but the user has configured an ahk sdk too.
     */
    @ProjectDescriptor(WithOneJavaSdkAsProjDefault::class)
    fun `test producer makes config w ahk sdk for proj w other sdk set as default matching hello-world-with-sdk`() {
        WriteAction.run<Exception> { ProjectJdkTable.getInstance().addJdk(WithOneAhkSdk.sdk, testRootDisposable) }
        val configurations = generateContextAhkRunconfigsFromFile("hello-world.ahk", "Msgbox Hi")
        val runConfigXml = generateXmlStringFromRunConfigs(configurations)
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", runConfigXml)
    }

    fun `test producer reuses old config when making from the same context twice`() {
        myFixture.configureByText(AhkFileType, "Msgbox Hi")
        val ctx1 = myFixture.findElementByText("Msgbox", PsiElement::class.java)
        val ctx2 = myFixture.findElementByText("Hi", PsiElement::class.java)
        val contexts = listOf(ConfigurationContext(ctx1), ConfigurationContext(ctx2))
        val configs = contexts.map { it.configurationsFromContext!!.single() }.map { it.configuration as AhkRunConfig }

        ahkRunConfigProducer.run {
            isConfigurationFromContext(configs[0], contexts[1]) shouldBe true
            isConfigurationFromContext(configs[1], contexts[0]) shouldBe true
        }
    }

    fun `test producer doesn't reuse old config when making from a different file`() {
        val ahkRunConfig = generateContextAhkRunconfigsFromFile("hello-world.ahk", "Msgbox Hi")
            .single().configuration as AhkRunConfig
        val otherContextDiffAhkFile = myFixture.makeConfigContextFrom(AhkFileType, "Msgbox Hi from another file")
        val otherContextNonAhkFile = myFixture.makeConfigContextFrom(PlainTextFileType, "Msgbox Hi from another file")

        ahkRunConfigProducer.isConfigurationFromContext(ahkRunConfig, otherContextDiffAhkFile) shouldBe false
        ahkRunConfigProducer.isConfigurationFromContext(ahkRunConfig, otherContextNonAhkFile) shouldBe false
    }

    fun `test producer doesn't reuse old config if the arguments have changed`() {
        val ctx = myFixture.makeConfigContextFrom(AhkFileType, "Msgbox Hi")
        val ahkRunConfig = ctx.configurationsFromContext!!.single().configuration as AhkRunConfig
        ahkRunConfig.runConfigSettings.arguments = "arg1"

        ahkRunConfigProducer.isConfigurationFromContext(ahkRunConfig, ctx) shouldBe false
    }

    /**
     * Creates the passed-in file with passed-in contents, opens it in memory, and then creates a ConfigurationContext
     * from it and generates the relevant run configs which are returned.
     *
     * Note: Must specify the filename here because that will affect the values in the run config
     */
    private fun generateContextAhkRunconfigsFromFile(
        filename: String,
        fileContent: String
    ): List<RunnerAndConfigurationSettings> {
        myFixture.addFileToProject(filename, fileContent)
        myFixture.configureFromExistingVirtualFile(myFixture.findFileInTempDir(filename))
        val element = myFixture.file.run { firstChild ?: originalFile }
        return ConfigurationContext(element).configurationsFromContext.orEmpty().map { it.configurationSettings }
    }

    private fun generateXmlStringFromRunConfigs(runconfigs: List<RunnerAndConfigurationSettings>): String {
        val root = Element("configurations")
        runconfigs.forEach {
            val content = (it as RunnerAndConfigurationSettingsImpl).writeScheme()
            root.addContent(content)
        }
        return root.toXmlString()
    }

    override fun getTestDataPath(): String = "${AhkTestCase.testResourcesPath}/${packagePath()}"

    /**
     * We want to grab the run config xml that matches the last word in the test's name
     */
    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        return super.getTestName(lowercaseFirstLetter).substringAfterLast(' ')
    }

    fun CodeInsightTestFixture.makeConfigContextFrom(fileType: FileType, fileContent: String) =
        ConfigurationContext(this.configureByText(fileType, fileContent))
}
