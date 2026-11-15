package com.autohotkey.lang.lexer

import com.autohotkey.lang.v2.lexer.Ahk2LexerAdapter
import com.autohotkey.util.AhkConstants.FILE_EXTENSION_V2
import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/** Selects the v2 lexer for files with the `.ah2` extension. */
class AhkDialectLexer(
    private val fileExtension: String? = null,
) : LexerBase() {
    private var delegate: Lexer = AhkLexerAdapter()

    override fun start(
        buffer: CharSequence,
        startOffset: Int,
        endOffset: Int,
        initialState: Int,
    ) {
        val useV2 = initialState == V2_STATE || fileExtension.equals(FILE_EXTENSION_V2, ignoreCase = true)
        delegate = if (useV2) Ahk2LexerAdapter() else AhkLexerAdapter()
        val delegateState = if (useV2) 0 else initialState
        delegate.start(buffer, startOffset, endOffset, delegateState)
    }

    override fun getState() = if (delegate is Ahk2LexerAdapter) V2_STATE else delegate.state

    override fun getTokenType(): IElementType? = delegate.tokenType

    override fun getTokenStart() = delegate.tokenStart

    override fun getTokenEnd() = delegate.tokenEnd

    override fun getBufferSequence() = delegate.bufferSequence

    override fun getBufferEnd() = delegate.bufferEnd

    override fun advance() = delegate.advance()

    private companion object {
        const val V2_STATE = 16
    }
}
