package de.nordgedanken.auto_hotkey.run_configurations.core;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import de.nordgedanken.auto_hotkey.AHKIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Creates a new type of run config to select when choosing a run config template. Registered in plugin.xml
 */
public class AhkRunConfigType implements ConfigurationType {
	@Override
	public @NotNull String getDisplayName() {
		return "AutoHotkey";
	}

	@Override
	public @Nls String getConfigurationTypeDescription() {
		return "AutoHotkey Run Configuration Type";
	}

	@Override
	public Icon getIcon() {
		return AHKIcons.FILE;
	}

	@Override
	public @NotNull String getId() {
		return "AHK_RUN_CONFIGURATION";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories() {
		return new ConfigurationFactory[]{new AhkRunConfigFactory(this)};
	}
}
