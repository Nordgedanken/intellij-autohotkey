package de.nordgedanken.auto_hotkey.settings.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.CollectionListModel
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.jetbrains.rd.util.remove
import de.nordgedanken.auto_hotkey.run_configurations.ui.AhkSdkListCellRenderer
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.sdk
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.ListSelectionModel

/**
 * Constructs the UI and logic for the Ahk Sdk toolbar widget within the AutoHotkey settings that allows you to add or
 * remove Sdks. This widget is necessary for non-IDEA IDEs, since they don't have a "Project Structure" dialog that
 * you can use to manage Sdks.
 */
class AhkSdkToolbarPanel(val project: Project) : JPanel() {
    val panel: JPanel
    private val availableSdksList: JBList<Sdk>
    private val sdkListCellRenderer = AhkSdkListCellRenderer(project.sdk)
    private val sdkListModel: CollectionListModel<Sdk> = CollectionListModel()

    init {
        sdkListModel.addAll(0, getAhkSdks())
        availableSdksList = JBList<Sdk>(sdkListModel).apply {
            emptyText.text = AhkBundle.msg("settings.autohotkey.ahkrunners.general.nosdks")
            cellRenderer = sdkListCellRenderer
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            createDoubleClickListener().installOn(this)
        }
        panel = ToolbarDecorator.createDecorator(availableSdksList).apply {
            setAddActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.add.buttonlabel"))
            setAddAction {
                val ahkSdkType = AhkSdkType.getInstance()
                val fileChooser = ahkSdkType.homeChooserDescriptor
                FileChooser.chooseFile(fileChooser, null,
                        LocalFileSystem.getInstance().findFileByIoFile(File(ahkSdkType.suggestHomePath()))) { chosenVirtualFile ->
                    val existingSdk = sdkListModel.items.find { chosenVirtualFile.path == it.homePath }
                    if (existingSdk != null) {
                        NotificationUtil.showErrorDialog(project, AhkBundle.msg("settings.autohotkey.ahkrunners.add.error.exists.title"), AhkBundle.msg("settings.autohotkey.ahkrunners.add.error.exists.info").format(existingSdk.name))
                    } else {
                        val newlyAddedSdk = SdkConfigurationUtil.createAndAddSDK(chosenVirtualFile.path, AhkSdkType.getInstance())
                        sdkListModel.add(newlyAddedSdk)
                        availableSdksList.selectedIndex = sdkListModel.getElementIndex(newlyAddedSdk)
                    }
                }
            }
            setRemoveActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.remove.buttonlabel"))
            setRemoveAction {
                val selectedIndex = availableSdksList.selectedIndex
                val selectedSdk = availableSdksList.selectedValue
                SdkConfigurationUtil.removeSdk(selectedSdk)
                sdkListModel.remove(selectedSdk)
                if(sdkListModel.size > 0) availableSdksList.selectedIndex = selectedIndex.coerceIn(0, sdkListModel.size - 1)
            }
            setEditActionName(AhkBundle.msg("settings.autohotkey.ahkrunners.edit.buttonlabel"))
            setEditAction { editAction() }
            disableUpDownActions()
        }.createPanel()
    }

    private fun editAction() {
        val selectedSdk = availableSdksList.selectedValue
        val newSdkName = JOptionPane.showInputDialog(AhkBundle.msg("settings.autohotkey.ahkrunners.edit.message").format(selectedSdk.name), selectedSdk.name) ?: selectedSdk.name
        if (doesGivenNewNameExist(selectedSdk, newSdkName)) {
            NotificationUtil.showErrorDialog(project, AhkBundle.msg("settings.autohotkey.ahkrunners.edit.error.alreadyexists.dialogtitle"), AhkBundle.msg("settings.autohotkey.ahkrunners.edit.error.alreadyexists.dialogmsg").format(newSdkName))
        } else {
            selectedSdk.sdkModificator.run {
                name = newSdkName
                commitChanges()
            }
        }
    }

    private fun doesGivenNewNameExist(sdkToRename: Sdk, newSdkName: String): Boolean {
        val allSdks = ProjectJdkTable.getInstance().allJdks.remove(sdkToRename)
        return allSdks.map { it.name }.contains(newSdkName)
    }

    private fun createDoubleClickListener() =
            object : DoubleClickListener() {
                override fun onDoubleClick(e: MouseEvent): Boolean {
                    editAction()
                    return true
                }
            }
}
