package de.nordgedanken.auto_hotkey.ide.linemarkers

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findDescendantOfType
import com.intellij.refactoring.suggested.startOffset
import de.nordgedanken.auto_hotkey.lang.psi.AhkLine
import de.nordgedanken.auto_hotkey.lang.psi.COMMENT_TOKENS
import de.nordgedanken.auto_hotkey.lang.psi.isLeaf

/**
 * Adds a run icon to the gutter for the first psiElement in the Ahk file which is of AhkLine type.
 */
class AhkExecutableRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (!element.isLeaf() || element.elementType in COMMENT_TOKENS) return null
        val firstElem = element.containingFile.findDescendantOfType<AhkLine>()
        if (element.startOffset != firstElem?.originalElement?.startOffset) return null

        val actions = ExecutorAction.getActions(0)
        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            { psiElement ->
                actions.mapNotNull { getText(it, psiElement) }.joinToString("\n")
            },
            *actions
        )
    }
}
