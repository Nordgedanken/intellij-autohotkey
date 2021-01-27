package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.lang.lexer.AhkLexerAdapter
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.LINE_COMMENT

class AhkSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = AhkLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): AhkColor? = when (tokenType) {
            LINE_COMMENT -> AhkColor.LINE_COMMENT
            else -> null
        }
    }

}
