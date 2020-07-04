package de.nordgedanken.auto_hotkey.run_configurations.core;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.IdeaProjectSettingsService;
import de.nordgedanken.auto_hotkey.AhkConstants;
import de.nordgedanken.auto_hotkey.run_configurations.execution.AhkRunState;
import de.nordgedanken.auto_hotkey.run_configurations.model.AhkRunConfigSettings;
import de.nordgedanken.auto_hotkey.run_configurations.ui.AhkRunConfigSettingsEditor;
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines instances of Ahk run configurations.
 */
@State(
	name = AhkConstants.PLUGIN_NAME,
	storages = {@Storage(AhkConstants.PLUGIN_NAME + "__run-configuration.xml")}
)
public class AhkRunConfig extends RunConfigurationBase<Object> {
	public AhkRunConfigSettings runConfigSettings = new AhkRunConfigSettings();

	protected AhkRunConfig(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
		super(project, factory, name);
	}

	@Override
	public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
		return new AhkRunConfigSettingsEditor(this.getProject());
	}

	@Override
	public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
		Sdk projectSDK = ProjectRootManager.getInstance(getProject()).getProjectSdk();
		//validating the Ahk SDK here. Eventually this should be removed and validation should happen while editing the run config
		if(projectSDK == null || !(projectSDK.getSdkType() instanceof AhkSdkType)) {
			String fullMessage = "SDK Error: You must create an AutoHotkey SDK and select it as the project's default SDK in order to run the AHK script. <br>Otherwise IntelliJ does not know what to use to run your script";
			NotificationUtil.showErrorPopup("Execution Error", fullMessage, getProject(), environment);
			IdeaProjectSettingsService.getInstance(getProject()).openProjectSettings();
		} else if(runConfigSettings.getPathToScript() == null || runConfigSettings.getPathToScript().isEmpty()) {
			String fullMessage = "Error: You must specify the path to the script you want to execute within the run config";
			NotificationUtil.showErrorPopup("Execution Error", fullMessage, getProject(), environment);
		} else {
			//return execution runstate only if everything is good. Else return null so nothing happens
			return new AhkRunState(this, environment);
		}
		return null;
	}

	/**
	 * This READS any prior persisted configuration from the State/Storage defined by this classes annotations.
	 */
	@Override
	public void readExternal(@NotNull Element element) {
		super.readExternal(element);
		runConfigSettings.populateFromElement(element);
	}

	/**
	 * This WRITES/persists configurations TO the State/Storage defined by this classes annotations.
	 */
	@Override
	public void writeExternal(@NotNull Element element) {
		super.writeExternal(element);
		runConfigSettings.writeToElement(element);
	}

	@Override
	public String toString() {
		return "AhkRunConfig{" + "runConfigSettings=" + runConfigSettings + '}';
	}
}
