package de.nordgedanken.auto_hotkey.run_configurations.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AhkRunConfigSettingsEditor extends SettingsEditor<AhkRunConfig> {
	private final Project project;
	private JTabbedPane configPane;
	private TextFieldWithBrowseButton pathToScriptTextField;
	private JTextField argumentsTextField;
	private JComboBox scriptExecutor;

	public AhkRunConfigSettingsEditor(Project project) {
		this.project = project;
	}

	@Override
	protected void resetEditorFrom(@NotNull AhkRunConfig s) {
		pathToScriptTextField.setText(s.getPathToScript());
		argumentsTextField.setText(s.getArguments());
	}

	@Override
	protected void applyEditorTo(@NotNull AhkRunConfig s) {
		s.setPathToScript(pathToScriptTextField.getText());
		s.setArguments(argumentsTextField.getText());
	}

	@Override
	protected @NotNull JComponent createEditor() {
		pathToScriptTextField.addBrowseFolderListener("Select AutoHotkey File",
				"Please select the AutoHotkey script to execute",
				project,
				FileChooserDescriptorFactory.createSingleFileDescriptor("ahk"));

		return configPane;
	}
}
