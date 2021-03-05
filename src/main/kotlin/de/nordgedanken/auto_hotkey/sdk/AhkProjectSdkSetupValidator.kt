package de.nordgedanken.auto_hotkey.sdk

import com.intellij.codeInsight.daemon.ProjectSdkSetupValidator
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.util.AhkBundle

/**
 * Displays a popup notification across the top of an open Ahk file asking the user to configure an Ahk sdk
 * if there are no Ahk sdks configured within the user's settings
 */
object AhkProjectSdkSetupValidator : ProjectSdkSetupValidator {
    override fun isApplicableFor(project: Project, file: VirtualFile): Boolean {
        return file.fileType == AhkFileType
    }

    override fun getErrorMessage(project: Project, file: VirtualFile): String? {
        if (getAhkSdks().isEmpty())
            return AhkBundle.msg("ahksdktype.projectsetup.noahksdksfound")
        return null
    }

    override fun getFixHandler(project: Project, file: VirtualFile): EditorNotificationPanel.ActionHandler {
        return SdkPopupFactory.newBuilder()
            .withProject(project)
            .withSdkTypeFilter { it is AhkSdkType }
            .updateSdkForFile(file)
            .buildEditorNotificationPanelHandler()
    }
}
