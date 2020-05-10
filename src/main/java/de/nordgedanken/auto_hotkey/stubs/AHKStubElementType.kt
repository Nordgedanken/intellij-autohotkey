package de.nordgedanken.auto_hotkey.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IStubFileElementType
import de.nordgedanken.auto_hotkey.AHKLanguage
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement

abstract class AHKStubElementType<StubT : StubElement<*>, PsiT : AHKElement>(
        debugName: String
) : IStubElementType<StubT, PsiT>(debugName, AHKLanguage) {

    final override fun getExternalId(): String = "rust.${super.toString()}"

    override fun indexStub(stub: StubT, sink: IndexSink) {}
}

fun createStubIfParentIsStub(node: ASTNode): Boolean {
    val parent = node.treeParent
    val parentType = parent.elementType
    return (parentType is IStubElementType<*, *> && parentType.shouldCreateStub(parent)) ||
            parentType is IStubFileElementType<*>
}
