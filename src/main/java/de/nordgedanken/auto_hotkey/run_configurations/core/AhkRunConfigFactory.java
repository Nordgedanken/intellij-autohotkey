package de.nordgedanken.auto_hotkey.run_configurations.core;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AhkRunConfigFactory extends ConfigurationFactory {

	public AhkRunConfigFactory(ConfigurationType configurationType) {
		super(configurationType);
	}

	@Override
	public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
		return new AhkRunConfig(project, this, "AutoHotkey");
	}
}
