package de.nordgedanken.auto_hotkey.run_configurations.ui;

import com.intellij.openapi.options.SettingsEditor;
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AhkRunConfigSettingsEditor extends SettingsEditor<AhkRunConfig> {
	private JTabbedPane configPane;
	private JTextField pathToScript;
	private JTextField arguments;
	private JComboBox scriptExecutor;

	@Override
	protected void resetEditorFrom(@NotNull AhkRunConfig s) {

	}

	@Override
	protected void applyEditorTo(@NotNull AhkRunConfig s) {
		s.scriptPath = pathToScript.getText();
		System.out.println("Saving: " + s.scriptPath);
	}

	@Override
	protected @NotNull JComponent createEditor() {
		return configPane;
	}
}
