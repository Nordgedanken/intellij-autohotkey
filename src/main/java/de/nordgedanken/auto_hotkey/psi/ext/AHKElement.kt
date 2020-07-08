package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement

interface AHKElement : PsiElement

abstract class AHKStubbedElementImpl<StubT : StubElement<*>> : StubBasedPsiElementBase<StubT>, AHKElement {

    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun toString(): String = "${javaClass.simpleName}($elementType)"
}

abstract class AHKElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), AHKElement
