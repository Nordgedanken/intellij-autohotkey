package de.nordgedanken.auto_hot_key;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import de.nordgedanken.auto_hot_key.psi.AHKTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AHKSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey VAR_ASIGN =
            createTextAttributesKey("AHK_VAR_ASIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
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


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] VAR_ASIGN_KEYS = new TextAttributesKey[]{VAR_ASIGN};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] FUNCTION_CALL_KEYS = new TextAttributesKey[]{FUNCTION_CALL};
    private static final TextAttributesKey[] FUNCTION_DECLARATION_KEYS = new TextAttributesKey[]{FUNCTION_DECLARATION};
    private static final TextAttributesKey[] PARENTHESE_KEYS = new TextAttributesKey[]{PARENTHESE};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new AHKLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(AHKTypes.VAR_ASIGN)) {
            return VAR_ASIGN_KEYS;
        } else if (tokenType.equals(AHKTypes.KEY)) {
            return KEY_KEYS;
        } else if (tokenType.equals(AHKTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(AHKTypes.COMMA)) {
            return COMMA_KEYS;
        } else if (tokenType.equals(AHKTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(AHKTypes.FUNCTION_CALL)) {
            return FUNCTION_CALL_KEYS;
        } else if (tokenType.equals(AHKTypes.FUNCTION_DEF)) {
            return FUNCTION_DECLARATION_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (tokenType.equals(AHKTypes.LPAREN) | tokenType.equals(AHKTypes.RPAREN) | tokenType.equals(AHKTypes.LBRACE) | tokenType.equals(AHKTypes.RBRACE) | tokenType.equals(AHKTypes.LBRACK) | tokenType.equals(AHKTypes.RBRACK)) {
            return PARENTHESE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
