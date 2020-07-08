package de.nordgedanken.auto_hotkey.stubs

import com.intellij.psi.stubs.*
import de.nordgedanken.auto_hotkey.psi.AHKFunction
import de.nordgedanken.auto_hotkey.psi.impl.AHKFunctionImpl

class AHKFunctionStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    override val name: String?
) : AHKNamedStub, StubBase<AHKFunction>(parent, elementType) {

    object Type : AHKStubElementType<AHKFunctionStub, AHKFunction>("FUNCTION") {

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) =
                AHKFunctionStub(parentStub, this,
                        dataStream.readName()?.string
                )

        override fun serialize(stub: AHKFunctionStub, dataStream: StubOutputStream) =
                with(dataStream) {
                    writeName(stub.name)
                }

        override fun createPsi(stub: AHKFunctionStub) =
                AHKFunctionImpl(stub, this)

        override fun createStub(psi: AHKFunction, parentStub: StubElement<*>?): AHKFunctionStub {
            return AHKFunctionStub(parentStub, this,
                    name = psi.name
            )
        }

        override fun indexStub(stub: AHKFunctionStub, sink: IndexSink) = sink.indexFunction(stub)
    }
}
