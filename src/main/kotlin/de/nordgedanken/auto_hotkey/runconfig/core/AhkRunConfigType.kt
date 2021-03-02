package de.nordgedanken.auto_hotkey.runconfig.core

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons
import org.jetbrains.annotations.Nls

/**
 * Creates a new type of run config to select when choosing a run config template. Registered in plugin.xml
 */
class AhkRunConfigType : ConfigurationType {
    override fun getDisplayName() = AhkConstants.LANGUAGE_NAME

    override fun getConfigurationTypeDescription(): @Nls String {
        return "AutoHotkey Run Configuration Type"
    }

    override fun getIcon() = AhkIcons.FILE

    override fun getId() = "AHK_RUN_CONFIGURATION"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(AhkRunConfigFactory(this))
    }
}
