package com.autohotkey.lang.parser

internal class AhkParsingTestComplete : AhkParsingTestBase("complete") {
    fun `test line comments`() = doTest(true)
    fun `test block comments`() = doTest(true)
    fun `test directives`() = doTest(true)
    fun `test hotkeys`() = doTest(true)
    fun `test hotstrings`() = doTest(true)
    fun `test normal labels`() = doTest(true)
}
