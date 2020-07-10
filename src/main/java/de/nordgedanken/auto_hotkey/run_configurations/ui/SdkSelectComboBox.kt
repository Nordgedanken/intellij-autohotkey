package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType

class SdkSelectComboBox(currentProject: Project) : ComboBox<Any?>() {
    init {
        val projectSdk: Sdk? = ProjectRootManager.getInstance(currentProject).projectSdk
        val ahkSdkList: MutableList<Sdk?> = getAllAhkSdks()
        val defaultSelectedSdk: Sdk? = if(projectSdk?.sdkType is AhkSdkType) projectSdk else null
        setModel(CollectionComboBoxModel(ahkSdkList.toList(), defaultSelectedSdk))
        setRenderer(AhkSdkListCellRenderer(projectSdk))
    }

    private fun getAllAhkSdks(): MutableList<Sdk?> {
        return ProjectJdkTable.getInstance().getSdksOfType(
                ProjectJdkTable.getInstance().getSdkTypeByName(
                        AhkSdkType.findInstance(AhkSdkType::class.java).name)).toMutableList()
    }

    fun getSelectedSdkName(): String {
        return when (val selectedSdk = selectedItem) {
            is Sdk -> selectedSdk.name
            is String -> selectedSdk
            null -> ""
            else -> throw IllegalStateException("Unexpected sdk in the run config options: $selectedSdk, ${selectedSdk::class.qualifiedName}")
        }
    }

    fun setSelectedSdkByName(sdkName: String) {
        var matchingSdk: Any? = getAllAhkSdks().find { it?.name == sdkName } //default is to set matching sdk by name if we find it
        if(matchingSdk == null && sdkName.isNotBlank()) //if the sdk's name isn't found among project's sdks, just show the invalid sdk's name by default
            matchingSdk = sdkName
        //selectedItem will be null if sdkName = "", which is what we want since null sdk gets rendered differently
        // (note that brand new projects with no set SDKs will also return a null sdk as the default project sdk, so we use null as a value here
        model.selectedItem = matchingSdk
    }
}
