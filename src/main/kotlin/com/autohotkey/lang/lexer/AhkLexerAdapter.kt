package com.autohotkey.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.autohotkey.AhkLexer

class AhkLexerAdapter : FlexAdapter(AhkLexer(null))
