package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import de.nordgedanken.auto_hotkey.run_configurations.core.AhkRunConfig
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder


class AhkRunConfigSettingsEditor(private val project: Project) : SettingsEditor<AhkRunConfig>() {
    private val pathToScriptTextField: TextFieldWithBrowseButton = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener("Select AutoHotkey File",
                "Please select the AutoHotkey script to execute",
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor("ahk"))
    }
    private val argumentsTextField: ExpandableTextField = ExpandableTextField()


    override fun resetEditorFrom(s: AhkRunConfig) {
        pathToScriptTextField.text = s.pathToScript
        argumentsTextField.text = s.arguments
    }

    override fun applyEditorTo(s: AhkRunConfig) {
        s.pathToScript = pathToScriptTextField.text
        s.arguments = argumentsTextField.text
    }

    override fun createEditor(): JComponent {
        val runConfigSettingsPanel = panel {
            row("Script Path:") { pathToScriptTextField() }
            row("Script Arguments:") { argumentsTextField(growX, pushX) }
            row("AutoHotkey Executor:") {
                label("To set the executor, please add an AutoHotkey SDK in \"Project Structure...\" and select it as the active SDK for this project")
            }
            row {
                label("Note: It is recommended to create/use a shortcut to rerun the config (eg Ctrl+Alt+R) during development for quick script reloading.")
            }
        }.apply {
            val lineBorder = BorderFactory.createLineBorder(EditorColorsManager.getInstance().globalScheme.defaultForeground)
            val margin = EmptyBorder(10, 10, 10, 10)
            border = CompoundBorder(lineBorder, margin)
        }
        val runConfigPane = JBTabbedPane().apply {
            tabComponentInsets = Insets(0,0,0,0)
            addTab("Configuration", runConfigSettingsPanel)

        }
        val rootPane = panel {
            row {
                runConfigPane(CCFlags.growX, pushX)
            }
        }
        return rootPane
    }

//    private fun LayoutBuilder.labeledRow(labelText: String, component: JComponent, init: Row.() -> Unit) {
//        val label = Label(labelText)
//        label.labelFor = component
//        row(label) { init() }
//    }
}
