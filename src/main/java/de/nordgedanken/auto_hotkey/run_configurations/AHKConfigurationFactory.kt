package de.nordgedanken.auto_hotkey.run_configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

private const val FACTORY_NAME = "AutoHotKey configuration factory"

class AHKConfigurationFactory constructor(type: ConfigurationType?) : ConfigurationFactory(type!!) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return AHKRunConfiguration(project, this, "AutoHotKey")
    }

    override fun getName(): String {
        return FACTORY_NAME
    }

    override fun getId(): String {
        return FACTORY_NAME
    }
}
