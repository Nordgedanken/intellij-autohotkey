package de.nordgedanken.auto_hotkey.resolve.ref

import com.intellij.psi.PsiPolyVariantReference
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement


interface AHKReference : PsiPolyVariantReference {

    override fun getElement(): AHKElement

    override fun resolve(): AHKElement?

    fun multiResolve(): List<AHKElement>
}
