package com.autohotkey.ide.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity.INFORMATION
import com.intellij.psi.PsiElement
import com.autohotkey.ide.highlighter.AhkHighlighterColor.DIRECTIVE
import com.autohotkey.ide.highlighter.AhkHighlighterColor.HOTKEY
import com.autohotkey.ide.highlighter.AhkHighlighterColor.HOTSTRING
import com.autohotkey.ide.highlighter.AhkHighlighterColor.NORMAL_LABEL
import com.autohotkey.lang.psi.AhkDirective
import com.autohotkey.lang.psi.AhkHotkey
import com.autohotkey.lang.psi.AhkHotstring
import com.autohotkey.lang.psi.AhkNormalLabel

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
