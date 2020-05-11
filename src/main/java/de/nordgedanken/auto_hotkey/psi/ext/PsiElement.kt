package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

val PsiElement.ancestors: Sequence<PsiElement>
    get() = generateSequence(this) {
        if (it is PsiFile) null else it.parent
    }
