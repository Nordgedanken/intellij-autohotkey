package com.autohotkey.project.settings

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.AhkTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.WithOneAhkSdkAsProjDefault
import com.autohotkey.mockAhkSdk
import com.autohotkey.mockAhkSdk2
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeSameInstanceAs
import util.TestUtil
import util.TestUtil.parseXmlFileToElement
import util.toXmlString

class AhkProjectSettingsServiceTest : AhkBasePlatformTestCase(), AhkTestCase {

    fun `test that if no default ahk sdk set, getState matches empty-state`() {
        val state = myFixture.project.service<AhkProjectSettingsService>().state
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", state.toXmlString())
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test that if default ahk sdk set, getState matches with-default-ahk-sdk-state`() {
        val state = myFixture.project.service<AhkProjectSettingsService>().state
        assertSameLinesWithFile("$testDataPath/${getTestName(true)}.xml", state.toXmlString())
    }

    fun `test no default ahk sdk set if none read in loadState() and none exist in project`() {
        val savedProjSettingsState = parseXmlFileToElement("empty-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        myFixture.project.defaultAhkSdk.shouldBeNull()
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test default ahk sdk set if none read in loadState() and one exists in project`() {
        val savedProjSettingsState = parseXmlFileToElement("empty-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        myFixture.project.defaultAhkSdk shouldBeSameInstanceAs mockAhkSdk
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test default ahk sdk set if one read in loadState() and same exists in project`() {
        val savedProjSettingsState = parseXmlFileToElement("with-default-ahk-sdk-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        myFixture.project.defaultAhkSdk shouldBeSameInstanceAs mockAhkSdk
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test correct default ahk sdk set if one read in loadState() and two exist in project`() {
        WriteAction.run<Exception> { ProjectJdkTable.getInstance().addJdk(mockAhkSdk2, testRootDisposable) }
        val savedProjSettingsState = parseXmlFileToElement("with-default-ahk-sdk2-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        myFixture.project.defaultAhkSdk shouldBeSameInstanceAs mockAhkSdk2
    }

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        return super.getTestName(lowercaseFirstLetter).substringAfterLast(' ')
    }

    override fun getTestDataPath(): String = "${AhkTestCase.testResourcesPath}/${TestUtil.packagePath()}"
}
