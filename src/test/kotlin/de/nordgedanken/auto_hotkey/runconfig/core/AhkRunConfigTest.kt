package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RuntimeConfigurationException
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import de.nordgedanken.auto_hotkey.runconfig.ui.AhkRunConfigSettingsEditor
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import util.TestUtil

class AhkRunConfigTest : AhkBasePlatformTestCase() {
    fun `test check configuration throws error if no runner selected`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        shouldThrow<RuntimeConfigurationException> {
            ahkRunConfig.checkConfiguration()
        }.message shouldStartWith "The script runner does not point to a valid AutoHotkey runner!"
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration throws error if file doesn't exist`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        shouldThrow<RuntimeConfigurationException> {
            ahkRunConfig.checkConfiguration()
        }.message shouldBe "The script path does not exist on disk!"
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration throws error if file isn't ahk file`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        ahkRunConfig.runConfigSettings.pathToScript = TestUtil.getResourceFile("empty.txt").path
        shouldThrow<RuntimeConfigurationException> {
            ahkRunConfig.checkConfiguration()
        }.message shouldBe "The script path does not point to an AutoHotkey (.ahk) file!"
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration doesn't throw error when requirements met`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        ahkRunConfig.runConfigSettings.pathToScript = TestUtil.getResourceFile("empty.ahk").path
        shouldNotThrowAny { ahkRunConfig.checkConfiguration() }
    }

    fun `test config's editor has type AhkRunConfigSettingsEditor`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.configurationEditor.shouldBeInstanceOf<AhkRunConfigSettingsEditor>()
    }

    fun `test getSuggestedName`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.pathToScript = """C:\test\test.ahk"""
        ahkRunConfig.suggestedName() shouldBe "Run test.ahk"
    }

    fun `test readExternal`() {
        val actualConfigElement = TestUtil.parseXmlFileToElement("runconfig_empty")
        val actualConfig = generateEmptyAhkRunconfig().apply { readExternal(actualConfigElement) }
        actualConfig.runConfigSettings shouldBe generateEmptyAhkRunconfig().runConfigSettings
    }

    private fun generateEmptyAhkRunconfig(): AhkRunConfig {
        val factory = AhkRunConfigType.getInstance().factory
        val templateConfig = RunManager.getInstance(project).getConfigurationTemplate(factory).configuration
        return factory.createConfiguration("test config", templateConfig) as AhkRunConfig
    }
}
