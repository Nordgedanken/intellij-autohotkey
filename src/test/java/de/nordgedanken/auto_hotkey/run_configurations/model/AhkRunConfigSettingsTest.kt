package de.nordgedanken.auto_hotkey.run_configurations.model

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

internal class AhkRunConfigSettingsTest {

    @Test
    fun getArgsAsList() {
        val runConfigSettings = AhkRunConfigSettings(arguments = "arg1 \"arg2 extended\"")
        assertArrayEquals(arrayOf("arg1", "arg2 extended"), runConfigSettings.getArgsAsList().toTypedArray())
    }
}
