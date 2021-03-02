package de.nordgedanken.auto_hotkey.util

import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.ui.UIUtil

object NotificationUtil {
    fun showErrorPopup(title: String, message: String, project: Project, environment: ExecutionEnvironment) {
        val toolWindowId = RunContentManager.getInstance(project).getToolWindowIdByEnvironment(environment)
        val toolWindowManager = ToolWindowManager.getInstance(project)
        if (toolWindowManager.canShowNotification(toolWindowId)) {
            toolWindowManager.notifyByBalloon(toolWindowId, MessageType.ERROR, message, AllIcons.General.Error, null)
        } else {
            showErrorDialog(project, title, message)
        }
    }

    fun showErrorDialog(project: Project, title: String, message: String) {
        Messages.showErrorDialog(project, UIUtil.toHtml(message), title)
    }
}
