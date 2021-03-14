package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import de.nordgedanken.auto_hotkey.runconfig.execution.AhkRunState
import de.nordgedanken.auto_hotkey.runconfig.model.AhkRunConfigSettings
import de.nordgedanken.auto_hotkey.runconfig.ui.AhkRunConfigSettingsEditor
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkConstants
import org.jdom.Element
import java.io.File

/**
 * Defines instances of Ahk run configurations.
 */
class AhkRunConfig(
    project: Project,
    factory: ConfigurationFactory,
    name: String?
) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var runConfigSettings = AhkRunConfigSettings()

    override fun suggestedName(): String = runConfigSettings.pathToScript.substringAfterLast('/')

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return AhkRunConfigSettingsEditor(this.project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return AhkRunState(this, environment)
    }

    /**
     * Verifies that the run config settings are valid. (Prevents execution if not valid)
     */
    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
        if (getAhkSdkByName(runConfigSettings.runner) == null) {
            throw RuntimeConfigurationError(AhkBundle.msg("runconfig.configtab.error.scriptrunner.notahksdktype"))
        } else {
            val f = File(runConfigSettings.pathToScript)
            if (!f.exists()) {
                throw RuntimeConfigurationError(AhkBundle.msg("runconfig.configtab.error.scriptpath.notexist"))
            } else if (f.extension != AhkConstants.FILE_EXTENSION) {
                throw RuntimeConfigurationError(AhkBundle.msg("runconfig.configtab.error.scriptpath.notahkextension"))
            }
        }
    }

    /**
     * This READS any prior persisted configuration from the State/Storage defined by this classes annotations.
     */
    override fun readExternal(element: Element) {
        super.readExternal(element)
        runConfigSettings.readFromElement(element)
    }

    /**
     * This WRITES/persists configurations TO the State/Storage defined by this classes annotations.
     */
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        runConfigSettings.writeToElement(element)
    }

    /**
     * Must override clone so that a deep-copy of runConfigSettings is made when generating from template
     */
    override fun clone() = (super.clone() as AhkRunConfig).apply {
        runConfigSettings = runConfigSettings.clone()
    }
}
