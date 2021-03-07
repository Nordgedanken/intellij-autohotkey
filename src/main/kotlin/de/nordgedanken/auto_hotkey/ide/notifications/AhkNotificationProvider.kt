package de.nordgedanken.auto_hotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications

/**
 * Simple base class for NotificationProviders to extend
 */
abstract class AhkNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {
    /**
     * We are setting up this override so that we can trace the debugId of the panel to help
     * with assertions during testing
     */
    abstract override fun createNotificationPanel(
        file: VirtualFile,
        editor: FileEditor,
        project: Project
    ): AhkEditorNotificationPanel?
}