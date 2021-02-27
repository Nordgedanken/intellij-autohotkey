package de.nordgedanken.auto_hotkey.runconfig.ui

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import org.jetbrains.annotations.Nls
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Creates a normal JBTabbedPane within a row using the given constraints, but the pane will have no insets.
 * Use within a panel to add a new tabbedPane in the UI DSL format
 */
fun Row.tabbedPane(vararg constraints: CCFlags, init: JBTabbedPane.() -> Unit): CellBuilder<JBTabbedPane> {
    val jbTabbedPane = JBTabbedPane().apply {
        tabComponentInsets = Insets(0, 0, 0, 0)
    }
    init(jbTabbedPane)
    return jbTabbedPane(*constraints)
}

/**
 * Creates a tab with an inner panel that has an outline and a margin of 10 pixels on all sides
 * except the bottom, since the createIntelliJSpacingConfiguration() invoked by panel already does that
 * Use within a tabbedPane to add new tabs in the UI DSL format
 */
fun JBTabbedPane.outlinedTab(@Nls tabName: String?, init: LayoutBuilder.() -> Unit) {
    addTab(
        tabName,
        panel {
            init()
        }.apply {
            val lineBorder = BorderFactory.createLineBorder(EditorColorsManager.getInstance().globalScheme.defaultForeground)
            val margin = EmptyBorder(10, 10, 0, 10)
            border = CompoundBorder(lineBorder, margin)
        }
    )
}
