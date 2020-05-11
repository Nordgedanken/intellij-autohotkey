package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import de.nordgedanken.auto_hotkey.psi.AHKTypes.IDENTIFIER
import de.nordgedanken.auto_hotkey.stubs.AHKNamedStub

interface AHKNamedElement : AHKElement, PsiNamedElement, NavigatablePsiElement

abstract class AHKNamedElementImpl(node: ASTNode) : AHKElementImpl(node), PsiNameIdentifierOwner {

    override fun getNameIdentifier(): PsiElement? = findChildByType(IDENTIFIER)

    override fun getName(): String? = nameIdentifier?.unescapedText

    override fun setName(name: String): PsiElement? {
        return this
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}


val PsiElement.unescapedText: String get() {
    return this.text ?: return ""
}

abstract class AHKStubbedNamedElementImpl<StubT> : AHKStubbedElementImpl<StubT>, PsiNameIdentifierOwner
        where StubT : AHKNamedStub, StubT : StubElement<*> {

    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? = findChildByType(IDENTIFIER)

    override fun getName(): String? {
        val stub = greenStub
        return if (stub != null) stub.name else ""
    }

    override fun setName(name: String): PsiElement? {
        return this
    }

}
