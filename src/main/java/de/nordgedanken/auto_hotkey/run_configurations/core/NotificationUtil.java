package de.nordgedanken.auto_hotkey.run_configurations.core;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.UIUtil;

public class NotificationUtil {
	public static void showErrorPopup(final String title, final String message, final Project project, final ExecutionEnvironment environment) {
		String toolWindowId = ExecutionManager.getInstance(project).getContentManager().getToolWindowIdByEnvironment(environment);
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		if(toolWindowManager.canShowNotification(toolWindowId)) {
			toolWindowManager.notifyByBalloon(toolWindowId, MessageType.ERROR, message, AllIcons.General.Error, null);
		} else {
			Messages.showErrorDialog(project, UIUtil.toHtml(message), title);
		}
	}
}
