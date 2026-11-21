package com.autohotkey.lang.parser

import com.autohotkey.lang.core.Ahk2FileType
import com.autohotkey.lang.core.Ahk2Language
import com.autohotkey.lang.psi.Ahk2File
import com.intellij.psi.PsiFile

internal class Ahk2ParsingTest : AhkParsingTestBase("v2", "ah2", Ahk2ParserDefinition()) {
    fun `test line comments`() = doTest(true)

    fun `test hotstrings`() = doTest(true)

    fun `test hotkeys`() = doTest(true)

    fun `test directives`() = doTest(true)

    fun `test assignments`() = doTest(true)

    fun `test file uses v2 language and file type`() {
        val file: PsiFile = createPsiFile("test.ah2", "MsgBox \"Hello, v2!\"")

        assertTrue(file is Ahk2File)
        assertSame(Ahk2Language, file.language)
        assertSame(Ahk2FileType, file.fileType)
    }
}
