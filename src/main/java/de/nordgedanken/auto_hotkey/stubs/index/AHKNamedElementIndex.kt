package de.nordgedanken.auto_hotkey.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import de.nordgedanken.auto_hotkey.psi.AHKNamedElement
import de.nordgedanken.auto_hotkey.stubs.AHKFileStub

class AHKNamedElementIndex : StringStubIndexExtension<AHKNamedElement>() {
    override fun getVersion(): Int = AHKFileStub.Type.stubVersion
    override fun getKey(): StubIndexKey<String, AHKNamedElement> = KEY

    companion object {
        val KEY: StubIndexKey<String, AHKNamedElement> =
                StubIndexKey.createIndexKey("org.rust.lang.core.stubs.index.RustNamedElementIndex")

        fun findElementsByName(
                project: Project,
                target: String,
                scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
        ): Collection<AHKNamedElement> {
            return getElements(KEY, target, project, scope)
        }
    }
}

inline fun <Key, reified Psi : PsiElement> getElements(
        indexKey: StubIndexKey<Key, Psi>,
        key: Key, project: Project,
        scope: GlobalSearchScope?
): Collection<Psi> =
        StubIndex.getElements(indexKey, key, project, scope, Psi::class.java)
