package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.lang.lexer.AhkLexerAdapter
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.BLOCK_COMMENT
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.LINE_COMMENT

/**
 * Defines what color raw tokens read directly from the lexer should be.
 *
 * Note: Only immediate tokens read directly from the lexer can be colored with this class. Any composite elements made
 * of multiple tokens must be highlighted with an annotator such as [AhkHighlightAnnotator]
 */
class AhkSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = AhkLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): AhkHighlighterColor? = when (tokenType) {
            LINE_COMMENT -> AhkHighlighterColor.LINE_COMMENT
            BLOCK_COMMENT -> AhkHighlighterColor.BLOCK_COMMENT
            else -> null
        }
    }
}
