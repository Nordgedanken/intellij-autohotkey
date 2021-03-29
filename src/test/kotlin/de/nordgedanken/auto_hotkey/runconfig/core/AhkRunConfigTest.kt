package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RuntimeConfigurationException
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import de.nordgedanken.auto_hotkey.runconfig.ui.AhkRunConfigSettingsEditor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

class AhkRunConfigTest : AhkBasePlatformTestCase() {
    fun `test check configuration throws error if no runner selected`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        shouldThrow<RuntimeConfigurationException> {
            ahkRunConfig.checkConfiguration()
        }.message shouldContain "The script runner does not point to a valid AutoHotkey runner!"
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration throws error if file doesn't exist`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        shouldThrow<RuntimeConfigurationException> {
            ahkRunConfig.checkConfiguration()
        }.message shouldContain "The script path does not exist on disk!"
    }

    fun `test config's editor has type AhkRunConfigSettingsEditor`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.configurationEditor.shouldBeInstanceOf<AhkRunConfigSettingsEditor>()
    }

    private fun generateEmptyAhkRunconfig(): AhkRunConfig {
        val factory = AhkRunConfigType.getInstance().factory
        val templateConfig = RunManager.getInstance(project).getConfigurationTemplate(factory).configuration
        return factory.createConfiguration("test config", templateConfig) as AhkRunConfig
    }
}