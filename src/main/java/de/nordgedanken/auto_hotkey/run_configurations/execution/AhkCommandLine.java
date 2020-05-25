package de.nordgedanken.auto_hotkey.run_configurations.execution;

import com.intellij.execution.configurations.CommandLineTokenizer;
import com.intellij.execution.configurations.GeneralCommandLine;

public class AhkCommandLine extends GeneralCommandLine {
	public AhkCommandLine(String exePath, String scriptName, CommandLineTokenizer commandLineArgs) {
		setExePath(exePath);
		addParameter(scriptName);
		while(commandLineArgs.hasMoreTokens()) {
			addParameter(commandLineArgs.nextToken());
		}
	}
}
