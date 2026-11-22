package com.autohotkey.ide.highlighter

import com.autohotkey.ide.highlighter.AhkHighlighterColor.DIRECTIVE
import com.autohotkey.ide.highlighter.AhkHighlighterColor.HOTKEY
import com.autohotkey.ide.highlighter.AhkHighlighterColor.HOTSTRING
import com.autohotkey.lang.v2.psi.Ahk2Directive
import com.autohotkey.lang.v2.psi.Ahk2Hotkey
import com.autohotkey.lang.v2.psi.Ahk2Hotstring
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

/** v2 version of [AhkHighlightAnnotator] */
class Ahk2HighlightAnnotator : Annotator {
    override fun annotate(
        psiElem: PsiElement,
        holder: AnnotationHolder,
    ) {
        when (psiElem) {
            is Ahk2Hotkey -> holder.newInfoHighlightAnnotation(HOTKEY)
            is Ahk2Hotstring -> holder.newInfoHighlightAnnotation(HOTSTRING)
            is Ahk2Directive -> holder.newInfoHighlightAnnotation(DIRECTIVE)
        }
    }
}
