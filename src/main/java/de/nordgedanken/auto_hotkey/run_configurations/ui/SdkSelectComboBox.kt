package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks

class SdkSelectComboBox(currentProject: Project) : ComboBox<Any?>() {
    init {
        val projectSdk: Sdk? = ProjectRootManager.getInstance(currentProject).projectSdk
        val defaultSelectedSdk: Sdk? = if(projectSdk?.sdkType is AhkSdkType) projectSdk else null
        setModel(CollectionComboBoxModel(getAhkSdks().toList(), defaultSelectedSdk))
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
     * If the given name doesn't match any sdk and the given name isn't blank, we set the selected item to a string of the given name.
     * ^(this case can occur if the sdk a run config was using was deleted or renamed by some action from the user)
     * Otherwise we set it to null, which will be rendered differently by the renderer.
     * (note that brand new projects with no set SDKs will also return a null sdk as the default project sdk, so we are required to use a null value here)
     */
    fun setSelectedSdkByName(sdkName: String) {
        var matchingSdk: Any? = getAhkSdkByName(sdkName)
        if(matchingSdk == null && sdkName.isNotBlank())
            matchingSdk = sdkName
        model.selectedItem = matchingSdk
    }
}
