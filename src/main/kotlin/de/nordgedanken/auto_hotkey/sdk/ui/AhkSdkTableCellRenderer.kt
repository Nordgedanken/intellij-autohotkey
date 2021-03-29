package de.nordgedanken.auto_hotkey.sdk.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTableCellRenderer
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import javax.swing.JTable

/**
 * Table cell renderer for ahk sdks that's used for JTables.
 */
class AhkSdkTableCellRenderer constructor(var project: Project) : ColoredTableCellRenderer() {
    /**
     * Renders the given value as an ahk sdk into this class (which is an instance of SimpleColoredComponent), or
     * some form of error string for the value otherwise.
     */
    override fun customizeCellRenderer(
        table: JTable?,
        value: Any?,
        selected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ) = renderGivenSdk(value, value === project.defaultAhkSdk)
}
