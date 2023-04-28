package com.autohotkey.ide.linemarkers

import com.autohotkey.lang.psi.AhkLine
import com.autohotkey.lang.psi.COMMENT_TOKENS
import com.autohotkey.lang.psi.isLeaf
import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.descendantsOfType
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.startOffset

/**
 * Adds a run icon to the gutter for the first psiElement in the Ahk file which is not a comment.
 */
class AhkExecutableRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (!element.isLeaf() || element.elementType in COMMENT_TOKENS) return null
        val firstElem = element.containingFile.descendantsOfType<AhkLine>().firstOrNull()
        if (element.startOffset != firstElem?.originalElement?.startOffset) return null

        val actions = ExecutorAction.getActions(0)
        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            { psiElement ->
                actions.mapNotNull { getText(it, psiElement) }.joinToString("\n")
            },
            *actions,
        )
    }
}
