package com.autohotkey.sdk

import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.mockAhkSdk
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AhkSdkUtilKtTest : AhkBasePlatformTestCase() {
    fun `test getAhkSdkByName for no sdks`() {
        getAhkSdkByName("Test sdk").shouldBeNull()
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test getAhkSdkByName for ahk sdk that doesn't match given name`() {
        getAhkSdkByName("Test sdk").shouldBeNull()
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test getAhkSdkByName for ahk sdk that matches given name`() {
        getAhkSdkByName("Mock Ahk Sdk") shouldBe mockAhkSdk
    }

    fun `test isAhkSdk`() {
        mockAhkSdk.isAhkSdk().shouldBeTrue()
        val mockNonAhkSdk = ProjectJdkImpl("Mock Non Ahk Sdk", null)
        mockNonAhkSdk.isAhkSdk().shouldBeFalse()
    }

    fun `test ahkExeName`() {
        mockAhkSdk.ahkExeName() shouldBe DEFAULT_AHK_EXE_NAME
    }

    fun `test ahkDocumentationUrl`() = mockAhkSdk.run {
        versionString = "1.1.33.07"
        ahkDocUrlBase shouldBe AHK_DOCUMENTATION_URL_V1
        versionString = "2.0-a133"
        ahkDocUrlBase shouldBe AHK_DOCUMENTATION_URL_V2
    }
}
