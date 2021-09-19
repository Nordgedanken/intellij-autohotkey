package de.nordgedanken.auto_hotkey.project.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import com.intellij.uiDesigner.core.Spacer
import de.nordgedanken.auto_hotkey.project.settings.ui.AhkProjectSettingsPanel
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkConstants

/**
 * This project configurable is the backend support for the AutoHotkey settings that are present when you click on
 * "AutoHotkey" in the Settings pane, which is under "Language & Frameworks". Handles saving the settings, calling the
 * UI renderer, etc.
 */
class AhkProjectConfigurable(
    project: Project
) : Configurable {
    private val ahkProjectSettingsPanel = AhkProjectSettingsPanel(project)

    override fun isModified(): Boolean = false

    override fun getDisplayName() = AhkConstants.LANGUAGE_NAME

    override fun apply() {
    }

    override fun createComponent() = panel {
        ahkProjectSettingsPanel.attachTo(this)
        row {
            Spacer()(pushY)
        }
        row {
            comment(AhkBundle.msg("settings.general.thankyou.label"), 150)
        }
    }
}
