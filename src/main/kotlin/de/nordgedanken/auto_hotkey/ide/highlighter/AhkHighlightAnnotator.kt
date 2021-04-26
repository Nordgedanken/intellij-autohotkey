package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity.INFORMATION
import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.ide.highlighter.AhkHighlighterColor.DIRECTIVE
import de.nordgedanken.auto_hotkey.ide.highlighter.AhkHighlighterColor.HOTKEY
import de.nordgedanken.auto_hotkey.ide.highlighter.AhkHighlighterColor.HOTSTRING
import de.nordgedanken.auto_hotkey.ide.highlighter.AhkHighlighterColor.NORMAL_LABEL
import de.nordgedanken.auto_hotkey.lang.psi.AhkDirective
import de.nordgedanken.auto_hotkey.lang.psi.AhkHotkey
import de.nordgedanken.auto_hotkey.lang.psi.AhkHotstring
import de.nordgedanken.auto_hotkey.lang.psi.AhkNormalLabel

/**
 * Highlights psiElements that can't be highlighted by [AhkSyntaxHighlighter] since they are made of composite tokens
 */
class AhkHighlightAnnotator : Annotator {
    override fun annotate(psiElem: PsiElement, holder: AnnotationHolder) {
        when (psiElem) {
            is AhkNormalLabel -> holder.newInfoHighlightAnnotation(NORMAL_LABEL)
            is AhkHotkey -> holder.newInfoHighlightAnnotation(HOTKEY)
            is AhkHotstring -> holder.newInfoHighlightAnnotation(HOTSTRING)

            is AhkDirective -> holder.newInfoHighlightAnnotation(DIRECTIVE)
        }
    }
}

fun AnnotationHolder.newInfoHighlightAnnotation(color: AhkHighlighterColor) =
    newSilentAnnotation(INFORMATION).textAttributes(color.textAttributesKey).create()
