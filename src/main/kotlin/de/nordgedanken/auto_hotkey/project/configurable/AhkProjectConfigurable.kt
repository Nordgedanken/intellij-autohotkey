package de.nordgedanken.auto_hotkey.project.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.panel.ComponentPanelBuilder
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
            // NOTE: We are not using the comment(...) function here because it breaks compatibility between 2020.1 and
            // 2020.3. Once the minimum supported version is 2021.1, this should be converted to a comment(...) call
            ComponentPanelBuilder.createCommentComponent(AhkBundle.msg("settings.general.thankyou.label"), true, 150)()
        }
    }
}
