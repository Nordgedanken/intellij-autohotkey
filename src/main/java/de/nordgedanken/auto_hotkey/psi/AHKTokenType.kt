package de.nordgedanken.auto_hotkey.psi

import de.nordgedanken.auto_hotkey.psi.AHKTypes.BLOCK_EXPR
import de.nordgedanken.auto_hotkey.psi.AHKTypes.FUNCTION
import de.nordgedanken.auto_hotkey.psi.ext.tokenSetOf

val AHK_ITEMS = tokenSetOf(
        FUNCTION
)
val AHK_BLOCK_LIKE_EXPRESSIONS = tokenSetOf(BLOCK_EXPR)
