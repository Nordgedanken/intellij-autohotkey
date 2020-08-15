package de.nordgedanken.auto_hotkey.settings.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import de.nordgedanken.auto_hotkey.localization.AhkBundle
import javax.swing.JPanel

/**
 * Contains the UI rendering logic of all the settings that are supported by AhkProjectConfigurable
 */
class AhkProjectSettingsPanel(project: Project) {
    private val ahkSdkToolbar: JPanel = AhkSdkToolbarPanel(project).panel

    fun attachTo(layoutBuilder: LayoutBuilder) = with(layoutBuilder) {
        row {
            cell(true) {
                label(AhkBundle.msg("settings.autohotkey.ahkrunners.general.label"))
                ahkSdkToolbar(pushX, growX)
            }
        }
        row {
            label(AhkBundle.msg("settings.autohotkey.ahkrunners.general.info"))
        }
    }
}
