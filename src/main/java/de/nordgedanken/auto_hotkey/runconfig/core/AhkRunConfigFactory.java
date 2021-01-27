package de.nordgedanken.auto_hotkey.runconfig.core;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import de.nordgedanken.auto_hotkey.util.AhkConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Produces new Ahk run configurations
 */
public class AhkRunConfigFactory extends ConfigurationFactory {

	public AhkRunConfigFactory(ConfigurationType configurationType) {
		super(configurationType);
	}

	@Override
	public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
		return new AhkRunConfig(project, this, AhkConstants.LANGUAGE_NAME);
	}

	@Override
	public @NotNull String getId() {
		return AhkConstants.LANGUAGE_NAME;
	}
}
