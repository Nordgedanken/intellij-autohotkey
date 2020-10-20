package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.lang.lexer.AhkLexerAdapter
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.HOTKEY_ASSIGNMENT

class AhkSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = AhkLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): AhkColor? = when (tokenType) {
            HOTKEY_ASSIGNMENT -> AhkColor.FUNCTION
//            SEPARATOR -> AhkColor.SEPARATOR_KEYS
//            KEY -> AhkColor.KEY_KEYS
//            VALUE -> AhkColor.VALUE_KEYS
//            COMMENT -> AhkColor.COMMENT_KEYS
//            BAD_CHARACTER -> AhkColor.BAD_CHAR_KEYS
//            IDENTIFIER -> AHKColor.IDENTIFIER
//
//            STRING_LITERAL -> AHKColor.STRING
//            INTEGER_LITERAL -> AHKColor.NUMBER
//            NUMBERS -> AHKColor.NUMBER
//
//            FUNCTION -> AHKColor.FUNCTION
//            FUNCTION_CALL -> AHKColor.FUNCTION_CALL
//            HOTKEY -> AHKColor.HOTKEY
//            C_COMMENT -> AHKColor.C_COMMENT
//
////            BLOCK_COMMENT -> AHKColor.BLOCK_COMMENT
////            EOL_COMMENT -> AHKColor.EOL_COMMENT
//
//            LPAREN, RPAREN -> AHKColor.PARENTHESES
//            LBRACE, RBRACE -> AHKColor.BRACES
//            LBRACK, RBRACK -> AHKColor.BRACKETS
//
//            SEMICOLON -> AHKColor.SEMICOLON
//            DOT -> AHKColor.DOT
//            COMMA -> AHKColor.COMMA
//
//            StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN -> AHKColor.VALID_STRING_ESCAPE
//            StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN -> AHKColor.INVALID_STRING_ESCAPE
//            StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN -> AHKColor.INVALID_STRING_ESCAPE

            else -> null
        }
    }

}
