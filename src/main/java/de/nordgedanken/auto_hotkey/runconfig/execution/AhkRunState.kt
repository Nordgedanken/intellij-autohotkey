package de.nordgedanken.auto_hotkey.runconfig.execution

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfig
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import java.nio.file.Paths

/**
 * Gets the state of a run config when you decide to run it. Decides how execution will happen based on the run config properties.
 */
class AhkRunState(private val ahkRunConfig: AhkRunConfig, environment: ExecutionEnvironment?) : CommandLineState(environment) {
    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler {
        val exePath = Paths.get(getAhkSdkByName(ahkRunConfig.runConfigSettings.runner)!!.homePath!!, "AutoHotkey.exe").toString()
        val ahkCommandLine = GeneralCommandLine()
                .withWorkDirectory(ahkRunConfig.project.basePath)
                .withExePath(exePath)
                .withParameters(ahkRunConfig.runConfigSettings.getEnabledSwitchesAsList())
                .withParameters(ahkRunConfig.runConfigSettings.pathToScript)
                .withParameters(ahkRunConfig.runConfigSettings.getArgsAsList())
        return KillableProcessHandler(ahkCommandLine)
    }
}
