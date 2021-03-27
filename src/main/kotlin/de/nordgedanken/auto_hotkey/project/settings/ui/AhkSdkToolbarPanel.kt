package de.nordgedanken.auto_hotkey.project.settings.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.table.JBTable
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.sdk
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkCellEditor
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkTableCellRenderer
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.lang.IllegalStateException
import javax.swing.AbstractCellEditor
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants.CENTER
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Constructs the UI and logic for the Ahk Sdk toolbar widget within the AutoHotkey settings that allows you to add or
 * remove Sdks. This widget is necessary for non-IDEA IDEs, since they don't have a "Project Structure" dialog that
 * you can use to manage Sdks.
 */
class AhkSdkToolbarPanel(val project: Project) : JPanel() {
    val panel: JPanel
    private val sdkTableCellEditor = AhkSdkCellEditor(project)
    private val ahkSdkCellRenderer = AhkSdkTableCellRenderer(project.sdk)

    private val sdkTableColumnNames = arrayOf("AutoHotkey runners", "Default")
    private val sdkTableModel = object : AbstractTableModel() {
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
    }
    private val sdkTable = JBTable(sdkTableModel).apply {
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
                maxWidth = 15 + tableHeader.getFontMetrics(tableHeader.font).stringWidth(sdkTableColumnNames[1])
            }
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
                val selectedSdk = sdkTableModel.getValueAt(selectedRowIndex, 0) as Sdk
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
}

fun JBTable.setSelectedRow(rowToSelect: Int) = setRowSelectionInterval(rowToSelect, rowToSelect)
