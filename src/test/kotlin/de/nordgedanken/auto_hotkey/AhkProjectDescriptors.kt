package de.nordgedanken.auto_hotkey

import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.testFramework.LightProjectDescriptor
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType

/**
 * This class contains a number of descriptors that can be used with the
 * [ProjectDescriptor] annotation to seamlessly specify what Descriptor a
 * BasePlatform test should use.
 *
 * @see AhkBasePlatformTestCase
 * @see MissingAhkSdkNotificationProviderTest
 */

object EmptyDescriptor : LightProjectDescriptor()

object WithOneAhkSdk : LightProjectDescriptor() {
    override fun getSdk() = ProjectJdkImpl("Empty Ahk Sdk", AhkSdkType.getInstance())
}
