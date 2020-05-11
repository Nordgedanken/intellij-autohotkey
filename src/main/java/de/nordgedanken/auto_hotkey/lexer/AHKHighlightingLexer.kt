package de.nordgedanken.auto_hotkey.lexer

import com.intellij.lexer.LayeredLexer
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.lexer.AHKEscapesLexer.Companion.ESCAPABLE_LITERALS_TOKEN_SET

class AHKHighlightingLexer : LayeredLexer(AHKLexer()) {
    init {
        ESCAPABLE_LITERALS_TOKEN_SET.types.forEach {
            registerLayer(AHKEscapesLexer.of(it), it)
        }
    }
}
