package de.nordgedanken.auto_hotkey.lang.parser

internal class AhkParsingTestComplete : AhkParsingTestBase("complete") {
    fun `test comments`() = doTest(true)
    fun `test block comments`() = doTest(true)
}
