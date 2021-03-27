package de.nordgedanken.auto_hotkey.sdk.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.components.JBTextField
import com.jetbrains.rd.util.remove
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.NotificationUtil
import java.awt.Component
import javax.swing.DefaultCellEditor
import javax.swing.JTable

/**
 * Defines a cell editor that can be used for editing the name of an ahk sdk wherever you choose to display a list of
 * ahk sdks within the UI. You must create an instance of the editor and assign it as the default cell editor to
 * whatever Swing component is showing the sdks (eg JBTable)
 *
 * @param project The project that contains the cellEditor about to be opened.
 * Required in order to show error modals in the correct location.
 */
class AhkSdkCellEditor(private val project: Project) : DefaultCellEditor(JBTextField()) {
    init {
        clickCountToStart = 2
    }

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

    private fun doesGivenNewNameExist(sdkToRename: Sdk, newSdkName: String): Boolean {
        val allSdks = ProjectJdkTable.getInstance().allJdks.remove(sdkToRename)
        return allSdks.map { it.name }.contains(newSdkName)
    }
}
