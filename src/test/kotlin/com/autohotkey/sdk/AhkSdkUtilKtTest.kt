package com.autohotkey.sdk

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.mockAhkSdk
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.projectRoots.impl.UnknownSdkType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AhkSdkUtilKtTest : AhkBasePlatformTestCase() {
    fun `test getAhkSdkByName for no sdks`() {
        assertNull(getAhkSdkByName("Test sdk"))
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test getAhkSdkByName for ahk sdk that doesn't match given name`() {
        assertNull(getAhkSdkByName("Test sdk"))
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test getAhkSdkByName for ahk sdk that matches given name`() {
        assertEquals(mockAhkSdk, getAhkSdkByName("Mock Ahk Sdk"))
    }

    fun `test isAhkSdk`() {
        assertTrue(mockAhkSdk.isAhkSdk())
        val mockNonAhkSdk = ProjectJdkImpl("Mock Non Ahk Sdk", UnknownSdkType.getInstance(""))
        assertFalse(mockNonAhkSdk.isAhkSdk())
    }

    fun `test ahkExeName`() {
        assertEquals(DEFAULT_AHK_EXE_NAME, mockAhkSdk.ahkExeName())
    }

    fun `test ahkDocumentationUrl`() =
        mockAhkSdk.run {
            sdkModificator.run {
                versionString = "1.1.33.07"
                WriteAction.run<Throwable>(::commitChanges)
            }
            assertEquals(AHK_DOCUMENTATION_URL_V1, ahkDocUrlBase)

            sdkModificator.run {
                versionString = "2.0-a133"
                WriteAction.run<Throwable>(::commitChanges)
            }
            assertEquals(AHK_DOCUMENTATION_URL_V2, ahkDocUrlBase)
        }
}
