package com.autohotkey.ide.actions

import com.autohotkey.util.AhkIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

/** Creates an empty AutoHotkey v2 (.ah2) file. */
class Ahk2CreateFileAction :
    CreateFileFromTemplateAction(CAPTION, "", AhkIcons.FILE),
    DumbAware {
    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder,
    ) {
        builder.setTitle(CAPTION).addKind("Empty file", AhkIcons.FILE, CAPTION)
    }

    override fun getActionName(
        directory: PsiDirectory,
        newName: String,
        templateName: String,
    ) = CAPTION

    companion object {
        private const val CAPTION = "AutoHotkey v2 File"
    }
}
