package de.nordgedanken.auto_hotkey.sdk

import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import de.nordgedanken.auto_hotkey.mockAhkSdk
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
}
