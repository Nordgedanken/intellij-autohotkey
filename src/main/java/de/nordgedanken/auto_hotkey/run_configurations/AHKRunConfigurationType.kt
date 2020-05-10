package de.nordgedanken.auto_hotkey.run_configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import de.nordgedanken.auto_hotkey.AHKIcons
import javax.swing.Icon

class AHKRunConfigurationType : ConfigurationType {
    override fun getIcon(): Icon {
        return AHKIcons.FILE
    }

    override fun getConfigurationTypeDescription(): String {
        return "AutoHotKey Script"
    }

    override fun getId(): String {
        return "AUTOHOTKEY_RUN_CONFIGURATION"
    }

    override fun getDisplayName(): String {
        return "AutoHotKey"
    }

    override fun getConfigurationFactories(): Array<ConfigurationFactory>? {
        return arrayOf(AHKConfigurationFactory(this))
    }
}
