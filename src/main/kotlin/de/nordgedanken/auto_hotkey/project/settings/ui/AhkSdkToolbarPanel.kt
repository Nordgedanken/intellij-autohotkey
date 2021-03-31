package de.nordgedanken.auto_hotkey.project.settings.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.io.File
import javax.swing.JPanel

/**
 * Constructs the UI and logic for the Ahk Sdk toolbar widget within the AutoHotkey settings that allows you to add or
 * remove Sdks. This widget is necessary for non-IDEA IDEs, since they don't have a "Project Structure" dialog that
 * you can use to manage Sdks.
 */
class AhkSdkToolbarPanel(val project: Project) : JPanel() {
    val panel: JPanel
    private val sdkTable = AhkSdkManagementTable(project)
    private val sdkTableModel = sdkTable.model as AhkSdkTableModel

    init {
        panel = ToolbarDecorator.createDecorator(sdkTable).apply {
            setAddActionName(AhkBundle.msg("settings.ahksdktable.add.buttonlabel"))
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
                            AhkBundle.msg("settings.ahksdktable.add.error.exists.title"),
                            AhkBundle.msg("settings.ahksdktable.add.error.exists.info")
                                .format(existingSdk.name)
                        )
                    } else {
                        val newlyAddedSdk = SdkConfigurationUtil
                            .createAndAddSDK(chosenVirtualFile.path, AhkSdkType.getInstance())!!
                        sdkTableModel.addSdk(newlyAddedSdk)
                        sdkTable.setSelectedRow(sdkTableModel.rowCount - 1)
                    }
                }
            }
            setRemoveActionName(AhkBundle.msg("settings.ahksdktable.remove.buttonlabel"))
            setRemoveAction {
                val selectedRowIndex = sdkTable.selectedRow
                val selectedSdk = sdkTableModel.getValueAt(selectedRowIndex, 0) as Sdk
                SdkConfigurationUtil.removeSdk(selectedSdk)
                sdkTableModel.removeSdkAtRow(selectedRowIndex)
                if (sdkTableModel.sdks.size > 0)
                    sdkTable.setSelectedRow(selectedRowIndex.coerceIn(0, sdkTableModel.sdks.size - 1))
            }
            setEditActionName(AhkBundle.msg("settings.ahksdktable.edit.buttonlabel"))
            setEditAction { sdkTable.editCellAt(sdkTable.selectedRow, 0) }
            disableUpDownActions()
        }.createPanel()
    }
}

fun JBTable.setSelectedRow(rowToSelect: Int) = setRowSelectionInterval(rowToSelect, rowToSelect)
