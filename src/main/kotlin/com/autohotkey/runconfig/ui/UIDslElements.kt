package com.autohotkey.runconfig.ui

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.annotations.Nls
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Creates a normal JBTabbedPane within a row using the given constraints, but the pane will have no insets.
 * Use within a panel to add a new tabbedPane in the UI DSL format
 */
fun Row.tabbedPane(init: JBTabbedPane.() -> Unit): Cell<JBTabbedPane> {
    val jbTabbedPane = JBTabbedPane().apply {
        tabComponentInsets = Insets(0, 0, 0, 0)
    }
    init(jbTabbedPane)
    return cell(jbTabbedPane).align(AlignX.FILL)
}

/**
 * Creates a tab with an inner panel that has an outline and a margin of 10 pixels on all sides
 * except the bottom, since the createIntelliJSpacingConfiguration() invoked by panel already does that
 * Use within a tabbedPane to add new tabs in the UI DSL format
 */
fun JBTabbedPane.outlinedTab(@Nls tabName: String?, init: Panel.() -> Unit) {
    addTab(
        tabName,
        panel {
            init()
        }.apply {
            val lineBorder = BorderFactory.createLineBorder(
                EditorColorsManager.getInstance().globalScheme.defaultForeground,
            )
            val margin = EmptyBorder(10, 10, 10, 10)
            border = CompoundBorder(lineBorder, margin)
        },
    )
}
