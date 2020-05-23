package de.nordgedanken.auto_hotkey.run_configurations.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig;
import org.jetbrains.annotations.NotNull;

public class AhkRunState extends CommandLineState {
	private final String pathToAhkScript;
	public AhkRunState(ExecutionEnvironment environment) {
		super(environment);
		AhkRunConfig environmentAhkRunConfig = (AhkRunConfig) environment.getRunnerAndConfigurationSettings().getConfiguration();
		this.pathToAhkScript = environmentAhkRunConfig.getPathToScript();
	}

	@Override
	protected @NotNull ProcessHandler startProcess() throws ExecutionException {
		AhkCommandLine ahkCommandLine = new AhkCommandLine("C:\\Program Files\\AutoHotkey\\AutoHotkey.exe", pathToAhkScript);
		return new KillableProcessHandler(ahkCommandLine);
	}
}
