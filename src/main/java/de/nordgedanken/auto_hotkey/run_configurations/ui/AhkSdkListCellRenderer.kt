package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import de.nordgedanken.auto_hotkey.localization.AhkBundle
import javax.swing.JList

class AhkSdkListCellRenderer constructor(private val projectSdk: Sdk?) : ColoredListCellRenderer<Any>() {


    override fun customizeCellRenderer(list: JList<out Any>, value: Any?, index: Int, selected: Boolean, hasFocus: Boolean) {
        when (value) {
            is Sdk -> {
                icon = (value.sdkType as SdkType).icon //should always be Ahk exe icon
                setupComboBoxEntry(value)
                if(value == projectSdk) {
                    append("  [${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.default")}]", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                }
            }
            is String -> {
                icon = AllIcons.Nodes.PpInvalid
                append("$value <${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.unrecognized")}>", SimpleTextAttributes.ERROR_ATTRIBUTES)
            }
            null -> {
                icon = AllIcons.Nodes.PpInvalid
                append("<${AhkBundle.msg("runconfig.configtab.scriptrunner.sdklabel.noneselected")}>", SimpleTextAttributes.ERROR_ATTRIBUTES)
            }
        }
    }

    private fun setupComboBoxEntry(sdk: Sdk) {
        val sdkName = sdk.name
        append(sdkName)
        val sdkVersion = sdk.versionString
        append(" ($sdkVersion)", SimpleTextAttributes.GRAY_ATTRIBUTES)
        val sdkHomePath = sdk.homePath
        append(" <$sdkHomePath>", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
    }
}

