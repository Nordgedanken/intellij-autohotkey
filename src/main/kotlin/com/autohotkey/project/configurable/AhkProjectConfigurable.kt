package com.autohotkey.project.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.autohotkey.project.settings.ui.AhkSdkToolbarPanel
import com.autohotkey.util.AhkBundle
import com.autohotkey.util.AhkConstants
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
            cell(ahkSdkToolbar).align(AlignX.FILL)
        }
        row {
            label(AhkBundle.msg("settings.ahksdktable.general.info"))
        }
        row {
            comment(AhkBundle.msg("settings.general.thankyou.label")).align(AlignY.BOTTOM)
        }.resizableRow()
    }
}
