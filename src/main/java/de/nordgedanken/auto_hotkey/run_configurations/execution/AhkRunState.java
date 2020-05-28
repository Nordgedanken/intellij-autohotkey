package de.nordgedanken.auto_hotkey.run_configurations.execution;

import com.intellij.CommonBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.MessageDialogBuilder;
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig;
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * Gets the state of a run config when you decide to run it. Decides how execution will happen based on the run config properties.
 */
public class AhkRunState extends CommandLineState {
	private final AhkRunConfig ahkRunConfig;
	public AhkRunState(AhkRunConfig ahkRunConfig, ExecutionEnvironment environment) {
		super(environment);
		this.ahkRunConfig = ahkRunConfig;
	}

	@Override
	protected @NotNull ProcessHandler startProcess() throws ExecutionException {
		Sdk projectSDK = ProjectRootManager.getInstance(ahkRunConfig.getProject()).getProjectSdk();
		//for now, validating the AutoHotkey SDK here. Eventually this should be removed and validation should happen in the run config
		if(projectSDK == null || !(projectSDK.getSdkType() instanceof AhkSdkType)) {
			MessageDialogBuilder
					.yesNo("SDK Error", "You must create an AutoHotkey SDK and select it as the project default SDK in order to run the AHK script. Otherwise IntelliJ does not know what to use to run your script")
					.yesText(CommonBundle.getOkButtonText())
					.noText(CommonBundle.getCancelButtonText())
					.icon(AllIcons.General.BalloonError)
					.show();
			return new KillableProcessHandler(new GeneralCommandLine(""));
		} else {
			String exePath = Paths.get(Objects.requireNonNull(projectSDK.getHomePath()), "AutoHotkey.exe").toAbsolutePath().toString();

			AhkCommandLine ahkCommandLine = new AhkCommandLine();
			ahkCommandLine.setWorkDirectory(ahkRunConfig.getProject().getBasePath());
			ahkCommandLine.setExePath(exePath);
			ahkCommandLine.addParameter(ahkRunConfig.getPathToScript());
			ahkCommandLine.addCommandLineArgs(ahkRunConfig.getArguments());
			return new KillableProcessHandler(ahkCommandLine);
		}
	}
}
