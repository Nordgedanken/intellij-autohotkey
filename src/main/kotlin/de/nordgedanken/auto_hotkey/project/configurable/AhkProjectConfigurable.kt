package de.nordgedanken.auto_hotkey.project.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import de.nordgedanken.auto_hotkey.project.settings.ui.AhkSdkToolbarPanel
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkConstants
import javax.swing.JPanel

/**
 * This project configurable is the backend support for the AutoHotkey settings that are present when you click on
 * "AutoHotkey" in the Settings pane, which is under "Language & Frameworks". Handles saving the settings, calling the
 * UI renderer, etc.
 */
class AhkProjectConfigurable(
    project: Project
) : Configurable {
    private val ahkSdkToolbar: JPanel = AhkSdkToolbarPanel(project).panel

    override fun isModified(): Boolean = false

    override fun getDisplayName() = AhkConstants.LANGUAGE_NAME

    override fun apply() {
    }

    override fun createComponent() = panel {
        row {
            cell(ahkSdkToolbar).horizontalAlign(HorizontalAlign.FILL)
        }
        row {
            label(AhkBundle.msg("settings.ahksdktable.general.info"))
        }
        row {
            comment(AhkBundle.msg("settings.general.thankyou.label"), 150).verticalAlign(VerticalAlign.BOTTOM)
        }.resizableRow()
    }
}
