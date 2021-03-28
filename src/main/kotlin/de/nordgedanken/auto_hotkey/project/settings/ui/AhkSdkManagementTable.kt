package de.nordgedanken.auto_hotkey.project.settings.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.table.JBTable
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkCellEditor
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkTableCellRenderer
import de.nordgedanken.auto_hotkey.util.AhkBundle
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

private val sdkTableColumnNames = arrayOf("AutoHotkey runners", "Default")

class AhkSdkManagementTable(
    project: Project
) : JBTable(AhkSdkTableModel(project)) {
    private val sdkTableCellEditor = AhkSdkCellEditor(project)
    private val ahkSdkCellRenderer = AhkSdkTableCellRenderer(project)

    init {
        setShowGrid(false)
        emptyText.text = AhkBundle.msg("settings.autohotkey.ahkrunners.general.nosdks")
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        tableHeader.resizingAllowed = false
        tableHeader.reorderingAllowed = false
        columnModel.apply {
            getColumn(0).apply {
                cellRenderer = ahkSdkCellRenderer
                cellEditor = sdkTableCellEditor
            }
            getColumn(1).apply {
                cellRenderer = RadioButtonCellEditorRenderer
                cellEditor = RadioButtonCellEditorRenderer
                maxWidth = 15 + tableHeader.getFontMetrics(tableHeader.font).stringWidth(model.getColumnName(1))
            }
        }
    }
}

class AhkSdkTableModel(
    private val project: Project
) : AbstractTableModel() {
    val sdks = getAhkSdks().toMutableList()

    override fun getRowCount() = sdks.size

    override fun getColumnCount() = sdkTableColumnNames.size

    override fun getColumnName(column: Int) = sdkTableColumnNames[column]

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (columnIndex) {
        0 -> sdks[rowIndex]
        1 -> project.defaultAhkSdk === sdks[rowIndex]
        else -> throw IllegalStateException("Unexpected column is trying to get value")
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        when (columnIndex) {
            0 -> sdks[rowIndex].sdkModificator.run {
                name = aValue as String
                commitChanges()
            }
            1 -> {
                project.defaultAhkSdk = sdks[rowIndex]
                fireTableRowsUpdated(0, rowCount)
            }
            else -> throw IllegalStateException("Unexpected column is trying to set value")
        }
    }

    fun addSdk(sdk: Sdk) {
        sdks.add(sdk)
        fireTableRowsInserted(sdks.lastIndex, sdks.lastIndex)
    }

    fun removeSdkAtRow(rowIndex: Int) {
        sdks.removeAt(rowIndex)
        fireTableRowsDeleted(rowIndex, rowIndex)
    }
}
