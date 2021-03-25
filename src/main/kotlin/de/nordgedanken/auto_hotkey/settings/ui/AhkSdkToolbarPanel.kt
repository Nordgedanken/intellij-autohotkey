package de.nordgedanken.auto_hotkey.settings.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.jetbrains.rd.util.remove
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.sdk
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkTableCellRenderer
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.awt.Component
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

/**
 * Constructs the UI and logic for the Ahk Sdk toolbar widget within the AutoHotkey settings that allows you to add or
 * remove Sdks. This widget is necessary for non-IDEA IDEs, since they don't have a "Project Structure" dialog that
 * you can use to manage Sdks.
 */
class AhkSdkToolbarPanel(val project: Project) : JPanel() {
    val panel: JPanel
    private val sdkTableCellRenderer = AhkSdkTableCellRenderer(project.sdk)
    private val sdkTableCellEditor = object : DefaultCellEditor(JBTextField()) {
        lateinit var sdkBeingEdited: Sdk

        /**
         * Allows editing to stop and the new sdk name to be saved only if the new name doesn't already exist in the
         * list. (Also see the documentation of this method in the TableCellEditor interface)
         */
        override fun stopCellEditing(): Boolean {
            val newSdkName = cellEditorValue as String
            if (doesGivenNewNameExist(sdkBeingEdited, newSdkName)) {
                NotificationUtil.showErrorDialog(
                    project,
                    AhkBundle.msg("settings.autohotkey.ahkrunners.edit.error.alreadyexists.dialogtitle"),
                    AhkBundle.msg("settings.autohotkey.ahkrunners.edit.error.alreadyexists.dialogmsg")
                        .format(newSdkName)
                )
                return false
            }
            return super.stopCellEditing()
        }

        /**
         * Returns the parent's TableCellEditorComponent, but with just the name of the sdk being edited since that's
         * all we want to allow the user to edit within this table.
         */
        override fun getTableCellEditorComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            row: Int,
            column: Int
        ): Component {
            sdkBeingEdited = value as Sdk
            return super.getTableCellEditorComponent(table, value.name, isSelected, row, column)
        }
    }.apply {
        clickCountToStart = 2
    }

    private val sdkTableModel = object : AbstractTableModel() {
        val sdks = getAhkSdks().toMutableList()

        override fun getRowCount() = sdks.size

        override fun getColumnCount() = 1

        override fun getValueAt(rowIndex: Int, columnIndex: Int) = sdks[rowIndex]

        override fun getColumnName(column: Int) = "AutoHotkey runners"

        override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true

        override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
            sdks[rowIndex].sdkModificator.run {
                name = aValue as String
                commitChanges()
            }
        }
    }
    private val sdkTable = JBTable(sdkTableModel).apply {
        setShowGrid(false)
        emptyText.text = AhkBundle.msg("settings.autohotkey.ahkrunners.general.nosdks")
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        columnModel.getColumn(0).apply {
            cellRenderer = sdkTableCellRenderer
            cellEditor = sdkTableCellEditor
        }
    }

    init {
        panel = ToolbarDecorator.createDecorator(sdkTable).apply {
            setAddActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.add.buttonlabel"))
            setAddAction {
                val ahkSdkType = AhkSdkType.getInstance()
                val fileChooser = ahkSdkType.homeChooserDescriptor
                FileChooser.chooseFile(
                    fileChooser, null,
                    LocalFileSystem.getInstance().findFileByIoFile(File(ahkSdkType.suggestHomePath()))
                ) { chosenVirtualFile ->
                    val existingSdk = sdkTableModel.sdks.find { chosenVirtualFile.path == it.homePath }
                    if (existingSdk != null) {
                        NotificationUtil.showErrorDialog(
                            project,
                            AhkBundle.msg("settings.autohotkey.ahkrunners.add.error.exists.title"),
                            AhkBundle.msg("settings.autohotkey.ahkrunners.add.error.exists.info")
                                .format(existingSdk.name)
                        )
                    } else {
                        val newlyAddedSdk = SdkConfigurationUtil
                            .createAndAddSDK(chosenVirtualFile.path, AhkSdkType.getInstance())!!
                        sdkTableModel.sdks.add(newlyAddedSdk)
                        sdkTable.setSelectedRow(sdkTableModel.rowCount - 1)
                    }
                }
            }
            setRemoveActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.remove.buttonlabel"))
            setRemoveAction {
                val selectedRowIndex = sdkTable.selectedRow
                val selectedSdk = sdkTableModel.getValueAt(selectedRowIndex, 0)
                SdkConfigurationUtil.removeSdk(selectedSdk)
                sdkTableModel.sdks.remove(selectedSdk)
                sdkTableModel.fireTableRowsDeleted(selectedRowIndex, selectedRowIndex)
                if (sdkTableModel.sdks.size > 0)
                    sdkTable.setSelectedRow(selectedRowIndex.coerceIn(0, sdkTableModel.sdks.size - 1))
            }
            setEditActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.edit.buttonlabel"))
            setEditAction { sdkTable.editCellAt(sdkTable.selectedRow, 0) }
            disableUpDownActions()
        }.createPanel()
    }

    private fun doesGivenNewNameExist(sdkToRename: Sdk, newSdkName: String): Boolean {
        val allSdks = ProjectJdkTable.getInstance().allJdks.remove(sdkToRename)
        return allSdks.map { it.name }.contains(newSdkName)
    }
}

fun JBTable.setSelectedRow(rowToSelect: Int) = setRowSelectionInterval(rowToSelect, rowToSelect)
