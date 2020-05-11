package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import de.nordgedanken.auto_hotkey.psi.AHKExpr
import de.nordgedanken.auto_hotkey.stubs.AHKPlaceholderStub

abstract class AHKExprMixin : AHKStubbedElementImpl<AHKPlaceholderStub>, AHKExpr {
    constructor(node: ASTNode) : super(node)
    constructor(stub: AHKPlaceholderStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
}
