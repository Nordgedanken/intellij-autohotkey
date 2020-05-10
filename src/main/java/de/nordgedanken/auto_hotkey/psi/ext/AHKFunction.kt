package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import de.nordgedanken.auto_hotkey.psi.AHKFunction
import de.nordgedanken.auto_hotkey.stubs.AHKFunctionStub

abstract class AHKFunctionImplMixin : AHKStubbedNamedElementImpl<AHKFunctionStub>, AHKFunction {

    constructor(node: ASTNode) : super(node)

    constructor(stub: AHKFunctionStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
}
