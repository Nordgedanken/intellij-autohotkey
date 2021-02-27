package de.nordgedanken.auto_hotkey.lang.lexer

import com.intellij.lexer.Lexer

class AhkLexingTest : AhkLexingTestBase() {
    override fun getTestDataPath(): String = "de/nordgedanken/auto_hotkey/lang/lexer"

    override fun createLexer(): Lexer = AhkLexerAdapter()

    fun `test comments`() = doTest()
}
