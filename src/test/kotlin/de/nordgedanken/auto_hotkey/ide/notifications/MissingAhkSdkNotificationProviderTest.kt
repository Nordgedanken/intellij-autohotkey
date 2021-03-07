package de.nordgedanken.auto_hotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditorManager
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe

/**
 * Tests whether the MissingAhkSdkNotificationProvider shows the notification in the correct scenarios.
 *
 * (Most of the code obtained from Rust plugin)
 */
class MissingAhkSdkNotificationProviderTest : AhkBasePlatformTestCase() {
    private val notificationProvider = MissingAhkSdkNotificationProvider()

    fun `test notification doesn't show if non-ahk file opened with no sdks set up`() {
        doTest("empty.txt", null)
    }

    fun `test notification shows if ahk file opened with no sdks set up`() {
        doTest("empty.ahk", MissingAhkSdkNotificationProvider.NO_AHK_SDK_PANEL_ID)
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test notification doesn't show if ahk file opened with one sdk set up`() {
        doTest("empty.ahk", null)
    }

    /**
     * Creates the passed-in file (with no content) and then opens it. Then it will
     * try to create a notification panel with the provider that the subclass sets
     * and check whether the id returned matches the expected id to return
     */
    private fun doTest(filePath: String, expectedId: String?) {
        myFixture.addFileToProject(filePath, "")!!
        val file = myFixture.findFileInTempDir(filePath)!!
        val editor = FileEditorManager.getInstance(project).openFile(file, true)[0]
        val actualId = notificationProvider.createNotificationPanel(file, editor, project)?.debugId
        val message = when {
            actualId == null && expectedId != null -> "The notification panel was not shown even though it was expected"
            actualId != null && expectedId == null -> "The notification panel was shown even though it should not have been"
            else -> ""
        }
        message.asClue {
            expectedId shouldBe actualId
        }
    }
}


