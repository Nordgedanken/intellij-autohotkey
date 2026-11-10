package com.autohotkey.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil

object NotificationUtil {
    fun showErrorDialog(
        project: Project,
        title: String,
        message: String,
    ) {
        Messages.showErrorDialog(project, UIUtil.toHtml(message), title)
    }
}
