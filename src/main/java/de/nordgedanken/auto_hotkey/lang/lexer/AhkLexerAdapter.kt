package de.nordgedanken.auto_hotkey.lang.lexer

import com.intellij.lexer.FlexAdapter
import de.nordgedanken.auto_hotkey.AhkLexer

class AhkLexerAdapter : FlexAdapter(AhkLexer(null))
