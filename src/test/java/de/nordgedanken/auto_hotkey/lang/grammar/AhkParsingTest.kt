package de.nordgedanken.auto_hotkey.lang.grammar

import com.intellij.testFramework.ParsingTestCase
import de.nordgedanken.auto_hotkey.lang.parser.AhkParserDefinition

internal class AhkParsingTest : ParsingTestCase("", "ahk", AhkParserDefinition()) {
    fun testParsingTestData() = doTest(true)

    override fun getTestDataPath() = "src/test/resources"

    override fun skipSpaces() = false

    override fun includeRanges() = true
}
