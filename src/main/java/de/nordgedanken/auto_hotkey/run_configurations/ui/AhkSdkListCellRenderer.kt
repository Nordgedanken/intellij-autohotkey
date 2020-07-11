package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import de.nordgedanken.auto_hotkey.AHKIcons
import de.nordgedanken.auto_hotkey.localization.AhkBundle
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import javax.swing.JList

class AhkSdkListCellRenderer constructor(private val projectSdk: Sdk?) : ColoredListCellRenderer<Any>() {
    /**
     * Render AhkSdks in the combobox list according to setupComboBoxEntry, or some form of error string for the entry otherwise
     */
    override fun customizeCellRenderer(list: JList<out Any>, value: Any?, index: Int, selected: Boolean, hasFocus: Boolean) {
        when {
            value is Sdk && value.sdkType is AhkSdkType -> {
                icon = AHKIcons.EXE
                setupComboBoxEntry(value)
                if(value === projectSdk) {
                    append("  [${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.default")}]", SimpleTextAttributes.SYNTHETIC_ATTRIBUTES)
                }
            }
            value is String -> {
                icon = AllIcons.Nodes.PpInvalid
                append("$value <${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.unrecognized")}>", SimpleTextAttributes.ERROR_ATTRIBUTES)
            }
            value === null -> {
                icon = AllIcons.Nodes.PpInvalid
                append("<${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.noneselected")}>", SimpleTextAttributes.ERROR_ATTRIBUTES)
            }
            else -> throw IllegalStateException("Combobox was asked to render unexpected value: $value, ${value::class.qualifiedName}")
        }
    }

    /**
     * Formats sdk to the following: "sdkName (sdkVersion) <sdkHomePath>"
     */
    private fun setupComboBoxEntry(sdk: Sdk) {
        val sdkName = sdk.name
        append(sdkName)
        val sdkVersion = sdk.versionString
        append(" ($sdkVersion)", SimpleTextAttributes.GRAY_ATTRIBUTES)
        val sdkHomePath = sdk.homePath
        append(" <$sdkHomePath>", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
    }
}

