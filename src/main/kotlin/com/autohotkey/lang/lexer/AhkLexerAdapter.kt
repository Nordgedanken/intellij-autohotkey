package com.autohotkey.lang.lexer

import com.autohotkey.AhkLexer
import com.intellij.lexer.FlexAdapter

class AhkLexerAdapter : FlexAdapter(AhkLexer(null))
