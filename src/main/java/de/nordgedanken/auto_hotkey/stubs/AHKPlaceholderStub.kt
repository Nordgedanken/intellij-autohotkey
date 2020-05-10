package de.nordgedanken.auto_hotkey.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement

open class AHKPlaceholderStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<AHKElement>(parent, elementType) {

    open class Type<PsiT : AHKElement>(
            debugName: String,
            private val psiCtor: (AHKPlaceholderStub, IStubElementType<*, *>) -> PsiT
    ) : AHKStubElementType<AHKPlaceholderStub, PsiT>(debugName) {

        override fun shouldCreateStub(node: ASTNode): Boolean = createStubIfParentIsStub(node)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?)
                = AHKPlaceholderStub(parentStub, this)

        override fun serialize(stub: AHKPlaceholderStub, dataStream: StubOutputStream) {
        }

        override fun createPsi(stub: AHKPlaceholderStub) = psiCtor(stub, this)

        override fun createStub(psi: PsiT, parentStub: StubElement<*>?) = AHKPlaceholderStub(parentStub, this)
    }
}
