package de.nordgedanken.auto_hotkey.run_configurations.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
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
		if(projectSDK == null || !(projectSDK.getSdkType() instanceof AhkSdkType)) {
			//this condition should not occur since we check before execution.
			return new KillableProcessHandler(new GeneralCommandLine(""));
		}
		String exePath = Paths.get(Objects.requireNonNull(projectSDK.getHomePath()), "AutoHotkey.exe").toAbsolutePath().toString();

		GeneralCommandLine ahkCommandLine = new GeneralCommandLine()
				.withWorkDirectory(ahkRunConfig.getProject().getBasePath())
				.withExePath(exePath)
				.withParameters(Objects.requireNonNull(ahkRunConfig.runConfigSettings.getPathToScript()))
				.withParameters(ahkRunConfig.runConfigSettings.getArgsAsList());

		return new KillableProcessHandler(ahkCommandLine);
	}
}
