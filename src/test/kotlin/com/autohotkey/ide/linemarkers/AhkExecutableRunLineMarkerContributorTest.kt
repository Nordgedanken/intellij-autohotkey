package com.autohotkey.ide.linemarkers

import com.autohotkey.AhkBasePlatformTestCase
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class AhkExecutableRunLineMarkerContributorTest : AhkBasePlatformTestCase() {
    fun `test run marker doesn't show for empty file`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "")
        actual.shouldBeEmpty()
    }

    fun `test run marker doesn't show for file with comments`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "; comment")
        actual.shouldBeEmpty()
    }

    fun `test run marker doesn't show for non-ahk file`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.txt", "MsgBox Hi")
        actual.shouldBeEmpty()
    }

    fun `test run marker shows for standard hello-world file`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "MsgBox hi")
        actual shouldContainExactly listOf(Pair(0, "Run 'hello-world'"))
    }

    fun `test run marker shows for file with hotkey`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "^a::\nMsgbox hi")
        actual shouldContainExactly listOf(Pair(0, "Run 'hello-world'"))
    }

    fun `test run marker shows on 2nd line for file w comments and code`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "; comment\nMsgBox hi")
        actual shouldContainExactly listOf(Pair(1, "Run 'hello-world'"))
    }

    fun `test run marker shows only once for file w multiple lines of code`() {
        val actual = createFileWithContentAndGetLineMarkers("hello-world.ahk", "MsgBox hi\nMsgBox hi")
        actual shouldContainExactly listOf(Pair(0, "Run 'hello-world'"))
    }

    private fun createFileWithContentAndGetLineMarkers(filename: String, fileContent: String): List<Pair<Int, String>> {
        myFixture.run {
            configureByText(filename, fileContent)
            doHighlighting()
            return getMarkersFromOpenFileIn(editor, project)
        }
    }

    /**
     * Finds line markers in the given editor and returns them as a list of Pair(line number, marker tooltip text)
     */
    private fun getMarkersFromOpenFileIn(editor: Editor, project: Project): List<Pair<Int, String>> =
        DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, project)
            .map {
                Pair(
                    editor.document.getLineNumber(it.element?.textRange?.startOffset as Int),
                    it.lineMarkerTooltip ?: "null",
                )
            }
            .sortedWith(compareBy({ it.first }, { it.second }))
}
