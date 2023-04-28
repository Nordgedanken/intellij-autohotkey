package com.autohotkey.project.settings.ui

import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.sdk.getAhkSdks
import com.autohotkey.sdk.ui.AhkSdkCellEditor
import com.autohotkey.sdk.ui.AhkSdkTableCellRenderer
import com.autohotkey.util.AhkBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.table.JBTable
import java.awt.event.MouseEvent
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

enum class SdkTableColumns(val index: Int, val title: String, val tooltip: String?) {
    DEFAULT(
        0,
        AhkBundle.msg("settings.ahksdktable.general.defaultcolumn.label"),
        AhkBundle.msg("settings.ahksdktable.general.defaultcolumn.tooltip"),
    ),
    SDK_INFO(
        1,
        AhkBundle.msg("settings.ahksdktable.general.sdkcolumn.label"),
        null,
    ),
}

/**
 * Table that displays the current ahk sdks registered in the project and allows the user to select a default. Offers
 * convenience methods to insert/delete from the table that an outside component can call.
 *
 * (See example in AhkSdkToolbarPanel.kt)
 */
class AhkSdkManagementTable(
    project: Project,
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
            getColumn(SdkTableColumns.DEFAULT.index).apply {
                cellRenderer = RadioButtonCellEditorRenderer
                cellEditor = RadioButtonCellEditorRenderer
                maxWidth = 15 + tableHeader.getFontMetrics(tableHeader.font).stringWidth(model.getColumnName(1))
            }
            getColumn(SdkTableColumns.SDK_INFO.index).apply {
                cellRenderer = ahkSdkCellRenderer
                cellEditor = sdkTableCellEditor
            }
        }
    }

    override fun createDefaultTableHeader() = object : JBTableHeader() {
        override fun getToolTipText(event: MouseEvent): String? = SdkTableColumns.values().first {
            it.index == columnAtPoint(event.point)
        }.tooltip
    }
}

/**
 * Model that backs the data displayed in the table. Calling the add/remove methods here will update the display in the
 * table.
 *
 * Column DEFAULT: Radio button marking whether the current row is the default sdk
 * Column SDK_INFO: Info about the sdk itself
 */
class AhkSdkTableModel(
    private val project: Project,
) : AbstractTableModel() {
    val sdks = getAhkSdks().toMutableList()

    override fun getRowCount() = sdks.size

    override fun getColumnCount() = SdkTableColumns.values().size

    override fun getColumnName(column: Int): String = SdkTableColumns.values().first { it.index == column }.title

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = when (columnIndex) {
        SdkTableColumns.DEFAULT.index -> project.defaultAhkSdk === sdks[rowIndex]
        SdkTableColumns.SDK_INFO.index -> sdks[rowIndex]
        else -> throw IllegalStateException("Unexpected column is trying to get value")
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        when (columnIndex) {
            SdkTableColumns.DEFAULT.index -> {
                project.defaultAhkSdk = sdks[rowIndex]
                fireTableRowsUpdated(0, rowCount)
            }
            SdkTableColumns.SDK_INFO.index -> sdks[rowIndex].sdkModificator.run {
                name = aValue as String
                commitChanges()
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
