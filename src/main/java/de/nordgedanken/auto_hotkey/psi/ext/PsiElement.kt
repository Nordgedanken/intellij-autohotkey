package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.stubs.StubElement

val PsiElement.ancestors: Sequence<PsiElement>
    get() = generateSequence(this) {
        if (it is PsiFile) null else it.parent
    }

@Suppress("UNCHECKED_CAST")
inline val <T : StubElement<*>> StubBasedPsiElement<T>.greenStub: T?
    get() = (this as? StubBasedPsiElementBase<T>)?.greenStub
