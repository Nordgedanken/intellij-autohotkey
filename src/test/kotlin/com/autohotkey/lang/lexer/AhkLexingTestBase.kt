package com.autohotkey.lang.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.testFramework.LexerTestCase
import com.intellij.testFramework.UsefulTestCase
import com.autohotkey.AhkTestCase
import com.autohotkey.pathToGoldTestFile
import com.autohotkey.pathToSourceTestFile
import org.jetbrains.annotations.NonNls
import java.io.IOException

abstract class AhkLexingTestBase : LexerTestCase(), AhkTestCase {
    override fun getDirPath(): String = throw UnsupportedOperationException()

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return AhkTestCase.camelOrWordsToSnake(camelCase)
    }

    override fun doTest(@NonNls text: String, expected: String?, lexer: Lexer) {
        val result = printTokens(text, 0, lexer)
        if (expected != null) {
            UsefulTestCase.assertSameLines(expected, result)
        } else {
            UsefulTestCase.assertSameLinesWithFile(pathToGoldTestFile().toFile().canonicalPath, result)
        }
    }

    // NOTE(matkad): this is basically a copy-paste of doFileTest.
    // The only difference is that encoding is set to utf-8
    protected fun doTest(lexer: Lexer = createLexer()) {
        val filePath = pathToSourceTestFile()
        var text = ""
        try {
            val fileText = FileUtil.loadFile(filePath.toFile(), CharsetToolkit.UTF8)
            text = StringUtil.convertLineSeparators(if (shouldTrim()) fileText.trim() else fileText)
        } catch (e: IOException) {
            fail("can't load file " + filePath + ": " + e.message)
        }
        doTest(text, null, lexer)
    }
}
