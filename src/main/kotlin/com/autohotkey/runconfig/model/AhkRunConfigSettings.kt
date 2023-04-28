package com.autohotkey.runconfig.model

import com.intellij.execution.configurations.CommandLineTokenizer
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element

val SWITCH = "switch"
val NAME = "name"
val ENABLED = "enabled"
val DEFAULT_SWITCHES: MutableMap<AhkSwitch, Boolean> = mutableMapOf(AhkSwitch.ERROR_STD_OUT to true)

/**
 * Simply contains the settings for a run config.
 *
 * Contains some convenience methods for usage in the plugin since we do not need to keep an
 * anemic domain model & aren't using a distributed architecture
 * See https://softwareengineering.stackexchange.com/a/359557
 */
data class AhkRunConfigSettings(
    var runner: String = "",
    var switches: MutableMap<AhkSwitch, Boolean> = DEFAULT_SWITCHES,
    var pathToScript: String = "",
    var arguments: String = "",
) : Cloneable {
    /**
     * Returns all enabled switches as a list of strings
     */
    fun getEnabledSwitchesAsList(): List<String> {
        return switches.entries.filter { it.value }.map { it.key.switchName }.toList()
    }

    /**
     * Returns the arguments field as a list of properly-delimited strings,
     * accounting for things like multi-word arguments in quotes.
     */
    fun getArgsAsList(): List<String> {
        val argsList = mutableListOf<String>()
        with(CommandLineTokenizer(arguments)) {
            while (hasMoreTokens()) {
                argsList.add(nextToken())
            }
        }
        return argsList
    }

    fun readFromElement(element: Element) {
        runner = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::runner.name).orEmpty()
        pathToScript = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::pathToScript.name).orEmpty()
        arguments = JDOMExternalizerUtil.readField(element, AhkRunConfigSettings::arguments.name).orEmpty()
        switches = readSwitchesFromElement(element)
    }

    private fun readSwitchesFromElement(parentElement: Element): MutableMap<AhkSwitch, Boolean> {
        val switchesElement = parentElement.getChild(AhkRunConfigSettings::switches.name) ?: return DEFAULT_SWITCHES
        return switchesElement.getChildren(SWITCH)
            .map { Pair(it.getAttributeValue(NAME), it.getAttributeValue(ENABLED)) }
            .filter { (switchName, _) -> AhkSwitch.isValidSwitch(switchName) }
            .associate { Pair(AhkSwitch.valueOfBySwitchName(it.first), it.second?.toBoolean() ?: false) }
            .toMutableMap()
    }

    fun writeToElement(element: Element) {
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::runner.name, runner)
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::pathToScript.name, pathToScript)
        JDOMExternalizerUtil.writeField(element, AhkRunConfigSettings::arguments.name, arguments)
        writeSwitchesToElement(element)
    }

    private fun writeSwitchesToElement(parentElement: Element) {
        val switchesElement = Element(AhkRunConfigSettings::switches.name)
        switches.entries.forEach { entry ->
            Element(SWITCH).run {
                setAttribute(NAME, entry.key.switchName)
                setAttribute(ENABLED, entry.value.toString())
                switchesElement.addContent(this)
            }
        }
        parentElement.addContent(switchesElement)
    }

    public override fun clone() = (super.clone() as AhkRunConfigSettings).apply {
        switches = switches.toMap().toMutableMap()
    }
}
