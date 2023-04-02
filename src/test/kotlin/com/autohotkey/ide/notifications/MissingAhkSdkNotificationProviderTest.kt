package com.autohotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.ui.EditorNotificationPanel
import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.ProjectDescriptor
import com.autohotkey.WithOneAhkSdk
import com.autohotkey.lang.core.AhkFileType
import com.autohotkey.util.AhkBundle
import io.kotest.matchers.nulls.shouldBeNull
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
        val panel = getPanelAfterOpeningFileOfType(PlainTextFileType)
        panel.shouldBeNull()
    }

    fun `test notification shows if ahk file opened with no sdks set up`() {
        val panel = getPanelAfterOpeningFileOfType(AhkFileType)
        panel!!.text shouldBe AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound.message")
    }

    @ProjectDescriptor(WithOneAhkSdk::class)
    fun `test notification doesn't show if ahk file opened with one sdk set up`() {
        val panel = getPanelAfterOpeningFileOfType(AhkFileType)
        panel.shouldBeNull()
    }

    /**
     * Creates an empty file of the passed-in type and opens it. Then we trigger our notification provider on the opened
     * file and return the panel (or null) that the provider would return for that file.
     */
    private fun getPanelAfterOpeningFileOfType(fileType: FileType): EditorNotificationPanel? {
        val file = myFixture.configureByText(fileType, "")!!.virtualFile
        val editor = FileEditorManager.getInstance(project).openFile(file, true)[0]
        return notificationProvider.collectNotificationData(project, file)?.apply(editor) as EditorNotificationPanel?
    }
}
