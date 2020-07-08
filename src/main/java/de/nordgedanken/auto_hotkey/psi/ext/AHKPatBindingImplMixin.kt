package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.search.SearchScope
import com.intellij.ui.LayeredIcon
import de.nordgedanken.auto_hotkey.psi.AHKPatBinding
import de.nordgedanken.auto_hotkey.resolve.ref.AHKPatBindingReferenceImpl
import de.nordgedanken.auto_hotkey.resolve.ref.AHKReference
import javax.swing.Icon

abstract class AHKPatBindingImplMixin(node: ASTNode) : AHKNamedElementImpl(node),
        AHKPatBinding {

    override fun getReference(): AHKReference = AHKPatBindingReferenceImpl(this)

    fun Icon.addFinalMark(): Icon = LayeredIcon(this, AllIcons.Nodes.FinalMark)
    override fun getIcon(flags: Int) = when {
        else -> AllIcons.Nodes.Parameter.addFinalMark()
    }

    override val referenceNameElement: PsiElement get() = nameIdentifier!!

    override fun getUseScope(): SearchScope {
        return super.getUseScope()
    }
}
