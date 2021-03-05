package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons

/**
 * Creates a new type of run config to select when choosing a run config template. Registered in plugin.xml
 */
class AhkRunConfigType : ConfigurationTypeBase(
    ID,
    AhkConstants.LANGUAGE_NAME,
    DESCRIPTION,
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
        const val ID = "AhkRunConfiguration"
        const val DESCRIPTION = "AutoHotkey run configuration"

        fun getInstance(): AhkRunConfigType =
            ConfigurationTypeUtil.findConfigurationType(AhkRunConfigType::class.java)
    }
}
