package de.nordgedanken.auto_hotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.settings.AhkProjectConfigurable
import de.nordgedanken.auto_hotkey.util.AhkBundle

/**
 * Displays a popup notification across the top of an open Ahk file asking the user to configure an Ahk sdk
 * if there are no Ahk sdks configured within the user's settings.
 *
 * Note: We aren't using ProjectSdkSetupValidator because that doesn't work for non-IntelliJ IDEs
 */
class MissingAhkSdkNotificationProvider : AhkNotificationProvider(), DumbAware {
    override fun getKey(): Key<EditorNotificationPanel> = PROVIDER_KEY

    /**
     * Only creates the notification panel if the user currently has an Ahk file
     * open and has no Ahk sdks configured in their settings
     */
    override fun createNotificationPanel(
        file: VirtualFile,
        editor: FileEditor,
        project: Project
    ): AhkEditorNotificationPanel? {
        if (file.fileType != AhkFileType) return null
        if (getAhkSdks().isNotEmpty()) return null
        return AhkEditorNotificationPanel(NO_AHK_SDK_PANEL_ID).apply {
            setText(AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound.message"))
            createActionLabel(AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound.actionlabel")) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, AhkProjectConfigurable::class.java)
            }
        }
    }

    companion object {
        const val NO_AHK_SDK_PANEL_ID = "NoAhkSdk"
        private val PROVIDER_KEY: Key<EditorNotificationPanel> = Key.create("Set up AutoHotkey runner")
    }
}
