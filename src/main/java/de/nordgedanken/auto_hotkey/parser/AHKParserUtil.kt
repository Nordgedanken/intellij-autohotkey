package de.nordgedanken.auto_hotkey.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderUtil
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.psi.AHKTypes.*
import de.nordgedanken.auto_hotkey.psi.AHK_BLOCK_LIKE_EXPRESSIONS
import de.nordgedanken.auto_hotkey.psi.ext.tokenSetOf

@Suppress("UNUSED_PARAMETER")
object AHKParserUtil : GeneratedParserUtilBase() {
    @JvmStatic
    fun defaultKeyword(b: PsiBuilder, level: Int): Boolean = contextualKeyword(b, "default", DEFAULT)
    private val DEFAULT_NEXT_ELEMENTS: TokenSet = tokenSetOf(NOT)
    private fun contextualKeyword(
            b: PsiBuilder,
            keyword: String,
            elementType: IElementType,
            nextElementPredicate: (IElementType?) -> Boolean = { it !in DEFAULT_NEXT_ELEMENTS }
    ): Boolean {
        // Tricky: the token can be already remapped by some previous rule that was backtracked
        if (b.tokenType == elementType ||
                b.tokenType == KEY && b.tokenText == keyword && nextElementPredicate(b.lookAhead(1))) {
            b.remapCurrentToken(elementType)
            b.advanceLexer()
            return true
        }
        return false
    }
    @JvmStatic
    fun parseCodeBlockLazy(builder: PsiBuilder, level: Int): Boolean {
        return PsiBuilderUtil.parseBlockLazy(builder, LBRACE, RBRACE, BLOCK) != null
    }

    @JvmStatic
    fun isBlock(b: PsiBuilder, level: Int): Boolean {
        val m = b.latestDoneMarker ?: return false
        return m.tokenType in AHK_BLOCK_LIKE_EXPRESSIONS
    }
}
