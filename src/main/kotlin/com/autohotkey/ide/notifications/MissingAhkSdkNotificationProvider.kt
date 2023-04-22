package com.autohotkey.ide.notifications

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotificationProvider.CONST_NULL
import com.autohotkey.lang.core.isAhkFile
import com.autohotkey.project.configurable.AhkProjectConfigurable
import com.autohotkey.sdk.getAhkSdks
import com.autohotkey.util.AhkBundle
import java.util.function.Function
import javax.swing.JComponent

/**
 * Displays a popup notification across the top of an open Ahk file asking the user to configure an Ahk sdk
 * if there are no Ahk sdks configured within the user's settings.
 *
 * Note: We aren't using ProjectSdkSetupValidator because that doesn't work for non-IntelliJ IDEs
 */
class MissingAhkSdkNotificationProvider : EditorNotificationProvider, DumbAware {
    override fun collectNotificationData(proj: Project, file: VirtualFile): Function<in FileEditor, out JComponent?> {
        if (!file.isAhkFile() || getAhkSdks().isNotEmpty()) return CONST_NULL
        return Function { fileEditor ->
            EditorNotificationPanel(fileEditor).apply {
                text = AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound.message")
                createActionLabel(AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound.actionlabel")) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(proj, AhkProjectConfigurable::class.java)
                }
            }
        }
    }
}
