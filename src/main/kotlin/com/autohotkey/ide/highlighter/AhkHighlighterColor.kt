package com.autohotkey.ide.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as DefaultColors

/**
 * Defines the default colors for important tokens within the Ahk syntax tree. (Even though some tokens have no visible
 * color change, defining them here allows the user to modify their color in the [AhkColorSettingsPage] if they want.)
 */
enum class AhkHighlighterColor(humanName: String, default: TextAttributesKey? = null) {
    LINE_COMMENT("Comments//Line Comment", DefaultColors.LINE_COMMENT),
    BLOCK_COMMENT("Comments//Block Comment", DefaultColors.BLOCK_COMMENT),

    // Labels
    NORMAL_LABEL("Labels//Normal Label", DefaultColors.LABEL),
    HOTKEY("Labels//Hotkey", DefaultColors.FUNCTION_DECLARATION),
    HOTSTRING("Labels//Hotstring", DefaultColors.FUNCTION_DECLARATION),

    DIRECTIVE("Directive", DefaultColors.METADATA),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("com.autohotkey.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
}
