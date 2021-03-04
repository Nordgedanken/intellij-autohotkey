package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons
import org.jetbrains.annotations.Nls

/**
 * Creates a new type of run config to select when choosing a run config template. Registered in plugin.xml
 */
class AhkRunConfigType : ConfigurationTypeBase(
    AhkConstants.RunConfig.ID,
    AhkConstants.LANGUAGE_NAME,
    AhkConstants.RunConfig.DESCRIPTION,
    AhkIcons.LOGO
) {
    init {
        addFactory(AhkRunConfigFactory(this))
    }

    val factory: ConfigurationFactory get() = configurationFactories.single()

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(AhkRunConfigFactory(this))
    }

    companion object {
        fun getInstance(): AhkRunConfigType =
            ConfigurationTypeUtil.findConfigurationType(AhkRunConfigType::class.java)
    }
}
