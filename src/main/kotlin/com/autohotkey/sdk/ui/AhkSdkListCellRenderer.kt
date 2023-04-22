package com.autohotkey.sdk.ui

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.ColoredListCellRenderer
import javax.swing.JList

/**
 * List cell renderer for ahk sdks that's used for JLists.
 */
class AhkSdkListCellRenderer constructor(var projectSdk: Sdk?) : ColoredListCellRenderer<Any>() {
    /**
     * Renders the given value as an ahk sdk into this class (which is an instance of SimpleColoredComponent), or
     * some form of error string for the value otherwise.
     */
    override fun customizeCellRenderer(
        list: JList<out Any>,
        value: Any?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) = renderGivenSdk(value, value === projectSdk)
}
