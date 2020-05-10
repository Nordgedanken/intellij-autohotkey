package de.nordgedanken.auto_hotkey.psi.ext

import de.nordgedanken.auto_hotkey.psi.AHKBlock

interface AHKLabeledExpression : AHKElement {
    val block: AHKBlock?
}
