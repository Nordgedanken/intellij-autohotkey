package com.autohotkey.project.settings.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.table.JBTable
import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.sdk.getAhkSdks
import com.autohotkey.sdk.ui.AhkSdkCellEditor
import com.autohotkey.sdk.ui.AhkSdkTableCellRenderer
import com.autohotkey.util.AhkBundle
import java.awt.event.MouseEvent
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

private val sdkTableColumnNames = arrayOf(
    AhkBundle.msg("settings.ahksdktable.general.sdkcolumn.label"),
    AhkBundle.msg("settings.ahksdktable.general.defaultcolumn.label")
)

private val sdkTableColumnToolTips = arrayOf(
    null,
    AhkBundle.msg("settings.ahksdktable.general.defaultcolumn.tooltip")
)

/**
 * Table that displays the current ahk sdks registered in the project and allows the user to select a default. Offers
 * convenience methods to insert/delete from the table that an outside component can call.
 *
 * (See example in AhkSdkToolbarPanel.kt)
 */
class AhkSdkManagementTable(
    project: Project
) : JBTable(AhkSdkTableModel(project)) {
    private val sdkTableCellEditor = AhkSdkCellEditor(project)
    private val ahkSdkCellRenderer = AhkSdkTableCellRenderer(project)

    init {
        setShowGrid(false)
        emptyText.text = AhkBundle.msg("settings.ahksdktable.general.nosdks")
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

    override fun createDefaultTableHeader() = object : JBTableHeader() {
        override fun getToolTipText(event: MouseEvent) = sdkTableColumnToolTips[columnAtPoint(event.point)]
    }
}

/**
 * Model that backs the data displayed in the table. Calling the add/remove methods here will update the display in the
 * table.
 *
 * The 1st column shows the sdk; the 2nd column shows a radio button indicating whether it is the default sdk
 */
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
