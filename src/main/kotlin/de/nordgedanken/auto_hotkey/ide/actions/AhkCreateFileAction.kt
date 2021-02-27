package de.nordgedanken.auto_hotkey.ide.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import de.nordgedanken.auto_hotkey.util.AhkIcons

class AhkCreateFileAction : CreateFileFromTemplateAction(CAPTION, "", AhkIcons.FILE), DumbAware {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(CAPTION).addKind("Empty File", AhkIcons.FILE, "AutoHotkey File")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String {
        return CAPTION
    }

    companion object {
        private const val CAPTION = "AutoHotkey File"
    }
}
