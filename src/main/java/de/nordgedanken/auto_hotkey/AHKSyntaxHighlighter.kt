package de.nordgedanken.auto_hotkey

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.StringEscapesTokenTypes
import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.colors.AHKColor
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.BLOCK_COMMENT
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.EOL_COMMENT
import de.nordgedanken.auto_hotkey.psi.AHKTypes.*

class AHKSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return AHKLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType): AHKColor? = when (tokenType) {
            IDENTIFIER -> AHKColor.IDENTIFIER

            STRING_LITERAL -> AHKColor.STRING
            NUMBERS -> AHKColor.NUMBER

            FUNCTION -> AHKColor.FUNCTION
            FUNCTION_CALL -> AHKColor.FUNCTION_CALL
            HOTKEY -> AHKColor.HOTKEY
            C_COMMENT -> AHKColor.C_COMMENT

            BLOCK_COMMENT -> AHKColor.BLOCK_COMMENT
            EOL_COMMENT -> AHKColor.EOL_COMMENT

            LPAREN, RPAREN -> AHKColor.PARENTHESES
            LBRACE, RBRACE -> AHKColor.BRACES
            LBRACK, RBRACK -> AHKColor.BRACKETS

            SEMICOLON -> AHKColor.SEMICOLON
            DOT -> AHKColor.DOT
            COMMA -> AHKColor.COMMA

            StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN -> AHKColor.VALID_STRING_ESCAPE
            StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN -> AHKColor.INVALID_STRING_ESCAPE
            StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN -> AHKColor.INVALID_STRING_ESCAPE

            else -> null
        }
    }
    /*override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey?> {
        return when {
            (tokenType == AHKTypes.VAR_ASIGN) or (tokenType == AHKTypes.COLON) or (tokenType == AHKTypes.NOT_EQ) or (tokenType == AHKTypes.EXPRESSION_SCRIPT) -> {
                OPERATION_KEYS
            }
            tokenType == AHKTypes.HOTKEY -> {
                HOTKEY_KEYS
            }
            tokenType == AHKTypes.IDENTIFIER -> {
                IDENTIFIER_KEYS
            }
            tokenType == AHKTypes.STRING -> {
                STRING_KEYS
            }
            (tokenType == AHKTypes.NUMBER) or (tokenType == AHKTypes.HEX) -> {
                NUMBER_KEYS
            }
            tokenType == AHKTypes.COMMA -> {
                COMMA_KEYS
            }
            tokenType in AHK_COMMENTS -> {
                return COMMENT_KEYS;
                //} else if (tokenType.equals(AHKTypes.FUNCTION)) {
                //    return FUNCTION_CALL_KEYS;
            }
            (tokenType == AHKTypes.FUNCTION) or (tokenType == AHKTypes.C_COMMENT) -> {
                FUNCTION_DECLARATION_KEYS
            }
            tokenType == TokenType.BAD_CHARACTER -> {
                BAD_CHAR_KEYS
            }
            (tokenType == AHKTypes.LPAREN) or (tokenType == AHKTypes.RPAREN) or (tokenType == AHKTypes.LBRACE) or (tokenType == AHKTypes.RBRACE) or (tokenType == AHKTypes.LBRACK) or (tokenType == AHKTypes.RBRACK) -> {
                PARENTHESE_KEYS
            }
            tokenType == AHKTypes.SEMICOLON -> {
                SEMICOLON_KEYS
            }
            else -> {
                EMPTY_KEYS
            }
        }
    }*/

}
