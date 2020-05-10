package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.lang.ASTNode
import de.nordgedanken.auto_hotkey.psi.AHKStmt

abstract class AHKStmtMixin(node: ASTNode) : AHKElementImpl(node), AHKStmt {
}
