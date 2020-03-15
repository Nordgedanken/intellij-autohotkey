package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.psi.AHKStringLiteral
import de.nordgedanken.auto_hotkey.psi.AHKTypes
import de.nordgedanken.auto_hotkey.util.AHKStringLiteralEscaper

fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)


abstract class AHKStringLiteralMixin(node: ASTNode) : PsiLanguageInjectionHost, StubBasedPsiElementBase<StubElement<AHKStringLiteral>>(node) {

    override fun isValidHost(): Boolean =
            node.findChildByType(tokenSetOf(AHKTypes.STRING, AHKTypes.STRING_LITERAL)) != null

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val valueNode = node.firstChildNode
        assert(valueNode is LeafElement)
        (valueNode as LeafElement).replaceWithText(text)
        return this
    }

    override fun createLiteralTextEscaper(): AHKStringLiteralEscaper {
        return AHKStringLiteralEscaper(this)
    }
}
