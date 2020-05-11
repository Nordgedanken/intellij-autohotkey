package de.nordgedanken.auto_hotkey.colors

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor

enum class AHKColor(humanName: String, default: TextAttributesKey? = null) {
    IDENTIFIER("Variables//Default", DefaultLanguageHighlighterColors.IDENTIFIER),

    FUNCTION("Functions//Function declaration", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION),
    FUNCTION_CALL("Function//Function call declaration", DefaultLanguageHighlighterColors.FUNCTION_CALL),
    HOTKEY("Hotkey//Hotkey declaration", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION),
    C_COMMENT("C Comments//C Comment declaration", DefaultLanguageHighlighterColors.FUNCTION_CALL),

    KEYWORD("Keywords//Keyword", DefaultLanguageHighlighterColors.KEYWORD),

    NUMBER("Literals//Number", DefaultLanguageHighlighterColors.NUMBER),
    STRING("Literals//Strings//String", DefaultLanguageHighlighterColors.STRING),
    VALID_STRING_ESCAPE("Literals//Strings//Escape sequence//Valid", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE),
    INVALID_STRING_ESCAPE("Literals//Strings//Escape sequence//Invalid", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE),

    BLOCK_COMMENT("Comments//Block comment", DefaultLanguageHighlighterColors.BLOCK_COMMENT),
    EOL_COMMENT("Comments//Line comment", DefaultLanguageHighlighterColors.LINE_COMMENT),

    BRACES("Braces and Operators//Braces", DefaultLanguageHighlighterColors.BRACES),
    BRACKETS("Braces and Operators//Brackets", DefaultLanguageHighlighterColors.BRACKETS),
    OPERATORS("Braces and Operators//Operation sign", DefaultLanguageHighlighterColors.OPERATION_SIGN),
    Q_OPERATOR("Braces and Operators//? operator", DefaultLanguageHighlighterColors.KEYWORD),
    SEMICOLON("Braces and Operators//Semicolon", DefaultLanguageHighlighterColors.SEMICOLON),
    DOT("Braces and Operators//Dot", DefaultLanguageHighlighterColors.DOT),
    COMMA("Braces and Operators//Comma", DefaultLanguageHighlighterColors.COMMA),
    PARENTHESES("Braces and Operators//Parentheses", DefaultLanguageHighlighterColors.PARENTHESES),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("de.nordgedanken.auto_hotkey.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
}

