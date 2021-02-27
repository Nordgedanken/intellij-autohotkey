package de.nordgedanken.auto_hotkey.runconfig.model

/**
 * Contains a list of valid command-line flags that you can provide to
 * AutoHotkey.exe while executing a script.
 *
 * See https://www.autohotkey.com/docs/Scripts.htm#cmd
 */
enum class AhkSwitch(val switchName: String) {
    ERROR_STD_OUT("/ErrorStdOut"),
    ;

    companion object {
        fun isValidSwitch(switchNameToCheck: String): Boolean {
            return enumValues<AhkSwitch>().any { it.switchName == switchNameToCheck }
        }

        /**
         * Throws an exception if the passed-in switch name is not valid
         */
        fun valueOfBySwitchName(switchName: String): AhkSwitch {
            return values().first { it.switchName == switchName }
        }
    }
}
