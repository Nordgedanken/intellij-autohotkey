package de.nordgedanken.auto_hotkey.project.settings.ui

import com.intellij.ui.components.JBRadioButton
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.AbstractCellEditor
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

object RadioButtonCellEditorRenderer : AbstractCellEditor(), TableCellRenderer, TableCellEditor, ActionListener {
    private val radioButton = JBRadioButton().also {
        it.horizontalAlignment = SwingConstants.CENTER
        it.addActionListener(this)
    }

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        radioButton.isSelected = value == true
        return radioButton
    }

    override fun getTableCellEditorComponent(
        table: JTable?,
        value: Any,
        isSelected: Boolean,
        row: Int,
        column: Int
    ): Component {
        radioButton.isSelected = value == true
        return radioButton
    }

    override fun actionPerformed(e: ActionEvent?) { stopCellEditing() }

    override fun getCellEditorValue(): Any = radioButton.isSelected
}
