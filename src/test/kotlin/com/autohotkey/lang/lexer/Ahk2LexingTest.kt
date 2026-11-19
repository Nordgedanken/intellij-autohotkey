package com.autohotkey.lang.lexer

import com.autohotkey.lang.v2.lexer.Ahk2LexerAdapter
import com.autohotkey.util.AhkConstants.FILE_EXTENSION_V2
import com.intellij.lexer.Lexer

class Ahk2LexingTest : AhkLexingTestBase() {
    override val sourceFileExtension: String = FILE_EXTENSION_V2

    override fun getTestDataPath(): String = "com/autohotkey/lang/lexer/v2"

    override fun createLexer(): Lexer = Ahk2LexerAdapter()

    fun `test source remains one text token`() = doTest()

    fun `test line comments`() = doTest()

    fun `test dialect lexer selects v2 for ah2 extension`() {
        val source = "MsgBox \"Hello, v2!\""
        doTest(source, "TEXT ('$source')", AhkDialectLexer(FILE_EXTENSION_V2))
    }
}
