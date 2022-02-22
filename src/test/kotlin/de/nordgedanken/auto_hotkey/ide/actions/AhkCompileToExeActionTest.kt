package de.nordgedanken.auto_hotkey.ide.actions

import com.intellij.ide.DataManager
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.wm.ToolWindowBalloonShowOptions
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.impl.ToolWindowHeadlessManagerImpl
import com.intellij.testFramework.TemporaryDirectory
import com.intellij.testFramework.replaceService
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdkAsProjDefault
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.sdk.ahkDocUrlBase
import de.nordgedanken.auto_hotkey.util.AhkBundle
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.spyk
import java.nio.file.Files
import java.nio.file.Path

class AhkCompileToExeActionTest : AhkBasePlatformTestCase() {
    private val TEST_AHK_SCRIPT_FILENAME = "test.ahk"

    fun `test action does not show if triggered from a non-ahk file`() {
        val resultPresentation = myFixture.testAction(AhkCompileToExeAction())
        resultPresentation.isEnabledAndVisible.shouldBeFalse()
    }

    fun `test error balloon shown if no ahk sdk configured`() {
        val balloonToCapture = mockToolWindowManagerAndCaptureBalloonNotification()
        configureCompileToExeActionToBeCalledFromFakeAhkScript()
        myFixture.testAction(AhkCompileToExeAction())
        balloonToCapture.captured.run {
            type shouldBe MessageType.ERROR
            htmlBody shouldBe AhkBundle.msg("compiletoexeaction.error.norunnerconfigured")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test error balloon shown if no ahk2exe file present`() {
        val balloonToCapture = mockToolWindowManagerAndCaptureBalloonNotification()
        configureCompileToExeActionToBeCalledFromFakeAhkScript()
        myFixture.testAction(AhkCompileToExeAction())
        balloonToCapture.captured.run {
            type shouldBe MessageType.ERROR
            htmlBody shouldBe AhkBundle.msg("compiletoexeaction.error.noahk2exeexists")
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test error balloon shown if compilation fails`() {
        val balloonToCapture = mockToolWindowManagerAndCaptureBalloonNotification()
        configureCompileToExeActionToBeCalledFromFakeAhkScript()
        val sdkHomeDir = TemporaryDirectory.generateTemporaryPath("")
        createFakeAhk2ExeFileWithin(sdkHomeDir)
        (project.defaultAhkSdk as ProjectJdkImpl).homePath = sdkHomeDir.toString()

        val mockProcess = mockProcessBuilderWithATerminatingMockProcess()
        every { mockProcess.exitValue() } returns 1

        myFixture.testAction(AhkCompileToExeAction())
        balloonToCapture.captured.run {
            type shouldBe MessageType.ERROR
            htmlBody shouldBe AhkBundle.msg("compiletoexeaction.error.errorrunningahk2exe")
                .format(TEST_AHK_SCRIPT_FILENAME)
        }
    }

    @ProjectDescriptor(WithOneAhkSdkAsProjDefault::class)
    fun `test success balloon shown if compilation successful`() {
        val balloonToCapture = mockToolWindowManagerAndCaptureBalloonNotification()
        configureCompileToExeActionToBeCalledFromFakeAhkScript()
        val sdkHomeDir = TemporaryDirectory.generateTemporaryPath("")
        createFakeAhk2ExeFileWithin(sdkHomeDir)
        (project.defaultAhkSdk as ProjectJdkImpl).homePath = sdkHomeDir.toString()

        val mockProcess = mockProcessBuilderWithATerminatingMockProcess()
        every { mockProcess.exitValue() } returns 0

        myFixture.testAction(AhkCompileToExeAction())
        balloonToCapture.captured.run {
            type shouldBe MessageType.INFO
            htmlBody shouldBe AhkBundle.msg("compiletoexeaction.success.message")
                .format(project.defaultAhkSdk!!.ahkDocUrlBase)
        }
    }

    private fun mockToolWindowManagerAndCaptureBalloonNotification() = slot<ToolWindowBalloonShowOptions>().also {
        val spyToolWindowManager = spyk(ToolWindowHeadlessManagerImpl(project))
        every { spyToolWindowManager.notifyByBalloon(options = capture(it)) } just Runs
        project.replaceService(ToolWindowManager::class.java, spyToolWindowManager, testRootDisposable)
    }

    private fun configureCompileToExeActionToBeCalledFromFakeAhkScript() {
        val fakeAhkScriptFile = myFixture.configureByText(TEST_AHK_SCRIPT_FILENAME, "")!!.virtualFile
        // Make a fake data context that pretends it was generated from the fake script
        val dataContextWScript = DataContext {
            return@DataContext when (it) {
                CommonDataKeys.PROJECT.name -> project
                CommonDataKeys.VIRTUAL_FILE.name -> fakeAhkScriptFile
                else -> null
            }
        }
        // Inject a mock DataManager to provide our fake data context when requested from the action event
        val spyDataManager = spyk(HeadlessDataManager())
        every { spyDataManager.dataContext } returns dataContextWScript
        ApplicationManager.getApplication().replaceService(DataManager::class.java, spyDataManager, testRootDisposable)
    }

    private fun createFakeAhk2ExeFileWithin(dir: Path) = dir.resolve("Compiler").resolve("Ahk2Exe.exe").let {
        Files.createDirectories(it.parent)
        Files.createFile(it)
    }

    private fun mockProcessBuilderWithATerminatingMockProcess(): Process {
        mockkConstructor(ProcessBuilder::class)
        return mockk<Process>().also {
            every { anyConstructed<ProcessBuilder>().start() } returns it
            every { it.waitFor(any(), any()) } returns true
        }
    }
}
