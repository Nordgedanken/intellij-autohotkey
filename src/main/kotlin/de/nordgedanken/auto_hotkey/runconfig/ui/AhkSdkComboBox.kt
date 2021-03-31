package de.nordgedanken.auto_hotkey.runconfig.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.ui.AhkSdkListCellRenderer

/**
 * Defines a combobox that you can select an ahk sdk from the available list of sdks. Rendering is defined by an
 * AhkSdkListCellRenderer which can render unknown values in a special format. (Eg. This can happen if you restore a run
 * config from xml but the sdk that that run config was referring to no longer exists in the project)
 */
class AhkSdkComboBox(private val currentProject: Project) : ComboBox<Any?>() {
    private var projectSdk: Sdk? = null

    init {
        renderer = AhkSdkListCellRenderer(projectSdk)
        updateSdkList()
    }

    /**
     * Updates the available sdks in this combobox. This method is also called
     * after someone modifies the project SDKs via the button next to the SDK
     * dropdown in the Ahk run config editor, so we update the sdk list/projectSdk as needed
     *
     * Note: setModel will set the default selected option to the first in the list, but it doesn't matter
     * because the settingseditor will change the selectedItem a few moments after construction via setSelectedSdk...()
     */
    fun updateSdkList() {
        projectSdk = currentProject.defaultAhkSdk
        (renderer as AhkSdkListCellRenderer).projectSdk = projectSdk // needed since it starts out null
        model = CollectionComboBoxModel(getAhkSdks().toList(), selectedItem)
    }

    fun getSelectedSdkName(): String {
        return when (val selectedSdk = selectedItem) {
            is Sdk -> selectedSdk.name
            is String -> selectedSdk
            null -> ""
            else -> throw IllegalStateException(
                "Unexpected sdk present in the combobox options: $selectedSdk, ${selectedSdk::class.qualifiedName}"
            )
        }
    }

    /**
     * Sets the selected sdk of this combobox to the AhkSdk that matches the given name.
     * If the given name doesn't match any existing sdk, we check:
     * 1. If the given name isn't blank, we set the selected item to that given name String.
     *    ^(this case can occur if the sdk that the run config was using got deleted)
     * 2. Otherwise, we just set the sdk to null, which will be rendered differently by the renderer & require the user
     * to select/create an sdk to run the script (Eg. a brand new project with no set SDKs will trigger this case)
     */
    fun setSelectedSdkByName(sdkName: String) {
        var matchingSdk: Any? = getAhkSdkByName(sdkName)
        if (matchingSdk == null) {
            matchingSdk = when {
                sdkName.isNotBlank() -> sdkName
                else -> null
            }
        }
        model.selectedItem = matchingSdk
    }
}
