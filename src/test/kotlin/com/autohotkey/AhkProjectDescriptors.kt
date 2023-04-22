package com.autohotkey

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.testFramework.LightProjectDescriptor
import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.sdk.AhkSdkType

/**
 * This class contains a number of descriptors that can be used with the
 * [ProjectDescriptor] annotation to seamlessly specify what Descriptor a
 * BasePlatform test should use.
 *
 * @see AhkBasePlatformTestCase
 * @see MissingAhkSdkNotificationProviderTest
 */

val mockAhkSdk = ProjectJdkImpl("Mock Ahk Sdk", AhkSdkType.getInstance())
val mockAhkSdk2 = ProjectJdkImpl("Mock Ahk Sdk2", AhkSdkType.getInstance())

object EmptyDescriptor : LightProjectDescriptor()

/**
 * ProjectDescriptor with a single ahk sdk added into the project.
 * (The sdk is not set as the project's default sdk)
 */
object WithOneAhkSdk : LightProjectDescriptor() {
    override fun getSdk() = mockAhkSdk
}

/**
 * ProjectDescriptor with a single ahk sdk added as the project's default
 */
object WithOneAhkSdkAsProjDefault : LightProjectDescriptor() {
    override fun setUpProject(project: Project, handler: SetupHandler) {
        super.setUpProject(project, handler)
        project.defaultAhkSdk = mockAhkSdk
    }

    override fun getSdk() = mockAhkSdk
}
