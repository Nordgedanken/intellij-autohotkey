package de.nordgedanken.auto_hotkey.lang.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage
//import de.nordgedanken.auto_hotkey.psi.AHKTypes.*

open class AhkTokenType(debugName: String) : IElementType(debugName, AhkLanguage)

//val AHK_ITEMS = tokenSetOf(
//        FUNCTION
//)

//val AHK_BLOCK_LIKE_EXPRESSIONS = tokenSetOf(BLOCK_EXPR)

fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

//val AHK_COMMENTS = tokenSetOf(BLOCK_COMMENT, EOL_COMMENT)

//val AHK_LITERALS = tokenSetOf(STRING_LITERAL, INTEGER_LITERAL)

//val AHK_EOL_COMMENTS = tokenSetOf(EOL_COMMENT)

//val AHK_ALL_STRING_LITERALS = tokenSetOf(STRING_LITERAL)
