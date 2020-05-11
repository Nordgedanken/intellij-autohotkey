package de.nordgedanken.auto_hotkey.run_configurations

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import de.nordgedanken.auto_hotkey.run_configurations.ui.AHKSettingsEditor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


class AHKRunConfiguration constructor(project: Project, factory: ConfigurationFactory?, name: String?) : RunConfigurationBase<Any?>(project, factory, name) {
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
            AHKSettingsEditor(project)

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
    }

    @Nullable
    @Throws(ExecutionException::class)
    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return null
    }
}
