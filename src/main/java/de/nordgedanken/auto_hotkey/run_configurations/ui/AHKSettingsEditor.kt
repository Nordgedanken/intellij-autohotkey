package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.components.Label
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import de.nordgedanken.auto_hotkey.run_configurations.AHKRunConfiguration
import javax.swing.JComponent


class AHKSettingsEditor(private val project: Project) : SettingsEditor<AHKRunConfiguration>() {
    private val command = CommandLineEditor(project, "")

    override fun resetEditorFrom(s: AHKRunConfiguration) {}

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(s: AHKRunConfiguration) {
    }

    override fun createEditor(): JComponent = panel {
        labeledRow("&Arguments:", command) {
            command(CCFlags.pushX, CCFlags.growX)
        }
    }

    private fun LayoutBuilder.labeledRow(labelText: String, component: JComponent, init: Row.() -> Unit) {
        val label = Label(labelText)
        label.labelFor = component
        row(label) { init() }
    }
}
