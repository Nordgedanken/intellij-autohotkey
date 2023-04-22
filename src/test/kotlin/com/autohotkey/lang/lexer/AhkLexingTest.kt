package com.autohotkey.lang.lexer

import com.intellij.lexer.Lexer

class AhkLexingTest : AhkLexingTestBase() {
    override fun getTestDataPath(): String = "com/autohotkey/lang/lexer"

    override fun createLexer(): Lexer = AhkLexerAdapter()

    fun `test line comments`() = doTest()
    fun `test block comments`() = doTest()
    fun `test directives`() = doTest()
}
