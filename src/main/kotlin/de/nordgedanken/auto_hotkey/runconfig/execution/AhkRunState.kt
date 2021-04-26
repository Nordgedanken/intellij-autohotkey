package de.nordgedanken.auto_hotkey.runconfig.execution

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.RegexpFilter
import com.intellij.execution.filters.RegexpFilter.FILE_PATH_MACROS
import com.intellij.execution.filters.RegexpFilter.LINE_MACROS
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfig
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import java.nio.file.Paths

/**
 * Determines the setup of the command line when the user chooses to run an ahk run config. A console filter is also
 * added here to add a file link in the console if AutoHotkey prints an error concerning a particular file.
 */
class AhkRunState(
    private val ahkRunConfig: AhkRunConfig,
    environment: ExecutionEnvironment?
) : CommandLineState(environment) {
    init {
        addConsoleFilters(RegexpFilter(ahkRunConfig.project, "$FILE_PATH_MACROS \\($LINE_MACROS\\)"))
    }

    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler {
        val exePath = Paths.get(
            getAhkSdkByName(ahkRunConfig.runConfigSettings.runner)!!.homePath!!,
            "AutoHotkey.exe"
        ).toString()

        val ahkCommandLine = GeneralCommandLine()
            .withWorkDirectory(ahkRunConfig.project.basePath)
            .withExePath(exePath)
            .withParameters(ahkRunConfig.runConfigSettings.getEnabledSwitchesAsList())
            .withParameters(ahkRunConfig.runConfigSettings.pathToScript)
            .withParameters(ahkRunConfig.runConfigSettings.getArgsAsList())
        return KillableProcessHandler(ahkCommandLine)
    }
}
