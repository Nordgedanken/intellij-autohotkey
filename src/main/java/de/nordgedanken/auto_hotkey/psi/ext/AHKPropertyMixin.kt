package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.StubElement
import de.nordgedanken.auto_hotkey.psi.AHKProperty
import de.nordgedanken.auto_hotkey.psi.AHKTypes

fun AHKProperty.getKey(): String {
    val keyNode = this.node.findChildByType(AHKTypes.KEY)
    return keyNode?.text?.replace("\\\\ ".toRegex(), " ") ?: ""
}

fun AHKProperty.getValue(): String {
    val valueNode = this.node.findChildByType(AHKTypes.STRING_LITERAL)
    return valueNode?.text ?: ""
}

abstract class AHKPropertyMixin(node: ASTNode) : StubBasedPsiElementBase<StubElement<AHKProperty>>(node) {

}
