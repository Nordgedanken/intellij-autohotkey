package de.nordgedanken.auto_hotkey.run_configurations.ui.util

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.util.TextFieldCompletionProvider
import com.intellij.util.execution.ParametersListUtil

class AHKCommandCompletionProvider(
        private val implicitTextPrefix: String
) : TextFieldCompletionProvider() {
    override fun getPrefix(currentTextPrefix: String): String = splitContextPrefix(currentTextPrefix).second

    override fun acceptChar(c: Char): CharFilter.Result? =
            if (c == '-') CharFilter.Result.ADD_TO_PREFIX else null

    override fun addCompletionVariants(text: String, offset: Int, prefix: String, result: CompletionResultSet) {
        val (ctx, _) = splitContextPrefix(text)
        result.addAllElements(complete(ctx))
    }

    // public for testing
    private fun splitContextPrefix(text: String): Pair<String, String> {
        val lexer = ParametersListLexer(text)
        var contextEnd = 0
        while (lexer.nextToken()) {
            if (lexer.tokenEnd == text.length) {
                return text.substring(0, contextEnd) to lexer.currentToken
            }
            contextEnd = lexer.tokenEnd
        }

        return text.substring(0, contextEnd) to ""
    }

    // public for testing
    private fun complete(context: String): List<LookupElement> {
        val args = ParametersListUtil.parse(implicitTextPrefix + context)
        if ("/" in args) return emptyList()
        if (args.isEmpty()) {
            return COMMON_OPTIONS.map { it.lookupElement }
        }

        return COMMON_OPTIONS.filter { it.long !in args }
                .map { it.lookupElement }
    }
}

private data class Context(
        val commandLinePrefix: List<String>
)

private typealias ArgCompleter = (Context) -> List<LookupElement>

private class Opt(
        val name: String,
        val argCompleter: ArgCompleter? = null

) {
    val long get() = "/$name"

    val lookupElement: LookupElement =
            LookupElementBuilder.create(long)
                    .withInsertHandler { ctx, _ ->
                        if (argCompleter != null) {
                            ctx.addSuffix(" ")
                            ctx.setLaterRunnable {
                                CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(ctx.project, ctx.editor)
                            }
                        }
                    }
}

fun InsertionContext.addSuffix(suffix: String) {
    document.insertString(selectionEndOffset, suffix)
    EditorModificationUtil.moveCaretRelatively(editor, suffix.length)
}

private val COMMON_OPTIONS = listOf(
        Opt("in"),
        Opt("out"),
        Opt("icon"),
        Opt("bin"),
        Opt("mpress")
)

// Copy of com.intellij.openapi.externalSystem.service.execution.cmd.ParametersListLexer,
// which is not present in all IDEs.
class ParametersListLexer(private val myText: String) {
    private var myTokenStart = -1
    private var index = 0

    val tokenEnd: Int
        get() {
            assert(myTokenStart >= 0)
            return index
        }

    val currentToken: String
        get() = myText.substring(myTokenStart, index)

    fun nextToken(): Boolean {
        var i = index

        while (i < myText.length && Character.isWhitespace(myText[i])) {
            i++
        }

        if (i == myText.length) return false

        myTokenStart = i
        var isInQuote = false

        do {
            val a = myText[i]
            if (!isInQuote && Character.isWhitespace(a)) break
            when {
                a == '\\' && i + 1 < myText.length && myText[i + 1] == '"' -> i += 2
                a == '"' -> {
                    i++
                    isInQuote = !isInQuote
                }
                else -> i++
            }
        } while (i < myText.length)

        index = i
        return true
    }
}
