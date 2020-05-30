package de.nordgedanken.auto_hotkey.run_configurations.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AhkCommandLineTest {

	@Test
	void testAddCommandLineArgs() {
		AhkCommandLine ahkCommandLine = new AhkCommandLine();
		final String args = "arg1 \"arg2 extended\"";
		ahkCommandLine.addCommandLineArgs(args);
		assertArrayEquals(new String[]{"arg1", "arg2 extended"}, ahkCommandLine.getParametersList().getParameters().toArray());
	}
}
