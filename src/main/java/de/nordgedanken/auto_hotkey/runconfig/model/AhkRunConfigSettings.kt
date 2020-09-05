package de.nordgedanken.auto_hotkey.runconfig.model

import com.intellij.execution.configurations.CommandLineTokenizer
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element

/**
 * Simply contains the settings for a run config.
 *
 * Contains some convenience methods for usage in the plugin since we do not need to keep an
 * anemic domain model & aren't using a distributed architecture
 * See https://softwareengineering.stackexchange.com/a/359557
 */
data class AhkRunConfigSettings(var pathToScript: String = "", var arguments: String = "", var runner: String = "") {
    /**
     * Returns the arguments field as a list of properly-delimited strings, accounting for things like multi-word arguments in quotes
     */
    fun getArgsAsList(): MutableList<String> {
        val argsList = mutableListOf<String>()
        with(CommandLineTokenizer(arguments)) {
            while (hasMoreTokens()) {
                argsList.add(nextToken())
            }
        }
        return argsList
    }

    fun populateFromElement(element: Element) {
        pathToScript = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::pathToScript.name).orEmpty()
        arguments = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::arguments.name).orEmpty()
        runner = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::runner.name).orEmpty()
    }

    fun writeToElement(element: Element) {
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::pathToScript.name, pathToScript)
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::arguments.name, arguments)
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::runner.name, runner)
    }
}
