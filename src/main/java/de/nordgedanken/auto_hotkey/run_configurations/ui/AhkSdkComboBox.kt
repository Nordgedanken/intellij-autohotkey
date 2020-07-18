package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks

class AhkSdkComboBox(currentProject: Project) : ComboBox<Any?>() {
    private val projectSdk: Sdk? = ProjectRootManager.getInstance(currentProject).projectSdk
    //setModel will set the default selected option to the first in the list, but it doesn't matter
    // because the settingseditor will change the selectedItem a few moments after construction via setSelectedSdk...()
    init {
        setModel(CollectionComboBoxModel(getAhkSdks().toList()))
        setRenderer(AhkSdkListCellRenderer(projectSdk))
    }

    fun getSelectedSdkName(): String {
        return when (val selectedSdk = selectedItem) {
            is Sdk -> selectedSdk.name
            is String -> selectedSdk
            null -> ""
            else -> throw IllegalStateException("Unexpected sdk present in the combobox options: $selectedSdk, ${selectedSdk::class.qualifiedName}")
        }
    }

    /**
     * Sets the selected sdk of this combobox to the AhkSdk that matches the given name.
     * If the given name doesn't match any existing sdk, we check 3 things:
     * 1. If the given name isn't blank, we set the selected item to that given name String.
     *    ^(this case can occur if the sdk a run config was using was deleted or renamed by some action from the user)
     * 2. If the project's sdk is an AhkSdk, we'll set the box to that option by default.
     *    ^(this option will allow new run configs to have the project default's sdk as the run config's sdk)
     * 3. Otherwise, if there are any Ahk sdks in the list, just set the default runner to that.
     * Otherwise we just set the sdk to null, which will be rendered differently by the renderer & require the user to select/create an sdk to run the script
     * (note that brand new projects with no set SDKs will also return a null sdk as the default project sdk, so we are required to handle a null value here)
     */
    fun setSelectedSdkByName(sdkName: String) {
        var matchingSdk: Any? = getAhkSdkByName(sdkName)
        if(matchingSdk == null) {
            if (sdkName.isNotBlank()) {
                matchingSdk = sdkName
            } else if (projectSdk?.sdkType is AhkSdkType) {
                matchingSdk = projectSdk
            } else {
                matchingSdk = getAhkSdks().firstOrNull()
            }
        }
        model.selectedItem = matchingSdk
    }
}
