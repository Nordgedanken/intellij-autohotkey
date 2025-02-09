package com.autohotkey.runconfig.core

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.runconfig.ui.AhkRunConfigSettingsEditor
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RuntimeConfigurationException
import util.TestUtil
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AhkRunConfigTest : AhkBasePlatformTestCase() {
    fun `test check configuration throws error if no runner selected`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        val exception =
            assertFailsWith<RuntimeConfigurationException> {
                ahkRunConfig.checkConfiguration()
            }
        assertTrue(
            exception.message!!.startsWith("The script runner does not point to a valid AutoHotkey runner!"),
        )
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration throws error if file doesn't exist`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        val exception =
            assertFailsWith<RuntimeConfigurationException> {
                ahkRunConfig.checkConfiguration()
            }
        assertEquals(
            "The script path does not exist on disk!",
            exception.message,
        )
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration throws error if file isn't ahk file`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        ahkRunConfig.runConfigSettings.pathToScript = TestUtil.getResourceFile("empty.txt").path
        val exception =
            assertFailsWith<RuntimeConfigurationException> {
                ahkRunConfig.checkConfiguration()
            }
        assertEquals(
            "The script path does not point to an AutoHotkey (.ahk) file!",
            exception.message,
        )
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test check configuration doesn't throw error when requirements met`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.runner = WithOneAhkSdk.sdk.name
        ahkRunConfig.runConfigSettings.pathToScript = TestUtil.getResourceFile("empty.ahk").path
        ahkRunConfig.checkConfiguration()
    }

    fun `test config's editor has type AhkRunConfigSettingsEditor`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        assertIs<AhkRunConfigSettingsEditor>(ahkRunConfig.configurationEditor)
    }

    fun `test getSuggestedName`() {
        val ahkRunConfig = generateEmptyAhkRunconfig()
        ahkRunConfig.runConfigSettings.pathToScript = """C:\test\test.ahk"""
        assertEquals("Run test.ahk", ahkRunConfig.suggestedName())
    }

    fun `test readExternal`() {
        val actualConfigElement = TestUtil.parseXmlFileToElement("runconfig_empty")
        val actualConfig = generateEmptyAhkRunconfig().apply { readExternal(actualConfigElement) }
        assertEquals(generateEmptyAhkRunconfig().runConfigSettings, actualConfig.runConfigSettings)
    }

    private fun generateEmptyAhkRunconfig(): AhkRunConfig {
        val factory = AhkRunConfigType.getInstance().factory
        val templateConfig = RunManager.getInstance(project).getConfigurationTemplate(factory).configuration
        return factory.createConfiguration("test config", templateConfig) as AhkRunConfig
    }
}
