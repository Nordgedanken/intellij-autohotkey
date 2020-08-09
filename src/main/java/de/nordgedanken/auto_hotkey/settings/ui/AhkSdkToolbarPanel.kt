package de.nordgedanken.auto_hotkey.settings.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import de.nordgedanken.auto_hotkey.localization.AhkBundle
import de.nordgedanken.auto_hotkey.run_configurations.ui.AhkSdkListCellRenderer
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.sdk
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.io.File
import javax.swing.JPanel
import javax.swing.ListSelectionModel

/**
 * Constructs the UI and logic for the Ahk Sdk toolbar widget within the AutoHotkey settings that allows you to add or
 * remove Sdks. This widget is necessary for non-IDEA IDEs, since they don't have a "Project Structure" dialog that
 * you can use to manage Sdks.
 */
class AhkSdkToolbarPanel(project: Project) : JPanel() {
    val panel: JPanel
    private val availableSdksList: JBList<Sdk>
    private val sdkListCellRenderer = AhkSdkListCellRenderer(project.sdk)
    private val sdkListModel: CollectionListModel<Sdk> = CollectionListModel()

    init {
        sdkListModel.addAll(0, getAhkSdks())
        availableSdksList = JBList<Sdk>(sdkListModel).apply {
            emptyText.text = AhkBundle.msg("settings.ahkrunners.general.nosdks")
            cellRenderer = sdkListCellRenderer
            selectionMode = ListSelectionModel.SINGLE_SELECTION
        }
        panel = ToolbarDecorator.createDecorator(availableSdksList).apply {
            setAddActionName(AhkBundle.msg("settings.ahkrunners.add.buttonlabel"))
            setAddAction {
                val ahkSdkType = AhkSdkType.getInstance()
                val fileChooser = ahkSdkType.homeChooserDescriptor
                FileChooser.chooseFile(fileChooser, null,
                        LocalFileSystem.getInstance().findFileByIoFile(File(ahkSdkType.suggestHomePath()))) {
                    chosenVirtualFile ->
                    val existingSdk = sdkListModel.items.find { chosenVirtualFile.path == it.homePath }
                    if (existingSdk != null) {
                        NotificationUtil.showErrorDialog(project, AhkBundle.msg("settings.ahkrunners.add.error.exists.title"), AhkBundle.msg("settings.ahkrunners.add.error.exists.info").format(existingSdk.name))
                    } else {
                        val newlyAddedSdk = SdkConfigurationUtil.createAndAddSDK(chosenVirtualFile.path, AhkSdkType.getInstance())
                        sdkListModel.add(newlyAddedSdk)
                        availableSdksList.selectedIndex = sdkListModel.getElementIndex(newlyAddedSdk)
                    }
                }
            }
            setRemoveActionName(AhkBundle.msg("settings.ahkrunners.remove.buttonlabel"))
            setRemoveAction {
                val selectedIndex = availableSdksList.selectedIndex
                val selectedSdk = availableSdksList.selectedValue
                SdkConfigurationUtil.removeSdk(selectedSdk)
                sdkListModel.remove(selectedSdk)
                if(sdkListModel.size > 0) availableSdksList.selectedIndex = selectedIndex.coerceIn(0, sdkListModel.size - 1)
            }
            disableUpDownActions()
        }.createPanel()
    }
}
