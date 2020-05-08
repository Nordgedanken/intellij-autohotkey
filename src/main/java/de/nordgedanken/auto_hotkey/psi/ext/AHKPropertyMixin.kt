package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.LoggerRt
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import de.nordgedanken.auto_hotkey.psi.AHKElementFactory
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
    private val log: Logger = Logger.getInstance("#de.nordgedanken.auto_hotkey.psi.ext.AHKPropertyMixin")
    fun getNameIdentifier(): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AHKTypes.KEY)
        return keyNode?.psi
    }

    fun getName(element: AHKProperty?): String? {
        log.debug("ELEMENTKEY: " + element?.getKey())
        return element?.getKey()
    }


    fun setName(newName: String?): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AHKTypes.KEY)
        if (keyNode != null) {
            val property: AHKProperty = AHKElementFactory.createProperty(this.project, newName)
            val newKeyNode: ASTNode = property.firstChild.node
            this.node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

}
