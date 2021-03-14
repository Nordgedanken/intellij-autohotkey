package de.nordgedanken.auto_hotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileType
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.ProjectDescriptor
import de.nordgedanken.auto_hotkey.WithOneAhkSdk
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import com.intellij.openapi.fileTypes.PlainTextFileType.INSTANCE as PlainTextFileType

/**
 * Tests whether the MissingAhkSdkNotificationProvider shows the notification in the correct scenarios.
 *
 * (Most of the code obtained from Rust plugin)
 */
class MissingAhkSdkNotificationProviderTest : AhkBasePlatformTestCase() {
    private val notificationProvider = MissingAhkSdkNotificationProvider()

    fun `test notification doesn't show if non-ahk file opened with no sdks set up`() {
        verifyIfOpeningFileTypeShowsPanel(PlainTextFileType, null)
    }

    fun `test notification shows if ahk file opened with no sdks set up`() {
        verifyIfOpeningFileTypeShowsPanel(AhkFileType, MissingAhkSdkNotificationProvider.NO_AHK_SDK_PANEL_ID)
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test notification doesn't show if ahk file opened with one sdk set up`() {
        verifyIfOpeningFileTypeShowsPanel(AhkFileType, null)
    }

    /**
     * Creates an empty file of the passed-in type and opens it. Then it tries to create a notification panel with the
     * notificationProvider and checks whether the id returned matches the expected id that should be returned
     */
    private fun verifyIfOpeningFileTypeShowsPanel(fileType: FileType, expectedId: String?) {
        val file = myFixture.configureByText(fileType, "")!!.virtualFile
        val editor = FileEditorManager.getInstance(project).openFile(file, true)[0]
        val actualId = notificationProvider.createNotificationPanel(file, editor, project)?.debugId
        val message = when {
            actualId == null && expectedId != null -> "The notification panel was not shown even though it was expected"
            actualId != null && expectedId == null -> "The notification panel was shown even though it shouldn't have"
            else -> ""
        }
        message.asClue {
            actualId shouldBe expectedId
        }
    }
}
