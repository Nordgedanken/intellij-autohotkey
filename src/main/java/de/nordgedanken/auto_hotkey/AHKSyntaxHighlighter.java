package de.nordgedanken.auto_hotkey;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer;
import de.nordgedanken.auto_hotkey.psi.AHKTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AHKSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey OPERATION =
            createTextAttributesKey("AHK_OPERATION", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEY =
            createTextAttributesKey("AHK_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("AHK_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("AHK_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("AHK_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey FUNCTION_CALL =
            createTextAttributesKey("AHK_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey FUNCTION_DECLARATION =
            createTextAttributesKey("AHK_FUNCTION_DEF", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey PARENTHESE =
            createTextAttributesKey("AHK_PARENTHESE", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("AHK_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey SEMICOLON =
            createTextAttributesKey("AHK_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("AHK_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey HOTKEY =
            createTextAttributesKey("AHK_HOTKEY", DefaultLanguageHighlighterColors.FUNCTION_CALL);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] OPERATION_KEYS = new TextAttributesKey[]{OPERATION};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] FUNCTION_CALL_KEYS = new TextAttributesKey[]{FUNCTION_CALL};
    private static final TextAttributesKey[] FUNCTION_DECLARATION_KEYS = new TextAttributesKey[]{FUNCTION_DECLARATION};
    private static final TextAttributesKey[] PARENTHESE_KEYS = new TextAttributesKey[]{PARENTHESE};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] HOTKEY_KEYS = new TextAttributesKey[]{HOTKEY};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new AHKLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(AHKTypes.VAR_ASIGN) | tokenType.equals(AHKTypes.COLON) | tokenType.equals(AHKTypes.NOT_EQ) | tokenType.equals(AHKTypes.STRING_CALL) | tokenType.equals(AHKTypes.EXPRESSION_SCRIPT)) {
            return OPERATION_KEYS;
        } else if (tokenType.equals(AHKTypes.HOTKEY)) {
            return HOTKEY_KEYS;
        } else if (tokenType.equals(AHKTypes.KEY)) {
            return KEY_KEYS;
        } else if (tokenType.equals(AHKTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(AHKTypes.NUMBER)|tokenType.equals(AHKTypes.HEX)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(AHKTypes.COMMA)) {
            return COMMA_KEYS;
        } else if (tokenType.equals(AHKTypes.COMMENT) || tokenType.equals(AHKTypes.BLOCK_COMMENT)) {
            return COMMENT_KEYS;
        //} else if (tokenType.equals(AHKTypes.FUNCTION)) {
        //    return FUNCTION_CALL_KEYS;
        } else if (tokenType.equals(AHKTypes.FUNCTION) | tokenType.equals(AHKTypes.C_COMMENT)) {
            return FUNCTION_DECLARATION_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (tokenType.equals(AHKTypes.LPAREN) | tokenType.equals(AHKTypes.RPAREN) | tokenType.equals(AHKTypes.LBRACE) | tokenType.equals(AHKTypes.RBRACE) | tokenType.equals(AHKTypes.LBRACK) | tokenType.equals(AHKTypes.RBRACK)) {
            return PARENTHESE_KEYS;
        } else if (tokenType.equals(AHKTypes.SEMICOLON)) {
            return SEMICOLON_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
