package de.nordgedanken.auto_hotkey.sdk.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import de.nordgedanken.auto_hotkey.sdk.ahkExeName
import de.nordgedanken.auto_hotkey.sdk.isAhkSdk
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkIcons

/**
 * Defines methods for the standard render of an ahk sdk within a SimpleColoredComponent. If the value isn't an ahk sdk,
 * it will render an alternative error string.
 */

/**
 * Will render the given value if one of three conditions match:
 * 1. value is ahk sdk -> normal rendering with icon, version, and path
 * 2. value is string -> error rendering with unrecognized sdk message
 * 3. value is null -> error rendering with no sdk
 * else it throws an exception since this case should never occur
 */
fun SimpleColoredComponent.renderGivenSdk(value: Any?, isProjectSdk: Boolean = false) {
    when {
        value is Sdk && value.isAhkSdk() -> {
            icon = AhkIcons.EXE
            renderSdkDetails(value)
            if (isProjectSdk) {
                append(
                    "  [${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.default")}]",
                    SimpleTextAttributes.SYNTHETIC_ATTRIBUTES
                )
            }
        }
        value is String -> {
            icon = AllIcons.Nodes.PpInvalid
            append(
                "$value <${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.unrecognized")}>",
                SimpleTextAttributes.ERROR_ATTRIBUTES
            )
        }
        value === null -> {
            icon = AllIcons.Nodes.PpInvalid
            append(
                "<${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.noneselected")}>",
                SimpleTextAttributes.ERROR_ATTRIBUTES
            )
        }
        else -> throw IllegalStateException(
            "ColoredComponent was asked to render unexpected value: $value, ${value::class.qualifiedName}"
        )
    }
}

/**
 * Adds 3 strings into the SimpleColoredComponent's rendering for the given sdk: sdk's name, version, and home path
 */
private fun SimpleColoredComponent.renderSdkDetails(sdk: Sdk) = run {
    append(sdk.name)
    append(" (${sdk.versionString})", SimpleTextAttributes.GRAY_ATTRIBUTES)
    append(" <${sdk.homePath}/${sdk.ahkExeName()}>", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
}
