package com.autohotkey.ide.actions

import com.autohotkey.lang.core.isAhkFile
import com.autohotkey.project.configurable.AhkProjectConfigurable
import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.project.settings.hasDefaultAhkSdk
import com.autohotkey.sdk.ahkDocUrlBase
import com.autohotkey.util.AhkBundle
import com.autohotkey.util.AhkIcons
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.RevealFileAction.findLocalFile
import com.intellij.ide.actions.RevealFileAction.openDirectory
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.wm.ToolWindowBalloonShowOptions
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.BrowserHyperlinkListener
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.swing.event.HyperlinkEvent

/**
 * Defines the behavior when the user right-clicks an ahk file in the Project Tree and selects "Compile to exe"
 */
class AhkCompileToExeAction : DumbAwareAction(
    AhkBundle.msg("compiletoexeaction.text"),
    AhkBundle.msg("compiletoexeaction.description"),
    AhkIcons.EXE
) {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.isAhkFile() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val toolWindowManager = ToolWindowManager.getInstance(e.project!!)
        if (!e.project!!.hasDefaultAhkSdk()) {
            return toolWindowManager.notifyByBalloon(ERROR_BALLOON_NO_RUNNER_CONFIGURED(e.project!!))
        }
        val defaultAhkSdkHomeDir = File(e.project!!.defaultAhkSdk!!.homePath!!)
        val ahk2ExeFile = defaultAhkSdkHomeDir.resolve("Compiler").resolve("Ahk2Exe.exe")
        if (!ahk2ExeFile.isFile) {
            return toolWindowManager.notifyByBalloon(ERROR_BALLOON_NO_AHK2EXE_EXISTS(defaultAhkSdkHomeDir))
        }
        val scriptToCompile = findLocalFile(e.getData(CommonDataKeys.VIRTUAL_FILE))!!
        // Below required since IDE doesn't instantly save to disk if a quick edit is made before selecting compile
        FileDocumentManager.getInstance().run { saveDocument(getCachedDocument(scriptToCompile)!!) }
        ProcessBuilder(ahk2ExeFile.path, "/in", scriptToCompile.path).directory(defaultAhkSdkHomeDir).start().run {
            val processTerminated = waitFor(60, SECONDS)
            if (!processTerminated || exitValue() != 0) {
                return toolWindowManager.notifyByBalloon(ERROR_BALLOON_ERROR_RUNNING_AHK2EXE(scriptToCompile.name))
            }
            toolWindowManager.notifyByBalloon(SUCCESS_BALLOON(e.project!!.defaultAhkSdk!!.ahkDocUrlBase))
            VfsUtil.markDirtyAndRefresh(true, false, true, scriptToCompile.parent)
        }
    }

    companion object {
        private val ERROR_BALLOON_NO_RUNNER_CONFIGURED = { project: Project ->
            ToolWindowBalloonShowOptions(
                ToolWindowId.PROJECT_VIEW,
                MessageType.ERROR,
                AhkBundle.msg("compiletoexeaction.error.norunnerconfigured"),
                AllIcons.General.Error,
                listener = {
                    if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, AhkProjectConfigurable::class.java)
                    }
                }
            )
        }

        private val ERROR_BALLOON_NO_AHK2EXE_EXISTS = { sdkHomeDir: File ->
            ToolWindowBalloonShowOptions(
                ToolWindowId.PROJECT_VIEW,
                MessageType.ERROR,
                AhkBundle.msg("compiletoexeaction.error.noahk2exeexists"),
                AllIcons.General.Error,
                listener = {
                    if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                        openDirectory(sdkHomeDir)
                    }
                }
            )
        }

        private val ERROR_BALLOON_ERROR_RUNNING_AHK2EXE = { scriptName: String ->
            ToolWindowBalloonShowOptions(
                ToolWindowId.PROJECT_VIEW,
                MessageType.ERROR,
                AhkBundle.msg("compiletoexeaction.error.errorrunningahk2exe").format(scriptName),
                AllIcons.General.Error
            )
        }

        private val SUCCESS_BALLOON = { ahkDocUrl: String ->
            ToolWindowBalloonShowOptions(
                ToolWindowId.PROJECT_VIEW,
                MessageType.INFO,
                AhkBundle.msg("compiletoexeaction.success.message").format(ahkDocUrl),
                listener = BrowserHyperlinkListener.INSTANCE
            )
        }
    }
}
