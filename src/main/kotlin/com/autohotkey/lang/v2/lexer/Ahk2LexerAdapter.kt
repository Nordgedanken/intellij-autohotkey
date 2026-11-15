package com.autohotkey.lang.v2.lexer

import com.autohotkey.lang.v2.Ahk2Lexer
import com.intellij.lexer.FlexAdapter

class Ahk2LexerAdapter : FlexAdapter(Ahk2Lexer(null))
