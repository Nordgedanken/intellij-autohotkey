package de.nordgedanken.auto_hotkey.stubs

import com.intellij.psi.stubs.IndexSink
import de.nordgedanken.auto_hotkey.stubs.index.AHKNamedElementIndex

fun IndexSink.indexFunction(stub: AHKFunctionStub) {
    indexNamedStub(stub)
}

private fun IndexSink.indexNamedStub(stub: AHKNamedStub) {
    stub.name?.let {
        occurrence(AHKNamedElementIndex.KEY, it)
    }
}
