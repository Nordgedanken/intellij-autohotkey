package de.nordgedanken.auto_hotkey.run_configurations.core;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import de.nordgedanken.auto_hotkey.run_configurations.execution.AhkRunState;
import de.nordgedanken.auto_hotkey.run_configurations.ui.AhkRunConfigSettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AhkRunConfig extends RunConfigurationBase<Object> {
	public String scriptPath;

	protected AhkRunConfig(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
		super(project, factory, name);
	}

	@Override
	public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
		return new AhkRunConfigSettingsEditor();
	}

	@Override
	public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
		return new AhkRunState(environment, this);
	}
}
