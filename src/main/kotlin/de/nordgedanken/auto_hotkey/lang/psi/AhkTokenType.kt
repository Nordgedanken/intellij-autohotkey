package de.nordgedanken.auto_hotkey.lang.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.BLOCK_COMMENT
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.LINE_COMMENT
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.WHITESPACE_HOZ

open class AhkTokenType(debugName: String) : IElementType(debugName, AhkLanguage)

fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

val COMMENT_TOKENS = tokenSetOf(LINE_COMMENT, BLOCK_COMMENT)

/**
 * Note: CRLF is intentionally not included because including it will mess up
 * the parsing of full lines. Once the bnf is more developed, it should ideally
 * be possible to include CRLF in this list.
 */
val WHITESPACE_TOKENS = tokenSetOf(WHITESPACE_HOZ)
