package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.execution.configurations.runConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons

/**
 * Creates a new type of run config to select when choosing a run config template. Registered in plugin.xml
 */
class AhkRunConfigType : SimpleConfigurationType(
    id = ID,
    name = AhkConstants.LANGUAGE_NAME,
    icon = NotNullLazyValue.createValue { AhkIcons.LOGO }
) {
    val factory: ConfigurationFactory get() = configurationFactories.single()

    companion object {
        const val ID = "AhkRunConfiguration"

        fun getInstance() = runConfigurationType<AhkRunConfigType>()
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return AhkRunConfig(project, this, AhkConstants.LANGUAGE_NAME)
    }
}
