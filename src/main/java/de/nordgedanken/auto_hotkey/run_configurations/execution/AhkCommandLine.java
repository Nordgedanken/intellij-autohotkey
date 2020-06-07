package de.nordgedanken.auto_hotkey.run_configurations.execution;

import com.intellij.execution.configurations.CommandLineTokenizer;
import com.intellij.execution.configurations.GeneralCommandLine;

/**
 * Object that contains the setup needed for executing the run config
 */
public class AhkCommandLine extends GeneralCommandLine {
	public AhkCommandLine() {
	}

	/**
	 * Splits given string by standard command line delimiters (taking into account quoted strings, etc),
	 * and adds each argument as a parameter for execution
	 */
	public void addCommandLineArgs(String commandLineArgs) {
		CommandLineTokenizer commandLineTokenizer = new CommandLineTokenizer(commandLineArgs);
		while(commandLineTokenizer.hasMoreTokens()) {
			addParameter(commandLineTokenizer.nextToken());
		}
	}
}
