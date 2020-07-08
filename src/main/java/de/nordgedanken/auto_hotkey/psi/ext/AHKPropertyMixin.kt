package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import de.nordgedanken.auto_hotkey.psi.AHKElementFactory
import de.nordgedanken.auto_hotkey.psi.AHKVariable
import de.nordgedanken.auto_hotkey.psi.AHKTypes

fun AHKVariable.getKey(): String {
    val keyNode = this.node.findChildByType(AHKTypes.IDENTIFIER)
    return keyNode?.text?.replace("\\\\ ".toRegex(), " ") ?: ""
}

fun AHKVariable.getValue(): String {
    val valueNode = this.node.findChildByType(AHKTypes.STRING_LITERAL)
    return valueNode?.text ?: ""
}

abstract class AHKPropertyMixin(node: ASTNode) : StubBasedPsiElementBase<StubElement<AHKVariable>>(node) {
    private val log: Logger = Logger.getInstance("#de.nordgedanken.auto_hotkey.psi.ext.AHKPropertyMixin")
    fun getNameIdentifier(): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AHKTypes.IDENTIFIER)
        return keyNode?.psi
    }

    fun getName(element: AHKVariable?): String? {
        log.debug("ELEMENTKEY: " + element?.getKey())
        return element?.getKey()
    }

    fun setName(newName: String?): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AHKTypes.IDENTIFIER)
        if (keyNode != null) {
            val property: AHKVariable = AHKElementFactory.createProperty(this.project, newName)
            val newKeyNode: ASTNode = property.firstChild.node
            this.node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }
}
