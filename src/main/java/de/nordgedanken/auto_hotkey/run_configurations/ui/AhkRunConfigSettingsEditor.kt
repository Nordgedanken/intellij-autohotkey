package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import de.nordgedanken.auto_hotkey.AhkConstants
import de.nordgedanken.auto_hotkey.localization.AhkBundle
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig
import javax.swing.JComponent

/**
 * Contains the settings that are shown when editing a run configuration.
 *
 * (If you're new to Kotlin, createEditor() will seem weird but it's just using
 * the UI DSL format to construct the UI instead of a .form file)
 */
class AhkRunConfigSettingsEditor(private val project: Project) : SettingsEditor<AhkRunConfig>() {
    private val pathToScriptTextField: TextFieldWithBrowseButton = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(AhkBundle.msg("runconfig.configtab.scriptpath.filechooser.title"),
                AhkBundle.msg("runconfig.configtab.scriptpath.filechooser.message"),
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor(AhkConstants.FILE_EXTENSION))
    }
    private val argumentsTextField: ExpandableTextField = ExpandableTextField()

    override fun resetEditorFrom(s: AhkRunConfig) {
        pathToScriptTextField.text = s.runConfigSettings.pathToScript.toString()
        argumentsTextField.text = s.runConfigSettings.arguments
    }

    override fun applyEditorTo(s: AhkRunConfig) {
        s.runConfigSettings.pathToScript = pathToScriptTextField.text
        s.runConfigSettings.arguments = argumentsTextField.text
    }

    override fun createEditor(): JComponent = panel {
        row {
            tabbedPane(CCFlags.growX, CCFlags.pushX) {
                outlinedTab(AhkBundle.msg("runconfig.configtab.label")) {
                    row(AhkBundle.msg("runconfig.configtab.scriptpath.label")) { pathToScriptTextField() }
                    row(AhkBundle.msg("runconfig.configtab.scriptargs.label")) { argumentsTextField(growX, pushX) }
                    row(AhkBundle.msg("runconfig.configtab.ahksdk.label")) {
                        label(AhkBundle.msg("runconfig.configtab.ahksdk.info"))
                    }
                    noteRow(AhkBundle.msg("runconfig.general.info.label"))
                }
            }
        }
    }
}
