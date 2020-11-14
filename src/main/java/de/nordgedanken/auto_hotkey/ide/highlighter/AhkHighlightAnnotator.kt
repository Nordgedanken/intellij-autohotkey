package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity.INFORMATION
import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.lang.psi.AhkHotkeyAssignment

class AhkHighlightAnnotator : Annotator {
    override fun annotate(psiElem: PsiElement, holder: AnnotationHolder) {
        if (psiElem is AhkHotkeyAssignment) {
            holder.newSilentAnnotation(INFORMATION).textAttributes(AhkColor.FUNCTION.textAttributesKey).create()
        }
    }

}
