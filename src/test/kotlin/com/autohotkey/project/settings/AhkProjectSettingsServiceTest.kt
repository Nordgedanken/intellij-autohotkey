package com.autohotkey.project.settings

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.AhkTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.WithOneAhkSdkAsProjDefault
import com.autohotkey.mockAhkSdk
import com.autohotkey.mockAhkSdk2
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.projectRoots.ProjectJdkTable
import util.TestUtil
import util.TestUtil.parseXmlFileToElement
import util.toXmlString
import kotlin.test.assertNull
import kotlin.test.assertSame

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
        assertNull(myFixture.project.defaultAhkSdk)
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test default ahk sdk set if none read in loadState() and one exists in project`() {
        val savedProjSettingsState = parseXmlFileToElement("empty-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        assertSame(mockAhkSdk, myFixture.project.defaultAhkSdk)
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test default ahk sdk set if one read in loadState() and same exists in project`() {
        val savedProjSettingsState = parseXmlFileToElement("with-default-ahk-sdk-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        assertSame(mockAhkSdk, myFixture.project.defaultAhkSdk)
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test correct default ahk sdk set if one read in loadState() and two exist in project`() {
        WriteAction.run<Throwable> { ProjectJdkTable.getInstance().addJdk(mockAhkSdk2, testRootDisposable) }
        val savedProjSettingsState = parseXmlFileToElement("with-default-ahk-sdk2-state")
        myFixture.project.service<AhkProjectSettingsService>().loadState(savedProjSettingsState)
        assertSame(mockAhkSdk2, myFixture.project.defaultAhkSdk)
    }

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        return super.getTestName(lowercaseFirstLetter).substringAfterLast(' ')
    }

    override fun getTestDataPath(): String = "${AhkTestCase.TEST_RESOURCES_PATH}/${TestUtil.packagePath()}"
}
