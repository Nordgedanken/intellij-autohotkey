package de.nordgedanken.auto_hotkey.run_configurations.ui

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.ExpandableEditorSupport
import com.intellij.ui.TextAccessor
import com.intellij.ui.components.fields.ExpandableSupport
import com.intellij.util.Function
import com.intellij.util.textCompletion.TextFieldWithCompletion
import de.nordgedanken.auto_hotkey.run_configurations.ui.util.AHKCommandCompletionProvider
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class CommandLineEditor(
        private val project: Project,
        private val implicitTextPrefix: String
) : JPanel(BorderLayout()), TextAccessor {

    private val textField = createTextField("")
    val preferredFocusedComponent: JComponent = textField

    init {
        ExpandableEditorSupportWithCustomPopup(textField, this::createTextField)
        add(textField, BorderLayout.CENTER)
    }

    override fun setText(text: String?) {
        textField.setText(text)
    }

    override fun getText(): String = textField.text

    fun setPreferredWidth(width: Int) {
        textField.setPreferredWidth(width)
    }

    fun attachLabel(label: JLabel) {
        label.labelFor = textField
    }

    private fun createTextField(value: String): TextFieldWithCompletion =
            TextFieldWithCompletion(
                    project,
                    AHKCommandCompletionProvider(implicitTextPrefix),
                    value,
                    true,
                    false,
                    false
            )
}

private class ExpandableEditorSupportWithCustomPopup(
        field: EditorTextField,
        private val createPopup: (text: String) -> EditorTextField
) : ExpandableEditorSupport(field) {
    override fun prepare(field: EditorTextField, onShow: Function<in String, String>): Content {
        val popup = createPopup(onShow.`fun`(field.text))
        val background = field.background

        popup.background = background
        popup.setOneLineMode(false)
        popup.preferredSize = Dimension(field.width, 5 * field.height)
        popup.addSettingsProvider { editor ->
            initPopupEditor(editor, background)
            copyCaretPosition(editor, field.editor)
        }

        return object : ExpandableSupport.Content {
            override fun getContentComponent(): JComponent = popup
            override fun getFocusableComponent(): JComponent = popup
            override fun cancel(onHide: Function<in String, String>) {
                field.text = onHide.`fun`(popup.text)
                val editor = field.editor
                if (editor != null) copyCaretPosition(editor, popup.editor)
                if (editor is EditorEx) updateFieldFolding((editor as EditorEx?)!!)
            }
        }
    }

    companion object {
        private fun copyCaretPosition(destination: Editor, source: Editor?) {
            if (source == null) return  // unexpected
            try {
                destination.caretModel.caretsAndSelections = source.caretModel.caretsAndSelections
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }
}
