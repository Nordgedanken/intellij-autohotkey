package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as DefaultColors

enum class AhkHighlighterColor(humanName: String, default: TextAttributesKey? = null) {
    LINE_COMMENT("Comments//Line Comment", DefaultColors.LINE_COMMENT),
    BLOCK_COMMENT("Comments//Block Comment", DefaultColors.BLOCK_COMMENT),

    DIRECTIVE("Directive", DefaultColors.METADATA),
    HOTKEY("Hotkey", DefaultColors.FUNCTION_DECLARATION),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("de.nordgedanken.auto_hotkey.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
}
