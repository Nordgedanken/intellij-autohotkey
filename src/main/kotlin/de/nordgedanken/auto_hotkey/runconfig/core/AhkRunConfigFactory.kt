package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import de.nordgedanken.auto_hotkey.util.AhkConstants

/**
 * Produces new Ahk run configurations
 */
class AhkRunConfigFactory(configurationType: AhkRunConfigType) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return AhkRunConfig(project, this, AhkConstants.LANGUAGE_NAME)
    }

    override fun getId() = AhkConstants.LANGUAGE_NAME
}
