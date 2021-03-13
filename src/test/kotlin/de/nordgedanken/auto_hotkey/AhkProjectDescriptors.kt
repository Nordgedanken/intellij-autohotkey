package de.nordgedanken.auto_hotkey

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ProjectRootManager
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

val mockAhkSdk = ProjectJdkImpl("Mock Ahk Sdk", AhkSdkType.getInstance())

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
        WriteAction.run<Exception> { ProjectRootManager.getInstance(project).projectSdk = mockAhkSdk }
    }

    override fun getSdk() = mockAhkSdk
}

/**
 * ProjectDescriptor with a single java sdk added as the project's default
 */
object WithOneJavaSdkAsProjDefault : LightProjectDescriptor() {
    private lateinit var mockJavaSdk: Sdk

    override fun setUpProject(project: Project, handler: SetupHandler) {
        mockJavaSdk = ProjectJdkImpl("Mock Java Sdk", SimpleJavaSdkType.getInstance())
        super.setUpProject(project, handler)
        WriteAction.run<Exception> { ProjectRootManager.getInstance(project).projectSdk = mockJavaSdk }
    }

    override fun getSdk() = mockJavaSdk
}
